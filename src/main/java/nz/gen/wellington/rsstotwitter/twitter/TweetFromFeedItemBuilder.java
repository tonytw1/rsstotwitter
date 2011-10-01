package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.twitter.TwitTextBuilderService;
import twitter4j.GeoLocation;

public class TweetFromFeedItemBuilder {
	
	private TwitTextBuilderService twitBuilderService;
	
	public TweetFromFeedItemBuilder(TwitTextBuilderService twitBuilderService) {
		this.twitBuilderService = twitBuilderService;
	}
	
	public Tweet buildTweetFromFeedItem(FeedItem feedItem, String tag) {
		final String tweetText = twitBuilderService.buildTwitForItem(feedItem, tag);
		Tweet tweet = new Tweet(tweetText);
		if (feedItem.isGeocoded()) {
			tweet.setGeoLocation(new GeoLocation(feedItem.getLatitude(), feedItem.getLongitude()));
		}
		return tweet;
	}
	

}
