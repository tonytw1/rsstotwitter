package nz.gen.wellington.rsstotwitter.twitter;

import com.google.common.base.Strings;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterHistoryDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
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
        final long tweetsSentInLastHour = twitterHistoryDAO.getNumberOfTwitsInLastHour(feed, account, destination);
        final long tweetsSentInLastTwentyForHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, account, destination);
        log.info("Sent to " + destination + " in last hour: " + tweetsSentInLastHour);
        log.info("Sent to " + destination + " in last 24 hours: " + tweetsSentInLastTwentyForHours);

        long sentThisRound = 0;
        for (FeedItem feedItem : feedItems) {
            if (hasExceededMaxTweetsPerHourRateLimit(tweetsSentInLastHour + sentThisRound) || hasExceededMaxTweetsPerDayFeedRateLimit(tweetsSentInLastTwentyForHours + sentThisRound)) {
                log.info("Feed '" + feed.getUrl() + "' has exceeded maximum per hour or day rate limit; returning");
                return;
            }

            final boolean isFreshEnough = isLessThanSevenDaysOld(feedItem);
            if (isFreshEnough) {
                boolean publisherRateLimitExceeded = isPublisherRateLimitExceed(feed, feedItem.getAuthor(), account, destination);
                if (!publisherRateLimitExceeded) {
                    if (processItem(account, feedItem, destination)) {
                        sentThisRound++;
                    }
                } else {
                    log.info("Publisher '" + feedItem.getAuthor() + "' has exceed the rate limit; skipping feed item from this publisher");
                }

            } else {
                log.debug("Not tweeting item which is not fresh enough: " + feedItem.getGuid());
            }
        }

        log.info("Update to " + destination + " completed for feed: " + feed.getUrl());
    }

    private boolean processItem(Account account, FeedItem feedItem, Destination destination) {
        final String guid = feedItem.getGuid();

        if (!twitterHistoryDAO.hasAlreadyBeenTweeted(account, guid, destination)) {
            try {
                final Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem);
                if (tweet != null) {
                    Tweet updatedStatus = null;
                    if (destination == Destination.TWITTER) {
                        if (isAccountContentedToTwitter(account)) {
                            log.info("Tweeting: " + tweet.getText());
                            updatedStatus = twitterService.tweet(tweet, account);
                        }
                    }
                    if (destination == Destination.MASTODON) {
                        if (isAccountConnectedToMastdon(account)) {
                            log.info("Tooting: " + tweet.getText());
                            updatedStatus = mastodonService.post(account.getMastodonAccessToken(), tweet.getText());
                            // Mastodon statuses are returned as HTML; step down to plain text
                            String plainText = Jsoup.parse(updatedStatus.getText()).text();
                            updatedStatus.setText(plainText);
                        }
                    }

                    if (updatedStatus != null) {
                        twitterHistoryDAO.markAsTweeted(account, feedItem, updatedStatus, destination);
                        return true;
                    }

                } else {
                    // TODO do we really need to impose the same formatting restrictions on Twitter and Mastodon?
                    log.warn("Could not compose tweet for feeditem: " + feedItem);
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
        return tweetsSent >= RateLimitingSettings.MAX_TWITS_PER_HOUR;
    }

    private boolean hasExceededMaxTweetsPerDayFeedRateLimit(long tweetsSent) {
        return tweetsSent >= RateLimitingSettings.MAX_TWITS_PER_DAY;
    }

    private boolean isPublisherRateLimitExceed(Feed feed, String publisher, Account account, Destination destination) {
        if (Strings.isNullOrEmpty(publisher)) {
            return false;
        }

        final long numberOfPublisherTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfPublisherTwitsInLastTwentyFourHours(feed, publisher, account, destination);
        log.debug("Publisher '" + publisher + "' has made " + numberOfPublisherTwitsInLastTwentyFourHours + " twits in the last 24 hours");
        return numberOfPublisherTwitsInLastTwentyFourHours >= RateLimitingSettings.MAX_PUBLISHER_TWITS_PER_DAY;
    }

    private boolean isLessThanSevenDaysOld(FeedItem feedItem) {
        final DateTime sevenDaysAgo = new DateTime().minusDays(7);
        return new DateTime(feedItem.getPublishedDate()).isAfter(sevenDaysAgo);
    }

    private boolean isAccountConnectedToMastdon(Account account) {
        return account.getMastodonAccessToken() != null;
    }

    private boolean isAccountContentedToTwitter(Account account) {
        return account.getToken() != null && account.getTokenSecret() != null;
    }

}
