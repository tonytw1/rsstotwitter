package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.tinyurl.TinyUrlService;
import nz.gen.wellington.twitter.TwitTextBuilderService;
import nz.gen.wellington.twitter.TwitterService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TwitTextBuilderServiceTest {

    private static final String PUBLISHER_NAME = "A Publisher";
	private static final String TITLE = "The quick brown fox";
    private static final String LONG_URL = "http://www.longurl/etc";
    private static final String LONG_TITLE = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
	private static final String REALLY_LONG_TITLE = LONG_TITLE + "12345";
	
    @Mock TinyUrlService tinyUrlService;
    TwitTextBuilderService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tinyUrlService = mock(TinyUrlService.class);
        when(tinyUrlService.makeTinyUrl(LONG_URL)).thenReturn("http://tinyurl/1");
        service = new TwitTextBuilderService(tinyUrlService);
    }

    @Test
    public void shouldIgnoreLinkIfSetToNull() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(TITLE, null, null, null, null, null, null), null);
        assertEquals(TITLE, twit);
    }

    @Test
    public void shouldConvertLinksIntoTinyUrls() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(TITLE, LONG_URL, LONG_URL, null, null, null, null), null);
        verify(tinyUrlService).makeTinyUrl(LONG_URL);
        assertEquals("The quick brown fox http://tinyurl/1", twit);
    }
    
    @Test
    public void shouldNotAppendChannelIfNotSet() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(TITLE, LONG_URL, LONG_URL, null, null, null, null), null);        
        assertEquals("The quick brown fox http://tinyurl/1", twit);        
    }

    @Test
    public void shouldPrependPublisher() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(TITLE, LONG_URL, LONG_URL, null, PUBLISHER_NAME, null, null), null);        
        assertEquals("A Publisher - The quick brown fox http://tinyurl/1", twit);
    }

    @Test
    public void shouldNotIncludeChannelButOnlyIfThereIsRoom() throws Exception {
        final String twit = service.buildTwitForItem(new FeedItem(TITLE, LONG_URL, LONG_URL, null, null, null, null), "testtag");      
        assertTrue(twit.length() <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH);      
        assertEquals(TITLE + " http://tinyurl/1 #testtag", twit);
        
        final String longTwit = service.buildTwitForItem(new FeedItem(REALLY_LONG_TITLE, LONG_URL, LONG_URL, null, null, null, null), "testtag");       
        assertTrue(longTwit.length() <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH);
        assertFalse(longTwit.endsWith("#testtag"));       
    }

}
