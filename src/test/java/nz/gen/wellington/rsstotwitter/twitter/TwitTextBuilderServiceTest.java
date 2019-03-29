package nz.gen.wellington.rsstotwitter.twitter;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.tinyurl.TinyUrlService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TwitTextBuilderServiceTest {

    private static final String PUBLISHER_NAME = "A Publisher";
	private static final String TITLE = "The quick brown fox";
    private static final String LONG_URL = "http://www.longurl/etc";
	
    @Mock TinyUrlService tinyUrlService;
    TwitTextBuilderService service;
    
    Feed feed;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tinyUrlService = mock(TinyUrlService.class);
        when(tinyUrlService.makeTinyUrl(LONG_URL)).thenReturn("http://tinyurl/1");
        service = new TwitTextBuilderService(tinyUrlService);
    }

    @Test
    public void shouldIgnoreLinkIfSetToNull() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, null, null, null, null, null, null));
        assertEquals(TITLE, twit);
    }

    @Test
    public void shouldConvertLinksIntoTinyUrls() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, null, null, null));
        verify(tinyUrlService).makeTinyUrl(LONG_URL);
        assertEquals("The quick brown fox http://tinyurl/1", twit);
    }
    
    @Test
    public void shouldNotAppendChannelIfNotSet() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, null, null, null));        
        assertEquals("The quick brown fox http://tinyurl/1", twit);        
    }

    @Test
    public void shouldPrependPublisher() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(feed, TITLE, LONG_URL, LONG_URL, null, PUBLISHER_NAME, null, null));        
        assertEquals("A Publisher - The quick brown fox http://tinyurl/1", twit);
    }
    
}
