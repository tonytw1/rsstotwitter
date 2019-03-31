package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class TwitterUpdaterTest {
	
	@Mock TwitterHistoryDAO twitterHistoryDAO;
	@Mock TweetFromFeedItemBuilder tweetFromFeedItemBuilder;
	@Mock TwitterService twitterService;
	@Mock Feed feed;
	
	@Mock Tweet tweetToSend;
	@Mock Tweet sentTweet;
	@Mock TwitterAccount account;
	
	TwitterUpdater service;

	private List<FeedItem> feedItems;
	private FeedItem feedItem;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);			
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(2);
		feedItem = new FeedItem(feed, "title", "guid", "link", Calendar.getInstance().getTime(), "author", null, null);
		feedItems = new ArrayList<FeedItem>();
		feedItems.add(feedItem);
		
		service = new TwitterUpdater(twitterHistoryDAO, twitterService, tweetFromFeedItemBuilder);
	}
			
	@Test
	public void shouldNotTwitIfFeedWasInitiallyOverFeedRateLimit() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(55);
		
		service.updateFeed(feed, feedItems, account);
		
		verifyNoMoreInteractions(twitterService);
	}
	
	@Test
	public void shouldTweetFeedItems() throws Exception {
		when(tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem)).thenReturn(tweetToSend);
		when(twitterService.tweet(tweetToSend, account)).thenReturn(sentTweet);
		
		service.updateFeed(feed, feedItems, account);
		
		verify(twitterService).tweet(tweetToSend, account);
		verify(twitterHistoryDAO).markAsTwittered(feedItem, sentTweet);
	}
		
	@Test
	public void shouldNotTweetFeedItemsOlderThanOneWeek() throws Exception {
		final Date oldDate = new DateTime().minusDays(10).toDate();
		FeedItem oldFeedItem = new FeedItem(feed, "title", "guid", "link", oldDate, "author", null, null);
		feedItems.clear();
		feedItems.add(oldFeedItem);
		
		service.updateFeed(feed, feedItems, account);
		
		verifyNoMoreInteractions(tweetFromFeedItemBuilder);
		verifyNoMoreInteractions(twitterService);
	}
		
	@Test
	public void shouldNotExceedRateLimitDuringRun() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(29);
	}
	
}
