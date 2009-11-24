package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;
import nz.gen.wellington.tinyurl.TinyUrlService;
import nz.gen.wellington.twitter.TwitBuilderService;
import nz.gen.wellington.twitter.TwitterService;

public class TwitBuilderServiceTest extends TestCase {

    private static final String TITLE = "The quick brown fox";
    private static final String LONG_URL = "http://www.longurl/etc";
    private static final String LONG_TITLE = "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    TinyUrlService tinyUrlService;
    TwitBuilderService service;
    
    @Override
    protected void setUp() throws Exception {
        tinyUrlService = mock(TinyUrlService.class);        
        stub(tinyUrlService.makeTinyUrl(LONG_URL)).toReturn("http://tinyurl/1");
        service = new TwitBuilderService(tinyUrlService);
    }
    
    public void testShouldIgnoreLinkIfSetToNull() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, null, null, null);
        assertEquals(TITLE, twit);
    }
    
    public void testShouldConvertLinksIntoTinyUrls() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, null, null);
        verify(tinyUrlService).makeTinyUrl(LONG_URL);
        assertEquals("The quick brown fox http://tinyurl/1", twit);
    }
    
    
    public void testShouldNorAppendChannelNotSet() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, null, null);        
        assertEquals("The quick brown fox http://tinyurl/1", twit);        
    }
    
    public void testShouldPrependPublisher() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, "A Publisher", null);        
        assertEquals("A Publisher - The quick brown fox http://tinyurl/1", twit);        
    }
            
    public void testShouldNotIncludeChannelButOnlyIfThereIsRoom() throws Exception {
        final String twit = service.buildTwitForItem(TITLE, LONG_URL, null, "testtag");      
        assertTrue(twit.length() <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH);      
        assertEquals(TITLE + " http://tinyurl/1 #testtag", twit);
        
        final String reallyLongTitle = LONG_TITLE + "12345";
        final String longTwit = service.buildTwitForItem(reallyLongTitle, LONG_URL, null, "testtag");       
        assertTrue(longTwit.length() <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH);
        assertFalse(longTwit.endsWith("#testtag"));       
    }
        
}
