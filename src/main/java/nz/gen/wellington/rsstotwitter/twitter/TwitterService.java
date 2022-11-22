package nz.gen.wellington.rsstotwitter.twitter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.Account;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Arrays;
import java.util.List;

@Component
public class TwitterService {

    private final static Logger log = LogManager.getLogger(TwitterService.class);

    private final String consumerKey;
    private final String consumerSecret;

    private final Counter tweetedCounter;

    @Autowired
    public TwitterService(@Value("${consumer.key}") String consumerKey, @Value("${consumer.secret}") String consumerSecret,
                          MeterRegistry meterRegistry) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.tweetedCounter = meterRegistry.counter("tweeted");
    }

    public Tweet tweet(Tweet tweet, Account account) {
        log.info("Attempting to tweet: " + tweet.getText());
        final Twitter twitterApiForAccount = getAuthenticatedApiForAccount(account);
        try {
            final Status updatedStatus = updateStatus(twitterApiForAccount, tweet);
            tweetedCounter.increment();
            return new Tweet(updatedStatus);
        } catch (TwitterException e) {
            log.warn("A TwitterException occured while trying to tweet: " + e.getMessage());
        }
        return null;
    }

    public List<Long> getFollowers(Account account) {
        final Twitter twitter = getAuthenticatedApiForAccount(account);
        try {
            IDs followersIDs = twitter.getFollowersIDs(account.getId());
            log.info("Found " + followersIDs.getIDs().length + " follower ids");
            return Arrays.asList(ArrayUtils.toObject(followersIDs.getIDs()));

        } catch (TwitterException e) {
            log.error("Error while fetching follows of '" + account.getUsername() + "'", e);
        }
        return null;
    }

    public twitter4j.User getTwitterUserCredentials(AccessToken accessToken) {
        Twitter twitterApi = getAuthenticatedApiForAccessToken(accessToken);
        try {
            return twitterApi.verifyCredentials();
        } catch (TwitterException e) {
            log.warn("Failed up obtain twitter user details due to Twitter exception: " + e.getMessage());
            return null;
        }
    }

    private Status updateStatus(Twitter twitter, Tweet tweet) throws TwitterException {
        log.info("Tweeting: " + tweet.getText() + ", location: " + tweet.getGeoLocation());
        StatusUpdate statusUpdate = new StatusUpdate(tweet.getText());
        if (tweet.getGeoLocation() != null) {
            statusUpdate.setLocation(tweet.getGeoLocation());
        }
        return twitter.updateStatus(statusUpdate);
    }

    private Twitter getAuthenticatedApiForAccount(Account account) {
        Twitter twitterApiForAccount = getAuthenticatedApiForAccessToken(new AccessToken(account.getToken(), account.getTokenSecret()));
        if (twitterApiForAccount == null) {
            throw new RuntimeException("Could not get api instance for account: " + account.getUsername());    // TODO is a null return really what twitter4j returns?
        }
        return twitterApiForAccount;
    }

    private Twitter getAuthenticatedApiForAccessToken(AccessToken accessToken) {
        final ConfigurationBuilder configBuilder = new ConfigurationBuilder().
                setOAuthConsumerKey(consumerKey).
                setOAuthConsumerSecret(consumerSecret);
        return new TwitterFactory(configBuilder.build()).getInstance(accessToken);
    }

}
