package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import net.unto.twitter.TwitterProtos.Status;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.FeedDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;
import nz.gen.wellington.twitter.TwitBuilderService;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class TwitterUpdateService {
    
    private static final int MAX_TWITS_PER_DAY = 30;
	private static final int MAX_PUBLISHER_TWITS_PER_DAY = 5;
    
	Logger log = Logger.getLogger(TwitterUpdateService.class);

	private FeedDAO feedDAO;
	private TwitterHistoryDAO twitterHistoryDAO;
	private TwitBuilderService twitBuilderService;
	private TwitterService twitterService;
    private TwitteredFeedDAO twitteredFeedDAO;
    private TweetDAO tweetDAO;
     
	public TwitterUpdateService(FeedDAO feedDAO, TwitterHistoryDAO twitterHistoryDAO, TwitBuilderService twitBuilderService, TwitterService twitterService, TwitteredFeedDAO twitteredFeedDAO, TweetDAO tweetDAO) {
		this.feedDAO = feedDAO;		
		this.twitterHistoryDAO = twitterHistoryDAO;
        this.twitBuilderService = twitBuilderService;
        this.twitterService = twitterService;
        this.twitteredFeedDAO = twitteredFeedDAO;
        this.tweetDAO = tweetDAO;
	}
    
    public void run() {       
        List<TwitteredFeed> feeds = twitteredFeedDAO.getAllFeeds();
        for (TwitteredFeed feed : feeds) {
            updateFeed(feed);            
        }
    }
    
    
	public void updateFeed(TwitteredFeed feed) {
        log.info("Running twitter update for: " + feed.getUrl());
        
        boolean rateLimitExceeded = isRateLimitExceeded(feed);
        if (!rateLimitExceeded) {

        	final SyndFeed syndfeed = feedDAO.loadSyndFeedWithFeedFetcher(feed.getUrl());
        	if (syndfeed != null) {
        		final List<SyndEntry> feedItems = syndfeed.getEntries();
        		
        		for (SyndEntry feedItem : feedItems) {
        			
        			boolean publisherRateLimitExceeded = isPublisherRateLimitExceed(feed, feedItem.getAuthor());        			      			
        			if (!publisherRateLimitExceeded) {
        				
	        			boolean isLessThanOneWeekOld = isLessThanOneWeekOld(feedItem);              
	        			final String guid = feedItem.getUri();
	        			boolean hasBeenTwitteredAlready = twitterHistoryDAO.hasAlreadyBeenTwittered(guid);
	        			
	        			if (isLessThanOneWeekOld && !hasBeenTwitteredAlready) {
	        				final String twit = twitBuilderService.buildTwitForItem(feedItem.getTitle(), feedItem.getLink(), feedItem.getAuthor(), feed.getTwitterTag());
	        				Status sentPost = twitterService.twitter(twit, feed.getAccount().getUsername(), feed.getAccount().getPassword());
							if (sentPost != null) {
								Tweet sentTweet = new Tweet(sentPost);
								tweetDAO.saveTweet(sentTweet);
	        					twitterHistoryDAO.markAsTwittered(guid, twit, feedItem.getAuthor(), feed, sentTweet);
	        					
	        				} else {
	        					log.warn("Failed to twitter: " + twit);
	        				}
	        			} else {
	        				log.info("Not twittering as guid has already been twittered or is more than a week old: " + guid);
	        			}
	        			
        			} else {
        				log.info("Publisher '" + feedItem.getAuthor() + "' has exceed the rate limit");
        			}
        			
        		}
        		
        	} else {
        		log.warn("Could not load feed from url: " + feed.getUrl());
        	}
        	
        } else {
        	log.info("Feed '" + feed.getUrl() + "' has exceeded rate limit; skipping");
        }
        log.info("Twitter update completed");
	}

    
    private boolean isPublisherRateLimitExceed(TwitteredFeed feed, String publisher) {
    	if (publisher != null && !publisher.isEmpty()) {
    		final int numberOfPublisherTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, publisher);
    		log.info("Publisher '" + publisher + "' has made " + numberOfPublisherTwitsInLastTwentyFourHours + " twits in the last 24 hours");
    		return numberOfPublisherTwitsInLastTwentyFourHours >= MAX_PUBLISHER_TWITS_PER_DAY;
    	}    	
		return false;
	}

	private boolean isRateLimitExceeded(TwitteredFeed feed) {
    	final int numberOfTwitsInLastTwentyFourHours = twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed);
    	log.info("Feed '" + feed.getUrl() + "' has made " + numberOfTwitsInLastTwentyFourHours + " twits in the last 24 hours");
    	return numberOfTwitsInLastTwentyFourHours >= MAX_TWITS_PER_DAY;
	}
    

	private boolean isLessThanOneWeekOld(SyndEntry feedItem) {
        final DateTime sevenDaysAgo = new DateTime().minusDays(7);
        return new DateTime(feedItem.getPublishedDate()).isAfter(sevenDaysAgo);        
    }
    
}
