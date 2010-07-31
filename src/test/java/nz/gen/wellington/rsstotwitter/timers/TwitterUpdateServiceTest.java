package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.FeedDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;
import nz.gen.wellington.twitter.TwitBuilderService;
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
	@Mock TwitBuilderService twitBuilderService;
	@Mock TwitterService twitterService;
	@Mock TweetDAO tweetDAO;
	@Mock TwitteredFeed feed;
	
	TwitterUpdateService service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);		
		when(feed.getUrl()).thenReturn(FIRST_FEED_URL);
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
		when(feedDAO.loadSyndFeedWithFeedFetcher(FIRST_FEED_URL)).thenReturn(null);
		service.updateFeed(feed);
		verify(feedDAO).loadSyndFeedWithFeedFetcher(FIRST_FEED_URL);
		verifyNoMoreInteractions(feedDAO);
		verifyNoMoreInteractions(twitterService);
	}
	
	@Test
	public void shouldNotExceedRateLimitDuringRun() throws Exception {
		when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed)).thenReturn(29);
	}
	
}
