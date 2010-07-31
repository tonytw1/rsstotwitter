package nz.gen.wellington.rsstotwitter.repositories;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class FeedDAO {

    public final Logger log = Logger.getLogger(FeedDAO.class);

    @SuppressWarnings("unchecked")
	public List<FeedItem> loadFeedItems(String feedUrl) {
    	SyndFeed syndfeed = loadSyndFeedWithFeedFetcher(feedUrl);
    	if (syndfeed == null) {
    		log.warn("Could not load syndfeed from url: " + feedUrl);    		
    	}
    	
    	List<FeedItem> feedItems = new ArrayList<FeedItem>();
        Iterator<SyndEntry> feedItemsIterator = syndfeed.getEntries().iterator();
        while (feedItemsIterator.hasNext()) {        	
        	SyndEntry feedItem = (SyndEntry) feedItemsIterator.next();
        	feedItems.add(new FeedItem(feedItem.getTitle(), feedItem.getUri(), feedItem.getLink(), feedItem.getPublishedDate(), feedItem.getAuthor()));
        }
        return feedItems;
    }
    
    private SyndFeed loadSyndFeedWithFeedFetcher(String feedUrl) {
        log.info("Loading SyndFeed from url: " + feedUrl);
    
        URL url;
        try {
            url = new URL(feedUrl);
            FeedFetcher fetcher = new HttpURLFeedFetcher();            
            SyndFeed feed = fetcher.retrieveFeed(url);
            return feed;
        } catch (Exception e) {
            log.warn("Error while fetching feed: " + e.getMessage());
        }
        return null;
    }

}
