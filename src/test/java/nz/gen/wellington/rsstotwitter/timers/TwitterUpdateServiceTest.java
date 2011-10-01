package nz.gen.wellington.rsstotwitter.timers;

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
import nz.gen.wellington.rsstotwitter.repositories.FeedDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;
import nz.gen.wellington.rsstotwitter.twitter.TweetFromFeedItemBuilder;
import nz.gen.wellington.twitter.TwitterService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TwitterUpdateServiceTest {
	
	private static final String FIRST_FEED_URL = "FIRST FEED URL";

	@Mock TwitteredFeedDAO twitteredFeedDAO; // TODO this shouldn't be on this service
	
	@Mock TwitterHistoryDAO twitterHistoryDAO;
	@Mock FeedDAO feedDAO;
	@Mock TweetFromFeedItemBuilder tweetFromFeedItemBuilder;
	@Mock TwitterService twitterService;
	@Mock TweetDAO tweetDAO;
	@Mock TwitteredFeed feed;
	
	@Mock Tweet tweetToSend;
	@Mock Tweet sentTweet;
	@Mock TwitterAccount account;
	
	TwitterUpdateService service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);		
		
		when(feed.getUrl()).thenReturn(FIRST_FEED_URL);
		when(feed.getTwitterTag()).thenReturn(null);
		when(feed.getAccount()).thenReturn(account);
		
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(2);
		service = new TwitterUpdateService(feedDAO, twitterHistoryDAO, twitterService, twitteredFeedDAO, tweetDAO, tweetFromFeedItemBuilder);
	}
			
	@Test
	public void shouldNotTwitIfFeedWasInitiallyOverFeedRateLimit() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(35);		
		service.updateFeed(feed);
		
		verifyNoMoreInteractions(feedDAO);
		verifyNoMoreInteractions(twitterService);
	}
	
	@Test
	public void shouldGracefullyDoNothingIfFeedFailsToLoad() throws Exception {
		when(feedDAO.loadFeedItems(FIRST_FEED_URL)).thenReturn(null);
		service.updateFeed(feed);
		
		verify(feedDAO).loadFeedItems(FIRST_FEED_URL);
		verifyNoMoreInteractions(feedDAO);
		verifyNoMoreInteractions(twitterService);
	}
	
	@Test
	public void shouldTweetFeedItems() throws Exception {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		FeedItem feedItem = new FeedItem("title", "guid", "link", Calendar.getInstance().getTime(), "author", null, null);
		feedItems.add(feedItem);
		when(feedDAO.loadFeedItems(FIRST_FEED_URL)).thenReturn(feedItems);
		when(tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem, null)).thenReturn(tweetToSend);
		when(twitterService.twitter(tweetToSend, feed.getAccount())).thenReturn(sentTweet);
		
		service.updateFeed(feed);
		
		verify(twitterService).twitter(tweetToSend, account);
		verify(tweetDAO).saveTweet(sentTweet);
		verify(twitterHistoryDAO).markAsTwittered(feedItem, feed, sentTweet);
	}
		
	@Test
	public void shouldNotTweetFeedItemsOlderThanOneWeek() throws Exception {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		Date oldDate = Calendar.getInstance().getTime();
		oldDate.setYear(90);
		FeedItem feedItem = new FeedItem("title", "guid", "link", oldDate, "author", null, null);
		feedItems.add(feedItem);
		when(feedDAO.loadFeedItems(FIRST_FEED_URL)).thenReturn(feedItems);

		service.updateFeed(feed);
		
		verifyNoMoreInteractions(tweetFromFeedItemBuilder);
		verifyNoMoreInteractions(twitterService);
	}
		
	@Test
	public void shouldNotExceedRateLimitDuringRun() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(29);
	}
	
}
