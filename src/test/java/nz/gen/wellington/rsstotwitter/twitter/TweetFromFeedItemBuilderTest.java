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
    public void canMapFeedItemToTweet() throws IOException {
        TweetFromFeedItemBuilder tweetFromFeedItemBuilder = new TweetFromFeedItemBuilder(twitTextBuilderService);

        LatLong latLong = new LatLong(51.3, -0.1);
        FeedItem feedItem = new FeedItem(null, "A title", null, "http://localhost/test", null, "An author", Optional.of(latLong));

        Tweet tweet = tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem);

        assertEquals("An author - A title http://localhost/test", tweet.getText());
        // If we were using extended features like location or spoiler test we would assert those here
    }

}
