package nz.gen.wellington.rsstotwitter.twitter;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.common.base.Strings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Status;
import twitter4j.v1.StatusUpdate;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TwitterService {

    private final static Logger log = LogManager.getLogger(TwitterService.class);

    private final String consumerKey;
    private final String consumerSecret;

    private final Counter tweetedCounter;

    @Autowired
    public TwitterService(@Value("${twitter.consumer.key}") String consumerKey,
                          @Value("${twitter.consumer.secret}") String consumerSecret,
                          MeterRegistry meterRegistry) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.tweetedCounter = meterRegistry.counter("tweeted");
    }

    public Tweet tweet(Tweet tweet, Account account) {
        log.info("Attempting to tweet: " + tweet.getText());
        final Twitter twitterApiForAccount = getAuthenticatedApiForAccount(account);
        try {
            final Status status = updateStatus(twitterApiForAccount, tweet);
            tweetedCounter.increment();

            // v1 createdAt is "UTC time when this Tweet was created."
            LocalDateTime createdAt = status.getCreatedAt();
            DateTime time = new DateTime(createdAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli(), DateTimeZone.UTC);
            return new Tweet(status.getId(), status.getUser().getId(), time.toDate(), status.getText(), status.getUser().getScreenName());

        } catch (TwitterException e) {
            log.warn("A TwitterException occurred while trying to tweet: " + e.getMessage());
        }
        return null;
    }

    public twitter4j.v1.User getTwitterUserCredentials(String accessTokenKey, String accessTokenSecret) {
        Twitter twitterApi = getAuthenticatedApiForAccessToken(accessTokenKey, accessTokenSecret);
        try {
            return twitterApi.v1().users().verifyCredentials();

        } catch (TwitterException e) {
            log.warn("Failed up obtain twitter user details due to Twitter exception: " + e.getMessage());
            return null;
        }
    }

    public OAuth10aService makeOauthService(String callBackUrl) {
        log.info("Building oauth service with consumer key and consumer secret: " + consumerKey + ":" + consumerSecret);
        log.info("Oauth callback url is: " + callBackUrl);
        return new ServiceBuilder(consumerKey).apiSecret(consumerSecret).callback(callBackUrl).build(TwitterApi.instance());
    }

    private Status updateStatus(Twitter twitter, Tweet tweet) throws TwitterException {
        StatusUpdate statusUpdate = StatusUpdate.of(tweet.getText());
        return twitter.v1().tweets().updateStatus(statusUpdate);
    }

    private Twitter getAuthenticatedApiForAccount(Account account) {
        Twitter twitterApiForAccount = getAuthenticatedApiForAccessToken(account.getToken(), account.getTokenSecret());
        if (twitterApiForAccount == null) {
            throw new RuntimeException("Could not get api instance for account: " + account.getUsername());    // TODO is a null return really what twitter4j returns?
        }
        return twitterApiForAccount;
    }

    private Twitter getAuthenticatedApiForAccessToken(String accessKeyToken, String accessKeySecret) {
        return Twitter.newBuilder().
                oAuthConsumer(consumerKey, consumerSecret).
                oAuthAccessToken(accessKeyToken, accessKeySecret).
                build();
    }

    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(consumerKey) && !Strings.isNullOrEmpty(consumerSecret);
    }

}
