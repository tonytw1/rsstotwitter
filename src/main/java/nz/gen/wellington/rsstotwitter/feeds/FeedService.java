package nz.gen.wellington.rsstotwitter.feeds;

import com.google.common.collect.Lists;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.GeoRSSUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FeedService {

    private final static Logger log = Logger.getLogger(FeedService.class);

    @SuppressWarnings("unchecked")
    public List<FeedItem> loadFeedItems(Feed feed) {

        SyndFeed syndfeed = loadSyndFeedWithFeedFetcher(feed.getUrl());
        if (syndfeed == null) {
            log.warn("Could not load syndfeed from url: " + feed.getUrl() + ". Returning empty list of items");
            return null;
        }

        List<SyndEntry> entries = syndfeed.getEntries();
        Stream<FeedItem> feedItems = entries.stream().map(entry -> mapFeedItem(feed, entry));

        return feedItems.collect(Collectors.toList());
    }

    private SyndFeed loadSyndFeedWithFeedFetcher(String feedUrl) {
        log.info("Loading SyndFeed from url: " + feedUrl);
        try {
            URL url = new URL(feedUrl);
            FeedFetcher fetcher = new HttpURLFeedFetcher();
            return fetcher.retrieveFeed(url);

        } catch (Exception e) {
            log.warn("Error while fetching feed: " + e.getMessage());
        }
        return null;
    }

    private FeedItem mapFeedItem(Feed feed, SyndEntry syndEntry) {
        Double latitude = null;
        Double longitude = null;
        GeoRSSModule geoModule = GeoRSSUtils.getGeoRSS(syndEntry);
        if (geoModule != null && geoModule.getPosition() != null) {
            latitude = geoModule.getPosition().getLatitude();
            longitude = geoModule.getPosition().getLongitude();
            log.debug("Feed item '" + syndEntry.getTitle() + "' has position information: " + latitude + "," + longitude);
        }
        return new FeedItem(feed, syndEntry.getTitle(), syndEntry.getUri(), syndEntry.getLink(), syndEntry.getPublishedDate(), syndEntry.getAuthor(), latitude, longitude);
    }

}
