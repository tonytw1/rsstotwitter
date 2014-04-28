package nz.gen.wellington.rsstotwitter.feeds;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.GeoRSSUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class FeedDAO {	// TODO not really a database DAO - rename

    public final static Logger log = Logger.getLogger(FeedDAO.class);

    @SuppressWarnings("unchecked")
	public List<FeedItem> loadFeedItems(Feed feed) {
    	List<FeedItem> feedItems = Lists.newArrayList();
    	SyndFeed syndfeed = loadSyndFeedWithFeedFetcher(feed.getUrl());
    	if (syndfeed == null) {
    		log.warn("Could not load syndfeed from url: " + feed.getUrl() + ". Returning empty list of items");
    		return null;
    	}
    	
        final Iterator<SyndEntry> feedItemsIterator = syndfeed.getEntries().iterator();
        while (feedItemsIterator.hasNext()) {        	
        	feedItems.add(mapFeedItem(feed, (SyndEntry) feedItemsIterator.next()));
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
    
    private FeedItem mapFeedItem(Feed feed, SyndEntry syndEntry) {
		Double latitude = null;
		Double longitude = null;
		GeoRSSModule geoModule = (GeoRSSModule) GeoRSSUtils.getGeoRSS(syndEntry);
		if (geoModule != null && geoModule.getPosition() != null) {
			latitude = geoModule.getPosition().getLatitude();
			longitude = geoModule.getPosition().getLongitude();
			log.debug("Rss item '" + syndEntry.getTitle() + "' has position information: " + latitude + "," + longitude);
		}		
		return new FeedItem(feed, syndEntry.getTitle(), syndEntry.getUri(), syndEntry.getLink(), syndEntry.getPublishedDate(), syndEntry.getAuthor(), latitude, longitude);
	}
    
}
