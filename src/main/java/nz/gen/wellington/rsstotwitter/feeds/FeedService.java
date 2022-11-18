package nz.gen.wellington.rsstotwitter.feeds;

import com.rometools.modules.georss.GeoRSSModule;
import com.rometools.modules.georss.GeoRSSUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.LatLong;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FeedService {

    private final static Logger log = LogManager.getLogger(FeedService.class);
    private final OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(10, TimeUnit.SECONDS).
            readTimeout(10, TimeUnit.SECONDS).
            build();

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
            Request request = new Request.Builder()
                    .url(feedUrl)
                    .build();

            Response response = client.newCall(request).execute();
            InputStream bytes = response.body().byteStream();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed syndFeed = input.build(new XmlReader(bytes));

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

    private Optional<LatLong> extractLatLong(SyndEntry syndEntry) {
        GeoRSSModule geoModule = GeoRSSUtils.getGeoRSS(syndEntry);
        if (geoModule != null && geoModule.getPosition() != null) {
            LatLong latLong = new LatLong(geoModule.getPosition().getLatitude(), geoModule.getPosition().getLongitude());
            log.debug("Feed item '" + syndEntry.getTitle() + "' has position information: " + latLong);
            return Optional.of(latLong);
        } else {
            return Optional.empty();
        }
    }

}
