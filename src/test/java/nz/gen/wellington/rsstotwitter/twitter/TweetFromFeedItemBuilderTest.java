package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.LatLong;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TweetFromFeedItemBuilderTest {

    @Mock
    TwitTextBuilderService twitTextBuilderService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void feedItemLocationIsIncludedOnTweet() {
        TweetFromFeedItemBuilder tweetFromFeedItemBuilder = new TweetFromFeedItemBuilder(twitTextBuilderService);

        LatLong latLong = new LatLong(51.3, -0.1);
        FeedItem feedItem = new FeedItem(null, "Has location", null, null, null, null, Optional.of(latLong));
        when(twitTextBuilderService.buildTwitForItem(feedItem)).thenReturn("Tweet text");

        Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem);

        assertTrue(tweet.getGeoLocation() != null);
        assertEquals(51.3, tweet.getGeoLocation().getLatitude(), 0);
        assertEquals(-0.1, tweet.getGeoLocation().getLongitude(), 0);
    }

}
