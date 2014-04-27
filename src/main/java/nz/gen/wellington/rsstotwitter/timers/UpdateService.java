package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.feeds.FeedDAO;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;

import org.apache.log4j.Logger;

public class UpdateService implements Runnable {
	
	private final static Logger log = Logger.getLogger(UpdateService.class);

    private FeedToTwitterJobDAO feedToTwitterJobDAO;
    private FeedDAO feedDAO;
    private Updater twitterUpdater;
    
	public UpdateService(FeedToTwitterJobDAO tweetFeedJobDAO, FeedDAO feedDAO, Updater twitterUpdater) {
		this.feedToTwitterJobDAO = tweetFeedJobDAO;
		this.feedDAO = feedDAO;
		this.twitterUpdater = twitterUpdater;
	}
	
	public void run() {
		log.info("Starting feed to twitter update.");
        List<FeedToTwitterJob> jobs = feedToTwitterJobDAO.getAllTweetFeedJobs();
        for (FeedToTwitterJob job : jobs) {
        	final Feed feed = job.getFeed();
			log.info("Running feed to twitter job: " + feed.getUrl() + " -> " + job.getAccount().getUsername());
        	List<FeedItem> feedItems = feedDAO.loadFeedItems(feed);        
        	if (feedItems != null && !feedItems.isEmpty()) {
        		twitterUpdater.updateFeed(feedItems, job.getAccount());
        		
        	} else {
        		log.warn("Failed to load feed items from feed url or feed contained no items: " + feed.getUrl());
        	}
        }
    }
    
}
