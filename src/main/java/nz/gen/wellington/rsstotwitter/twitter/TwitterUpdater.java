package nz.gen.wellington.rsstotwitter.twitter;

import com.google.common.base.Strings;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterHistoryDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TwitterUpdater {

    private final static Logger log = LogManager.getLogger(TwitterUpdater.class);

    private final TwitterHistoryDAO twitterHistoryDAO;
    private final TwitterService twitterService;
    private final TweetFromFeedItemBuilder tweetFromFeedItemBuilder;
    private final MastodonService mastodonService;

    @Autowired
    public TwitterUpdater(TwitterHistoryDAO twitterHistoryDAO, TwitterService twitterService, TweetFromFeedItemBuilder tweetFromFeedItemBuilder,
                          MastodonService mastodonService) {
        this.twitterHistoryDAO = twitterHistoryDAO;
        this.twitterService = twitterService;
        this.tweetFromFeedItemBuilder = tweetFromFeedItemBuilder;
        this.mastodonService = mastodonService;
    }

    public void updateFeed(Account account, Feed feed, List<FeedItem> feedItems, Destination destination) {
        log.info("Calling update feed for account '" + account.getUsername() + "' to " + destination + " with " + feedItems.size() + " feed items");
        final long tweetsSentInLastHour = twitterHistoryDAO.getNumberOfTwitsInLastHour(feed, account);
        final long tweetsSentInLastTwentyForHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, account);
        log.info("Sent to " + destination + " in last hour: " + tweetsSentInLastHour);
        log.info("Sent to " + destination + " in last 24 hours: " + tweetsSentInLastTwentyForHours);

        long sentThisRound = 0;
        for (FeedItem feedItem : feedItems) {
            if (hasExceededMaxTweetsPerHourRateLimit(tweetsSentInLastHour + sentThisRound) || hasExceededMaxTweetsPerDayFeedRateLimit(tweetsSentInLastTwentyForHours + sentThisRound)) {
                log.info("Feed '" + feed.getUrl() + "' has exceeded maximum per hour or day rate limit; returning");
                return;
            }

            boolean publisherRateLimitExceeded = isPublisherRateLimitExceed(feed, feedItem.getAuthor(), account);
            if (!publisherRateLimitExceeded) {
                if (processItem(account, feedItem, destination)) {
                    sentThisRound++;
                }

            } else {
                log.info("Publisher '" + feedItem.getAuthor() + "' has exceed the rate limit; skipping feed item from this publisher");
            }
        }

        log.info("Update to " + destination + " completed for feed: " + feed.getUrl());
    }

    private boolean processItem(Account account, FeedItem feedItem, Destination destination) {
        final String guid = feedItem.getGuid();

        final boolean isLessThanOneWeekOld = isLessThanOneWeekOld(feedItem);
        if (!isLessThanOneWeekOld) {
            log.debug("Not tweeting as the item's publication date is more than one week old: " + guid);    // TODO push up
            return false;
        }

        if (!twitterHistoryDAO.hasAlreadyBeenTweeted(account, guid, destination)) {
            try {
                final Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem);

                Tweet updatedStatus = null;
                if (destination == Destination.TWITTER) {
                    updatedStatus = twitterService.tweet(tweet, account);
                }
                if (destination == Destination.MASTODON) {
                    updatedStatus = mastodonService.post(tweet.getText());
                }
                if (updatedStatus != null) {
                    twitterHistoryDAO.markAsTweeted(account, feedItem, updatedStatus, destination);
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

    private boolean isPublisherRateLimitExceed(Feed feed, String publisher, Account account) {
        if (Strings.isNullOrEmpty(publisher)) {
            return false;
        }

        final int numberOfPublisherTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, publisher, account);
        log.debug("Publisher '" + publisher + "' has made " + numberOfPublisherTwitsInLastTwentyFourHours + " twits in the last 24 hours");
        return numberOfPublisherTwitsInLastTwentyFourHours >= TwitterSettings.MAX_PUBLISHER_TWITS_PER_DAY;
    }

    private boolean isLessThanOneWeekOld(FeedItem feedItem) {
        final DateTime sevenDaysAgo = new DateTime().minusDays(7);
        return new DateTime(feedItem.getPublishedDate()).isAfter(sevenDaysAgo);
    }

}
