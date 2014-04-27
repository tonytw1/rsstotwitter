package nz.gen.wellington.rsstotwitter.twitter;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.timers.Updater;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class TwitterUpdater implements Updater {
	
	private static Logger log = Logger.getLogger(TwitterUpdater.class);
	
	private static final int MAX_TWITS_PER_DAY = 50;
	private static final int MAX_PUBLISHER_TWITS_PER_DAY = MAX_TWITS_PER_DAY;
	
	private TwitterHistoryDAO twitterHistoryDAO;
	private TwitterService twitterService;
    private TweetDAO tweetDAO;
    private TweetFromFeedItemBuilder tweetFromFeedItemBuilder;
    
	public TwitterUpdater(TwitterHistoryDAO twitterHistoryDAO, TwitterService twitterService, TweetDAO tweetDAO, TweetFromFeedItemBuilder tweetFromFeedItemBuilder) {
		this.twitterHistoryDAO = twitterHistoryDAO;
		this.twitterService = twitterService;
		this.tweetDAO = tweetDAO;
		this.tweetFromFeedItemBuilder = tweetFromFeedItemBuilder;
	}

	public void updateFeed(List<FeedItem> feedItems, TwitterAccount account, String tag) {
		log.info("Calling update feed for account '" + account.getUsername() + "' with " + feedItems.size() + " feed items");
		Feed feed = feedItems.get(0).getFeed();	// TODO Meh - drops out when we move to account rate limiting.
        int tweetsSent = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed);	// TODO rate limit should really be about the twitter account, not the feed.
        if (hasExceededFeedRateLimit(tweetsSent)) {
        	log.info("Feed '" + feed.getUrl() + "' has exceeded rate limit; skipping");
        	return;
        }
        
        for (FeedItem feedItem : feedItems) {
			if (hasExceededFeedRateLimit(tweetsSent)) {
				return;
			}
		        			
        	boolean publisherRateLimitExceeded = isPublisherRateLimitExceed(feed, feedItem.getAuthor());        			      			
        	if (!publisherRateLimitExceeded) {        				
	        	if (processItem(feedItem, account, tag)) {
	        		tweetsSent++;
	        	}	        			                                             	        			
        	} else {
        		log.info("Publisher '" + feedItem.getAuthor() + "' has exceed the rate limit");
        	}
        }
        	
        log.info("Twitter update completed for feed: " + feed.getUrl());
	}
   	
	private boolean processItem(FeedItem feedItem, TwitterAccount account, String tag) {
		final String guid = feedItem.getGuid();
		
		final boolean isLessThanOneWeekOld = isLessThanOneWeekOld(feedItem);
		if (!isLessThanOneWeekOld) {
			log.debug("Not tweeting as the item's publication date is more than one week old: " + guid);
			return false;
		}
		
		final boolean hasAlreadyBeenTwittered = twitterHistoryDAO.hasAlreadyBeenTwittered(guid);
		if (!hasAlreadyBeenTwittered) {			
			Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem, tag);
			Tweet sentTweet = twitterService.tweet(tweet, account);
			if (sentTweet != null) {
				tweetDAO.saveTweet(sentTweet);
				twitterHistoryDAO.markAsTwittered(feedItem, sentTweet);
				return true;
				
			} else {
				log.warn("Failed to twitter: " + tweet.getText());
				return false;
			}
			
		} else {
			log.debug("Not twittering as guid has already been twittered: " + guid);
		}
		
		return false;
	}
	
	private boolean hasExceededFeedRateLimit(int tweetsSent) {
		return tweetsSent >= MAX_TWITS_PER_DAY;
	}
	
    private boolean isPublisherRateLimitExceed(Feed feed, String publisher) {
    	if (publisher != null && !publisher.isEmpty()) {
    		final int numberOfPublisherTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, publisher);
    		log.debug("Publisher '" + publisher + "' has made " + numberOfPublisherTwitsInLastTwentyFourHours + " twits in the last 24 hours");
    		return numberOfPublisherTwitsInLastTwentyFourHours >= MAX_PUBLISHER_TWITS_PER_DAY;
    	}    	
		return false;
	}

	private boolean isLessThanOneWeekOld(FeedItem feedItem) {
        final DateTime sevenDaysAgo = new DateTime().minusDays(7);
        return new DateTime(feedItem.getPublishedDate()).isAfter(sevenDaysAgo);        
    }

}
