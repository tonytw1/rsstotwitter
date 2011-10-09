package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.feeds.FeedDAO;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.TweetFeedJob;
import nz.gen.wellington.rsstotwitter.repositories.TweetFeedJobDAO;

import org.apache.log4j.Logger;

public class UpdateService {
	
	private static Logger log = Logger.getLogger(UpdateService.class);

    private TweetFeedJobDAO tweetFeedJobDAO;
    private FeedDAO feedDAO;
    private Updater twitterUpdater;
    
	public UpdateService(TweetFeedJobDAO tweetFeedJobDAO, FeedDAO feedDAO, Updater twitterUpdater) {
		this.tweetFeedJobDAO = tweetFeedJobDAO;
		this.feedDAO = feedDAO;
		this.twitterUpdater = twitterUpdater;
	}
	
	public void run() {       
        List<TweetFeedJob> jobs = tweetFeedJobDAO.getAllTweetFeedJobs();
        for (TweetFeedJob job : jobs) {
        	final Feed feed = job.getFeed();
			log.info("Running feed to twitter job: " + feed + ":" + job.getAccount());
        	List<FeedItem> feedItems = feedDAO.loadFeedItems(feed.getUrl());
        	if (feedItems != null) {
        		twitterUpdater.updateFeed(feed, feedItems, job.getAccount(), job.getTag());
        		
        	} else {
        		log.warn("Failed to load feed items from feed url: " + feed.getUrl());
        	}
        }
    }
    
}
