package nz.gen.wellington.rsstotwitter.twitter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TwitterUpdaterTest {
	
	@Mock TwitterHistoryDAO twitterHistoryDAO;
	@Mock TweetFromFeedItemBuilder tweetFromFeedItemBuilder;
	@Mock TwitterService twitterService;
	@Mock TweetDAO tweetDAO;
	@Mock TwitteredFeed feed;
	
	@Mock Tweet tweetToSend;
	@Mock Tweet sentTweet;
	@Mock TwitterAccount account;
	
	TwitterUpdater service;

	private List<FeedItem> feedItems;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);		
		when(feed.getTwitterTag()).thenReturn(null);
		when(feed.getAccount()).thenReturn(account);
		
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(2);
		feedItems = new ArrayList<FeedItem>();
		
		service = new TwitterUpdater(twitterHistoryDAO, twitterService, tweetDAO, tweetFromFeedItemBuilder);
	}
			
	@Test
	public void shouldNotTwitIfFeedWasInitiallyOverFeedRateLimit() throws Exception {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(35);
		
		service.updateFeed(feed, feedItems);
		
		verifyNoMoreInteractions(twitterService);
	}
	
	@Test
	public void shouldTweetFeedItems() throws Exception {
		FeedItem feedItem = new FeedItem("title", "guid", "link", Calendar.getInstance().getTime(), "author", null, null);
		feedItems.add(feedItem);
		when(tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem, null)).thenReturn(tweetToSend);
		when(twitterService.twitter(tweetToSend, feed.getAccount())).thenReturn(sentTweet);
		
		service.updateFeed(feed, feedItems);
		
		verify(twitterService).twitter(tweetToSend, account);
		verify(tweetDAO).saveTweet(sentTweet);
		verify(twitterHistoryDAO).markAsTwittered(feedItem, feed, sentTweet);
	}
		
	@Test
	public void shouldNotTweetFeedItemsOlderThanOneWeek() throws Exception {
		final Date oldDate = new DateTime().minusDays(10).toDate();
		FeedItem feedItem = new FeedItem("title", "guid", "link", oldDate, "author", null, null);
		feedItems.add(feedItem);
		
		service.updateFeed(feed, feedItems);
		
		verifyNoMoreInteractions(tweetFromFeedItemBuilder);
		verifyNoMoreInteractions(twitterService);
	}
		
	@Test
	public void shouldNotExceedRateLimitDuringRun() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(29);
	}
	
}