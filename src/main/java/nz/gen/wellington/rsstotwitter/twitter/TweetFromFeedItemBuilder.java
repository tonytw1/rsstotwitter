package nz.gen.wellington.rsstotwitter.twitter;

import com.twitter.twittertext.TwitterTextParseResults;
import com.twitter.twittertext.TwitterTextParser;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.GeoLocation;

@Component
public class TweetFromFeedItemBuilder {

    private final TwitTextBuilderService twitBuilderService;

    @Autowired
    public TweetFromFeedItemBuilder(TwitTextBuilderService twitBuilderService) {
        this.twitBuilderService = twitBuilderService;
    }

    public Tweet buildTweetFromFeedItem(FeedItem feedItem) {    // TODO could just be a string if we aren't using any extended features
        final String tweetText = twitBuilderService.buildTwitForItem(feedItem);
        validateTweet(tweetText);
        return new Tweet(tweetText);
    }

    private void validateTweet(String tweetText) {
        TwitterTextParseResults twitterTextParseResults = TwitterTextParser.parseTweet(tweetText);
        if (!twitterTextParseResults.isValid) {
            throw new IllegalArgumentException("Invalid tweet text: " + tweetText);    // TODO programming by exception
        }
    }

}
