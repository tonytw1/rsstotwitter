package nz.gen.wellington.rsstotwitter.twitter;

import com.twitter.twittertext.Extractor;
import com.twitter.twittertext.TwitterTextParseResults;
import com.twitter.twittertext.TwitterTextParser;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwitTextBuilderServiceTest {

    private static final String PUBLISHER_NAME = "A Publisher";
    private static final String TITLE = "The quick brown fox";
    private static final String LONG_URL = "http://www.longurl.com/etc/1234567890/abcdefghijk";

    TwitTextBuilderService service = new TwitTextBuilderService();

    private final Feed feed = new Feed();

    @Test
    public void shouldIgnoreLinkIfSetToNull() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, null, null, null, null, Optional.empty()));
        assertEquals(TITLE, twit);
    }

    @Test
    public void shouldKnowThatUrlsAreShorterned() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, null, Optional.empty()));

        assertEquals("The quick brown fox http://www.longurl.com/etc/1234567890/abcdefghijk", twit);

        TwitterTextParseResults twitterTextParseResults = TwitterTextParser.parseTweet(twit);
        assertTrue(twitterTextParseResults.isValid);
        // Twitter applies t.co shorter to all urls so all urls count as length 23
        assertTrue(LONG_URL.length() > 23);
        assertEquals(TITLE.length() + " ".length() + 23, twitterTextParseResults.weightedLength);

        final Extractor extractor = new Extractor();
        List<String> urls = extractor.extractURLs(twit);
        assertEquals(1, urls.size());
        assertEquals("http://www.longurl.com/etc/1234567890/abcdefghijk",urls.get(0));
    }

    @Test
    public void shouldNotAppendChannelIfNotSet() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, null, Optional.empty()));
        assertEquals("The quick brown fox http://www.longurl.com/etc/1234567890/abcdefghijk", twit);
    }

    @Test
    public void shouldPrependPublisher() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, PUBLISHER_NAME, Optional.empty()));
        assertEquals("A Publisher - The quick brown fox http://www.longurl.com/etc/1234567890/abcdefghijk", twit);
    }

}
