package nz.gen.wellington.rsstotwitter.twitter;

import com.twitter.twittertext.Extractor;
import com.twitter.twittertext.TwitterTextParseResults;
import com.twitter.twittertext.TwitterTextParser;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.tinyurl.TinyUrlService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitTextBuilderServiceTest {

    private static final String PUBLISHER_NAME = "A Publisher";
    private static final String TITLE = "The quick brown fox";
    private static final String LONG_URL = "http://www.longurl.com/etc/1234567890/abcdefghijk";
    private static final String SHORT_URL = "http://tinyurl.com/12345";

    @Mock
    TinyUrlService tinyUrlService;
    TwitTextBuilderService service;

    Feed feed;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        tinyUrlService = mock(TinyUrlService.class);
        when(tinyUrlService.makeTinyUrl(LONG_URL)).thenReturn(SHORT_URL);
        service = new TwitTextBuilderService(tinyUrlService);
    }

    @Test
    public void shouldIgnoreLinkIfSetToNull() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, null, null, null, null, Optional.empty()));
        assertEquals(TITLE, twit);
    }

    @Test
    public void shouldConvertLinksIntoTinyUrls() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, null, Optional.empty()));

        assertEquals("The quick brown fox http://tinyurl.com/12345", twit);

        TwitterTextParseResults twitterTextParseResults = TwitterTextParser.parseTweet(twit);
        assertTrue(twitterTextParseResults.isValid);
        // Twitter seems to let you have the space for free
        assertEquals(TITLE.length() + SHORT_URL.length(), twitterTextParseResults.weightedLength);

        final Extractor extractor = new Extractor();
        List<String> urls = extractor.extractURLs(twit);
        assertEquals(1, urls.size());
        assertEquals("http://tinyurl.com/12345",urls.get(0));
    }

    @Test
    public void shouldNotAppendChannelIfNotSet() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, null, Optional.empty()));
        assertEquals("The quick brown fox http://tinyurl/1", twit);
    }

    @Test
    public void shouldPrependPublisher() throws IOException {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, PUBLISHER_NAME, Optional.empty()));
        assertEquals("A Publisher - The quick brown fox http://tinyurl/1", twit);
    }

}
