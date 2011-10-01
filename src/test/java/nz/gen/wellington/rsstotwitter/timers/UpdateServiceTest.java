package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.FeedDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateServiceTest {
	
	private static final String FIRST_FEED_URL = "FIRST FEED URL";
	
	@Mock TwitteredFeedDAO twitteredFeedDAO;
	@Mock FeedDAO feedDAO;
	@Mock Updater twitterUpdater;

	UpdateService service;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);		
		
		service = new UpdateService(twitteredFeedDAO, feedDAO, twitterUpdater);
	}
	
	@Test
	public void shouldGracefullyDoNothingIfFeedFailsToLoad() throws Exception {
		List<TwitteredFeed> feeds = new ArrayList<TwitteredFeed>();
		feeds.add(new TwitteredFeed(FIRST_FEED_URL, null, null, null, null));
		when(twitteredFeedDAO.getAllFeeds()).thenReturn(feeds);
		when(feedDAO.loadFeedItems(FIRST_FEED_URL)).thenReturn(null);
		
		service.run();
		
		verify(feedDAO).loadFeedItems(FIRST_FEED_URL);
		verifyNoMoreInteractions(twitterUpdater);
	}

}
