package nz.gen.wellington.rsstotwitter.repositories;

import java.net.URL;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class FeedDAO {

    public final Logger log = Logger.getLogger(FeedDAO.class);

    protected SyndFeed loadSyndFeed(String url) {                      
        return loadSyndFeedWithFeedFetcher(url);       
    }

    public SyndFeed loadSyndFeedWithFeedFetcher(String feedUrl) {
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
