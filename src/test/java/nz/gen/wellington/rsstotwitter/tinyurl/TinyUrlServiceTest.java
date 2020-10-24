package nz.gen.wellington.rsstotwitter.tinyurl;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TinyUrlServiceTest {

    @Test
    public void canShortenUrlsToTinyUrls() throws IOException {
        TinyUrlService tinyUrlService = new TinyUrlService();

        String tinyUrl = tinyUrlService.makeTinyUrl("http://wellington.gen.nz/tiny-url-test");

        assertEquals("https://tinyurl.com/y5qyqgzk", tinyUrl);
    }

}



