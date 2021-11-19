package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.LatLong;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TweetFromFeedItemBuilderTest {

    private final TwitTextBuilderService twitTextBuilderService = new TwitTextBuilderService();

    @Test
    public void feedItemLocationIsIncludedOnTweet() throws IOException {
        TweetFromFeedItemBuilder tweetFromFeedItemBuilder = new TweetFromFeedItemBuilder(twitTextBuilderService);

        LatLong latLong = new LatLong(51.3, -0.1);
        FeedItem feedItem = new FeedItem(null, "Has location", null, null, null, null, Optional.of(latLong));

        Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem);

        assertTrue(tweet.getGeoLocation() != null);
        assertEquals(51.3, tweet.getGeoLocation().getLatitude(), 0);
        assertEquals(-0.1, tweet.getGeoLocation().getLongitude(), 0);
    }

}
