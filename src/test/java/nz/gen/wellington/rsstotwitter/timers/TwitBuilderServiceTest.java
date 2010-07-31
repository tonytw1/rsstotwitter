package nz.gen.wellington.rsstotwitter.timers;

import nz.gen.wellington.tinyurl.TinyUrlService;
import nz.gen.wellington.twitter.TwitBuilderService;
import nz.gen.wellington.twitter.TwitterService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TwitBuilderServiceTest {

    private static final String TITLE = "The quick brown fox";
    private static final String LONG_URL = "http://www.longurl/etc";
    private static final String LONG_TITLE = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    @Mock TinyUrlService tinyUrlService;
    TwitBuilderService service;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tinyUrlService = mock(TinyUrlService.class);
        when(tinyUrlService.makeTinyUrl(LONG_URL)).thenReturn("http://tinyurl/1");
        service = new TwitBuilderService(tinyUrlService);
    }

    @Test
    public void shouldIgnoreLinkIfSetToNull() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, null, null, null);
        assertEquals(TITLE, twit);
    }

    @Test
    public void shouldConvertLinksIntoTinyUrls() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, null, null);
        verify(tinyUrlService).makeTinyUrl(LONG_URL);
        assertEquals("The quick brown fox http://tinyurl/1", twit);
    }
    
    @Test
    public void shouldNorAppendChannelNotSet() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, null, null);        
        assertEquals("The quick brown fox http://tinyurl/1", twit);        
    }

    @Test
    public void shouldPrependPublisher() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, "A Publisher", null);        
        assertEquals("A Publisher - The quick brown fox http://tinyurl/1", twit);        
    }

    @Test
    public void shouldNotIncludeChannelButOnlyIfThereIsRoom() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, null, "testtag");      
        assertTrue(twit.length() <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH);      
        assertEquals(TITLE + " http://tinyurl/1 #testtag", twit);
        
        final String reallyLongTitle = LONG_TITLE + "12345";
        final String longTwit = service.buildTwitForItem(reallyLongTitle, LONG_URL, null, "testtag");       
        assertTrue(longTwit.length() <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH);
        assertFalse(longTwit.endsWith("#testtag"));       
    }

}
