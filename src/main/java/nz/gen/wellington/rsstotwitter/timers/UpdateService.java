package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.FeedDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;

import org.apache.log4j.Logger;

public class UpdateService {
	
	private static Logger log = Logger.getLogger(UpdateService.class);

    private TwitteredFeedDAO twitteredFeedDAO;
    private FeedDAO feedDAO;
    private Updater twitterUpdater;
    
	public UpdateService(TwitteredFeedDAO twitteredFeedDAO, FeedDAO feedDAO, Updater twitterUpdater) {
		this.twitteredFeedDAO = twitteredFeedDAO;
		this.feedDAO = feedDAO;
		this.twitterUpdater = twitterUpdater;
	}
	
	public void run() {       
        List<TwitteredFeed> feeds = twitteredFeedDAO.getAllFeeds();
        for (TwitteredFeed feed : feeds) {
        	log.info("Updating feed: " + feed.getUrl());

        	List<FeedItem> feedItems = feedDAO.loadFeedItems(feed.getUrl());
        	if (feedItems != null) {
        		twitterUpdater.updateFeed(feed, feedItems);
        	} else {
        		log.warn("Failed to load feed items from feed url: " + feed.getUrl());
        	}
        }
    }
    
}
