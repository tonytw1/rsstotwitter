package nz.gen.wellington.rsstotwitter.twitter;

import com.google.common.base.Strings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.repositories.mongo.AccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.auth.OAuth2Authorization;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class TwitterService {

    private final static Logger log = LogManager.getLogger(TwitterService.class);

    private final String consumerKey;
    private final String consumerSecret;
    private final String clientId;

    private final AccountDAO accountDAO;
    private final Counter tweetedCounter;

    @Autowired
    public TwitterService(@Value("${twitter.consumer.key}") String consumerKey,
                          @Value("${twitter.consumer.secret}") String consumerSecret,
                          @Value("${twitter.oauth2.client.id}") String clientId,
                          AccountDAO accountDAO,
                          MeterRegistry meterRegistry) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.clientId = clientId;
        this.accountDAO = accountDAO;
        this.tweetedCounter = meterRegistry.counter("tweeted");
    }

    public String getAuthorizeUrl(String callbackUrl) {
        twitter4j.conf.ConfigurationBuilder cb = new twitter4j.conf.ConfigurationBuilder().
                setOAuthConsumerKey(consumerKey).
                setOAuthConsumerSecret(consumerSecret);

        OAuth2TokenProvider oAuth2TokenProvider = new OAuth2TokenProvider(cb.build());
        String[] writeScopes = {"tweet.read", "users.read", "tweet.write", "offline.access"};
        return oAuth2TokenProvider.createAuthorizeUrl(
                clientId,
                callbackUrl,
                writeScopes,
                "challenge"
        );
    }

    public OAuth2TokenProvider.Result getOAuth2Token(String code, String callbackUrl) {
        twitter4j.conf.ConfigurationBuilder cb = new twitter4j.conf.ConfigurationBuilder().
                setDebugEnabled(true).
                setOAuthConsumerKey(consumerKey).
                setOAuthConsumerSecret(consumerSecret);

        OAuth2TokenProvider oAuth2TokenProvider = new OAuth2TokenProvider(cb.build());

        return oAuth2TokenProvider.getAccessToken(clientId, callbackUrl, code, "challenge");
    }

    public UsersResponse getTwitterUserCredentials(String accessToken) {
        try {
            Twitter twitterApi = getAuthenticatedApiForAccessToken(accessToken);
            final TwitterV2 v2 = TwitterV2ExKt.getV2(twitterApi);
            return v2.getMe("",
                    V2DefaultFields.tweetFields,
                    V2DefaultFields.userFields
            );

        } catch (TwitterException e) {
            log.warn("Failed up obtain twitter user details due to Twitter exception: " + e.getMessage());
            return null;
        }
    }

    public Tweet tweet(Tweet tweet, Account account) {
        log.info("Attempting to tweet: " + tweet.getText());
        final Twitter twitterApiForAccount = getAuthenticatedApiForAccount(account);

        try {
            final CreateTweetResponse status = createTweet(twitterApiForAccount, tweet);
            tweetedCounter.increment();

            // The v2 response is really sparse, so we have to make approximations =(
            DateTime created = DateTime.now();
            return new Tweet(status.getId(), account.getId(), created.toDate(), status.getText(), account.getUsername());

        } catch (TwitterException e) {
            log.warn("A TwitterException occurred while trying to tweet: " + e.getMessage());
            return null;
        }
    }

    private CreateTweetResponse createTweet(Twitter twitterApi, Tweet tweet) throws TwitterException {
        final TwitterV2 v2 = TwitterV2ExKt.getV2(twitterApi);
        return v2.createTweet(null, null, null,
                null, null, null,
                null, null, null, null, null,
                tweet.getText());
    }


    private Twitter getAuthenticatedApiForAccount(Account account) {
        Twitter twitterApiForAccount = getAuthenticatedApiForAccessToken(account.getTwitterAccessToken());
        if (twitterApiForAccount == null) {
            throw new RuntimeException("Could not get api instance for account: " + account.getUsername());    // TODO is a null return really what twitter4j returns?
        }
        return twitterApiForAccount;
    }

    private Twitter getAuthenticatedApiForAccessToken(String accessToken) {
        Configuration conf = new ConfigurationBuilder().build();
        OAuth2Authorization oAuth2Authorization = new OAuth2Authorization(conf);
        oAuth2Authorization.setOAuth2Token(new OAuth2Token("bearer", accessToken));
        return new TwitterFactory(conf).getInstance(oAuth2Authorization);
    }

    public boolean isReadyToPublishFor(Account account) {
        String twitterAccessToken = account.getTwitterAccessToken();
        if (twitterAccessToken == null) {
            log.info("Twitter access token is null for: " + account.getUsername());
            return false;
        }

        try {
            // Check that the access token is valid
            Twitter twitterApi = getAuthenticatedApiForAccessToken(twitterAccessToken);
            final TwitterV2 v2 = TwitterV2ExKt.getV2(twitterApi);
            UsersResponse me = v2.getMe("",
                    V2DefaultFields.tweetFields,
                    V2DefaultFields.userFields
            );
            log.info("Twitter is ready to publish for: " + me.getUsers().get(0));
            return true;

        } catch (TwitterException tw) {
            log.warn("Twitter is not ready to publish: " + tw.getErrorCode() + " " + tw.getStatusCode() + " " + tw.getExceptionCode() + " / " + tw.getMessage());
            String twitterRefreshToken = account.getTwitterRefreshToken();
            if (tw.getStatusCode() == 401 && twitterRefreshToken != null) {
                // Attempt to refresh the access token
                log.info("Attempting to refresh access token for account: " + account.getUsername());

                try {
                    twitter4j.conf.ConfigurationBuilder cb = new twitter4j.conf.ConfigurationBuilder().
                            setDebugEnabled(true).
                            setOAuthConsumerKey(consumerKey).
                            setOAuthConsumerSecret(consumerSecret);

                    OAuth2TokenProvider oAuth2TokenProvider = new OAuth2TokenProvider(cb.build());

                    OAuth2TokenProvider.Result result = oAuth2TokenProvider.refreshToken(clientId, twitterRefreshToken);
                    if (result != null) {
                        log.info("Refreshed access token for account: " + result);
                        account.setTwitterAccessToken(result.getAccessToken());
                        account.setTwitterRefreshToken(result.getRefreshToken());
                        accountDAO.saveAccount(account);
                        return true;
                    } else {
                        log.warn("Failed to refresh access token for account: " + account.getUsername());
                        return false;
                    }

                } catch (Exception e) {
                    log.warn("Failed to refresh access token for account: " + account.getUsername() + " due to exception: " + e.getMessage());
                    return false;
                }

            } else {
                log.warn("Twitter is not ready to publish for account: " + account.getUsername() + " and no refresh token is available");
                return false;
            }
        }
    }

    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(consumerKey) && !Strings.isNullOrEmpty(consumerSecret);
    }

}
