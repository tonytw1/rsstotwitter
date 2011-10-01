package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.FeedDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;
import nz.gen.wellington.twitter.TwitTextBuilderService;
import nz.gen.wellington.twitter.TwitterService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TwitterUpdateServiceTest {
	
	private static final String FIRST_FEED_URL = "FIRST FEED URL";
	private static final String FIRST_TWIT = "FIRST TWIT";

	@Mock TwitteredFeedDAO twitteredFeedDAO; // TODO this shouldn't be on this service
	
	@Mock TwitterHistoryDAO twitterHistoryDAO;
	@Mock FeedDAO feedDAO;
	@Mock TwitTextBuilderService twitBuilderService;
	@Mock TwitterService twitterService;
	@Mock TweetDAO tweetDAO;
	@Mock TwitteredFeed feed;
	
	TwitterUpdateService service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);		
		
		when(feed.getUrl()).thenReturn(FIRST_FEED_URL);
		when(feed.getTwitterTag()).thenReturn(null);
		
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(2);
		service = new TwitterUpdateService(feedDAO, twitterHistoryDAO, twitBuilderService, twitterService, twitteredFeedDAO, tweetDAO);
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
		when(twitBuilderService.buildTwitForItem(feedItem, feed.getTwitterTag())).thenReturn(FIRST_TWIT);
		
		service.updateFeed(feed);
		verify(twitBuilderService).buildTwitForItem(feedItem, null);
		verify(twitterService).twitter(FIRST_TWIT, null, feed.getAccount());
		// TODO verify saved
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
		verifyNoMoreInteractions(twitBuilderService);
		verifyNoMoreInteractions(twitterService);
	}
		
	@Test
	public void shouldNotExceedRateLimitDuringRun() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(29);
	}
	
}
