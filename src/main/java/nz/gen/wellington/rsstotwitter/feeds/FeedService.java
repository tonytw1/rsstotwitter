package nz.gen.wellington.rsstotwitter.feeds;

import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.GeoRSSUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.LatLong;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FeedService {

    private final static Logger log = Logger.getLogger(FeedService.class);

    public List<FeedItem> loadFeedItems(Feed feed) {
        List<SyndEntry> entries = loadSyndFeedEntiresWithFeedFetcher(feed.getUrl());
        if (entries == null) {
            log.warn("Could not load syndfeed entires from url: " + feed.getUrl() + ". Returning empty list of items");
            return null;
        }

        Stream<FeedItem> feedItems = entries.stream().map(entry -> mapFeedItem(feed, entry));

        return feedItems.collect(Collectors.toList());
    }

    private List<SyndEntry> loadSyndFeedEntiresWithFeedFetcher(String feedUrl) {
        log.info("Loading SyndFeed from url: " + feedUrl);
        try {
            URL url = new URL(feedUrl);
            FeedFetcher fetcher = new HttpURLFeedFetcher();
            SyndFeed syndFeed = fetcher.retrieveFeed(url);
            if (syndFeed != null) {
                return syndFeed.getEntries();
            } else {
                return null;
            }

        } catch (Exception e) {
            log.warn("Error while fetching feed: " + e.getMessage());
        }

        return null;
    }

    private FeedItem mapFeedItem(Feed feed, SyndEntry syndEntry) {
        return new FeedItem(feed, syndEntry.getTitle(), syndEntry.getUri(), syndEntry.getLink(), syndEntry.getPublishedDate(), syndEntry.getAuthor(), extractLatLong(syndEntry));
    }

    private LatLong extractLatLong(SyndEntry syndEntry) {
        GeoRSSModule geoModule = GeoRSSUtils.getGeoRSS(syndEntry);
        if (geoModule != null && geoModule.getPosition() != null) {
            LatLong latLong = new LatLong(geoModule.getPosition().getLatitude(), geoModule.getPosition().getLongitude());
            log.debug("Feed item '" + syndEntry.getTitle() + "' has position information: " + latLong);
            return latLong;
        } else {
            return null;
        }
    }

}
