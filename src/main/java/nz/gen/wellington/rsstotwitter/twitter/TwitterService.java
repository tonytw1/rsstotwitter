package nz.gen.wellington.rsstotwitter.twitter;

import com.google.common.collect.Lists;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
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

    private final static Logger log = Logger.getLogger(TwitterService.class);

    private final static int REPLY_PAGES_TO_FETCH = 1;

    private String consumerKey;
    private String consumerSecret;

    private Counter tweetedCounter;

    @Autowired
    public TwitterService(@Value("${consumer.key}") String consumerKey, @Value("${consumer.secret}") String consumerSecret,
                          MeterRegistry meterRegistry) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.tweetedCounter = meterRegistry.counter("tweeted");
    }

    public Tweet tweet(Tweet tweet, TwitterAccount account) {
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

    public List<Status> getReplies(TwitterAccount account) {
        log.info("Getting twitter replies from live api");
        final Twitter twitter = getAuthenticatedApiForAccount(account);
        final List<Status> all = Lists.newArrayList();
        for (int i = 1; i <= REPLY_PAGES_TO_FETCH; i++) {
            try {
                all.addAll(twitter.getMentionsTimeline(new Paging(i)));
            } catch (TwitterException e) {
                log.warn("A TwitterException occured while trying to fetch mentions: " + e.getMessage());
            }
        }
        return all;
    }

    public List<Long> getFollowers(TwitterAccount account) {
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

    public List<Long> getFriends(TwitterAccount account) {
        final Twitter twitter = getAuthenticatedApiForAccount(account);
        try {
            IDs friendIds = twitter.getFriendsIDs((account.getId()));
            log.info("Found " + friendIds.getIDs().length + " friend ids");
            return Arrays.asList(ArrayUtils.toObject(friendIds.getIDs()));

        } catch (TwitterException e) {
            log.error("Error while fetching friends of '" + account.getUsername() + "'" + e.getMessage());
        }
        return null;
    }

    public boolean follow(TwitterAccount account, long userId) throws TwitterException {
        Twitter twitter = getAuthenticatedApiForAccount(account);
        try {
            User followed = twitter.createFriendship(userId, true);
            if (followed != null) {
                log.info("Followed: " + followed.getScreenName());
                return true;
            } else {
                log.warn("Failed to follow");
            }
        } catch (Exception e) {
            log.error("Error while attempting to follow: " + e.getMessage());
        }
        return false;
    }

    public twitter4j.User getTwitteUserCredentials(AccessToken accessToken) {
        Twitter twitterApi = getAuthenticatedApiForAccessToken(accessToken);
        try {
            return twitterApi.verifyCredentials();
        } catch (TwitterException e) {
            log.warn("Failed up obtain twitter user details due to Twitter exception: " + e.getMessage());
            return null;
        }
    }

    public ResponseList<User> getUserDetails(List<Long> userIds, TwitterAccount account) throws TwitterException {
        final Twitter twitter = getAuthenticatedApiForAccount(account);
        return twitter.lookupUsers(ArrayUtils.toPrimitive(userIds.toArray(new Long[userIds.size()])));
    }

    private Status updateStatus(Twitter twitter, Tweet tweet) throws TwitterException {
        log.info("Twittering: " + tweet.getText() + ", location: " + tweet.getGeoLocation());
        StatusUpdate statusUpdate = new StatusUpdate(tweet.getText());
        if (tweet.getGeoLocation() != null) {
            statusUpdate.setLocation(tweet.getGeoLocation());
        }
        return twitter.updateStatus(statusUpdate);
    }

    private Twitter getAuthenticatedApiForAccount(TwitterAccount account) {
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
