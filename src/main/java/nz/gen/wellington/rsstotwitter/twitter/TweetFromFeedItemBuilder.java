package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.GeoLocation;

@Component
public class TweetFromFeedItemBuilder {

    public final static int MAXIMUM_TWITTER_MESSAGE_LENGTH = 140;

    private final static Logger log = Logger.getLogger(TweetFromFeedItemBuilder.class);

    private final TwitTextBuilderService twitBuilderService;

    @Autowired
    public TweetFromFeedItemBuilder(TwitTextBuilderService twitBuilderService) {
        this.twitBuilderService = twitBuilderService;
    }

    public Tweet buildTweetFromFeedItem(FeedItem feedItem) {
        final String tweetText = twitBuilderService.buildTwitForItem(feedItem);
        validateTweet(tweetText);

        Tweet tweet = new Tweet(tweetText);
        feedItem.getLatLong().ifPresent(latLong -> {
            tweet.setGeoLocation(new GeoLocation(latLong.getLatitude(), latLong.getLongitude()));
        });

        return tweet;
    }

    private void validateTweet(String tweetText) {
        if (!(tweetText.length() <= MAXIMUM_TWITTER_MESSAGE_LENGTH)) {
            log.warn("Message too long to tweet; not tweeted: " + tweetText);
            throw new RuntimeException("Message to long to tweet");
        }

        log.debug("Checking for valid characters.");
        char[] charArray = tweetText.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char letter = tweetText.charAt(i);
            log.debug(letter + "(" + Character.codePointAt(charArray, i) + "): " + Character.isValidCodePoint(letter));
            if (!Character.isValidCodePoint(letter)) {
                log.warn("Message has invalid code point: " + letter);
                throw new RuntimeException("Message has invalid code point: " + letter);
            }
            if (65533 == Character.codePointAt(charArray, i)) {
                log.warn("Message has problem code point 65533: " + letter);
                throw new RuntimeException("Message has problem code point 65533: " + letter);
            }
        }
    }

}
