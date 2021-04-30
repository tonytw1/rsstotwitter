package nz.gen.wellington.rsstotwitter.twitter;

import com.google.common.base.Strings;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoTwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.timers.Updater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TwitterUpdater implements Updater {

    private final static Logger log = LogManager.getLogger(TwitterUpdater.class);

    private final MongoTwitterHistoryDAO twitterHistoryDAO;
    private final TwitterService twitterService;
    private final TweetFromFeedItemBuilder tweetFromFeedItemBuilder;

    @Autowired
    public TwitterUpdater(MongoTwitterHistoryDAO twitterHistoryDAO, TwitterService twitterService, TweetFromFeedItemBuilder tweetFromFeedItemBuilder) {
        this.twitterHistoryDAO = twitterHistoryDAO;
        this.twitterService = twitterService;
        this.tweetFromFeedItemBuilder = tweetFromFeedItemBuilder;
    }

    public void updateFeed(Feed feed, List<FeedItem> feedItems, TwitterAccount account) {
        log.info("Calling update feed for account '" + account.getUsername() + "' with " + feedItems.size() + " feed items");
        final long tweetsSentInLastHour = twitterHistoryDAO.getNumberOfTwitsInLastHour(feed, account.getId());
        final long tweetsSentInLastTwentyForHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, account.getId());
        log.info("Tweets sent in last hour: " + tweetsSentInLastHour);
        log.info("Tweets sent in last 24 hours: " + tweetsSentInLastTwentyForHours);

        long tweetsSentThisRound = 0;
        for (FeedItem feedItem : feedItems) {
            if (hasExceededMaxTweetsPerHourRateLimit(tweetsSentInLastHour + tweetsSentThisRound) || hasExceededMaxTweetsPerDayFeedRateLimit(tweetsSentInLastTwentyForHours + tweetsSentThisRound)) {
                log.info("Feed '" + feed.getUrl() + "' has exceeded maximum tweets per hour or day rate limit; returning");
                return;
            }

            boolean publisherRateLimitExceeded = isPublisherRateLimitExceed(feed, feedItem.getAuthor());
            if (!publisherRateLimitExceeded) {
                if (processItem(account, feedItem)) {
                    tweetsSentThisRound++;
                }
            } else {
                log.info("Publisher '" + feedItem.getAuthor() + "' has exceed the rate limit; skipping feeditem from this publisher");
            }
        }

        log.info("Twitter update completed for feed: " + feed.getUrl());
    }

    private boolean processItem(TwitterAccount account, FeedItem feedItem) {
        final String guid = feedItem.getGuid();

        final boolean isLessThanOneWeekOld = isLessThanOneWeekOld(feedItem);
        if (!isLessThanOneWeekOld) {
            log.debug("Not tweeting as the item's publication date is more than one week old: " + guid);
            return false;
        }

        if (!twitterHistoryDAO.hasAlreadyBeenTweeted(guid)) {
            try {
                final Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem);
                final Tweet updatedStatus = twitterService.tweet(tweet, account);
                if (updatedStatus != null) {
                    twitterHistoryDAO.markAsTweeted(feedItem, updatedStatus);
                    return true;
                }

            } catch (Exception e) {
                log.warn("Failed to tweet: " + feedItem.getTitle(), e);
            }

        } else {
            log.debug("Not tweeting as guid has already been tweeted: " + guid);
        }
        return false;
    }

    private boolean hasExceededMaxTweetsPerHourRateLimit(long tweetsSent) {
        return tweetsSent >= TwitterSettings.MAX_TWITS_PER_HOUR;
    }

    private boolean hasExceededMaxTweetsPerDayFeedRateLimit(long tweetsSent) {
        return tweetsSent >= TwitterSettings.MAX_TWITS_PER_DAY;
    }

    private boolean isPublisherRateLimitExceed(Feed feed, String publisher) {
        if (Strings.isNullOrEmpty(publisher)) {
            return false;
        }

        final int numberOfPublisherTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, publisher);
        log.debug("Publisher '" + publisher + "' has made " + numberOfPublisherTwitsInLastTwentyFourHours + " twits in the last 24 hours");
        return numberOfPublisherTwitsInLastTwentyFourHours >= TwitterSettings.MAX_PUBLISHER_TWITS_PER_DAY;
    }

    private boolean isLessThanOneWeekOld(FeedItem feedItem) {
        final DateTime sevenDaysAgo = new DateTime().minusDays(7);
        return new DateTime(feedItem.getPublishedDate()).isAfter(sevenDaysAgo);
    }

}
