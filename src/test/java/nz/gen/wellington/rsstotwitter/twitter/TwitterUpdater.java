package nz.gen.wellington.rsstotwitter.twitter;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.timers.UpdateService;
import nz.gen.wellington.rsstotwitter.timers.Updater;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class TwitterUpdater implements Updater {
	
	private static Logger log = Logger.getLogger(UpdateService.class);

	private static final int MAX_TWITS_PER_DAY = 30;
	private static final int MAX_PUBLISHER_TWITS_PER_DAY = 5;
	
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

	public void updateFeed(TwitteredFeed feed, List<FeedItem> feedItems) {
		if (feedItems == null) {
			log.warn("Could not load feed from url: " + feed.getUrl());
			return;
		}
		
        log.info("Running twitter update for: " + feed.getUrl());
                
        int tweetsSent = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed);
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
	        	if (processItem(feed, feedItem)) {
	        		tweetsSent++;
	        	}
	        			                                             	        			
        	} else {
        		log.info("Publisher '" + feedItem.getAuthor() + "' has exceed the rate limit");
        	}        			
        }
        	
        log.info("Twitter update completed for feed: " + feed.getUrl());
	}
   	
	private boolean processItem(TwitteredFeed feed, FeedItem feedItem) {
		final String guid = feedItem.getGuid();
		if (isLessThanOneWeekOld(feedItem) && !twitterHistoryDAO.hasAlreadyBeenTwittered(guid)) {			
			Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem, feed.getTwitterTag());
			Tweet sentTweet = twitterService.twitter(tweet, feed.getAccount());
			if (sentTweet != null) {
				tweetDAO.saveTweet(sentTweet);
				twitterHistoryDAO.markAsTwittered(feedItem, feed, sentTweet);
				return true;
				
			} else {
				log.warn("Failed to twitter: " + tweet.getText());
			}
			
		} else {
			log.info("Not twittering as guid has already been twittered or is more than a week old: " + guid);
		}
		return false;
	}
	
	private boolean hasExceededFeedRateLimit(int tweetsSent) {
		return tweetsSent >= MAX_TWITS_PER_DAY;
	}
	
    private boolean isPublisherRateLimitExceed(TwitteredFeed feed, String publisher) {
    	if (publisher != null && !publisher.isEmpty()) {
    		final int numberOfPublisherTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, publisher);
    		log.info("Publisher '" + publisher + "' has made " + numberOfPublisherTwitsInLastTwentyFourHours + " twits in the last 24 hours");
    		return numberOfPublisherTwitsInLastTwentyFourHours >= MAX_PUBLISHER_TWITS_PER_DAY;
    	}    	
		return false;
	}

	private boolean isLessThanOneWeekOld(FeedItem feedItem) {
        final DateTime sevenDaysAgo = new DateTime().minusDays(7);
        return new DateTime(feedItem.getPublishedDate()).isAfter(sevenDaysAgo);        
    }

}
