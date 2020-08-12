package nz.gen.wellington.rsstotwitter.tinyurl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TinyUrlServiceTest {

    @Test
    public void canShortenUrlsToTinyUrls() {
        TinyUrlService tinyUrlService = new TinyUrlService();

        String tinyUrl = tinyUrlService.makeTinyUrl("http://wellington.gen.nz/tiny-url-test");

        assertEquals("http://tinyurl.com/y5qyqgzk", tinyUrl);
    }


}



