package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.rsstotwitter.feeds.FeedDAO;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.TweetFeedJob;
import nz.gen.wellington.rsstotwitter.repositories.TweetFeedJobDAO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateServiceTest {
	
	private static final String FIRST_FEED_URL = "FIRST FEED URL";
	
	@Mock TweetFeedJobDAO tweetFeedJobDAO;
	@Mock FeedDAO feedDAO;
	@Mock Updater twitterUpdater;

	UpdateService service;

	private List<TweetFeedJob> feedToTwitterJobs;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		feedToTwitterJobs = new ArrayList<TweetFeedJob>();
		service = new UpdateService(tweetFeedJobDAO, feedDAO, twitterUpdater);
	}
	
	@Test
	public void shouldGracefullyDoNothingIfFeedFailsToLoad() throws Exception {		
		feedToTwitterJobs.add(new TweetFeedJob(new Feed(FIRST_FEED_URL), null, null));
		when(tweetFeedJobDAO.getAllTweetFeedJobs()).thenReturn(feedToTwitterJobs);
		when(feedDAO.loadFeedItems(FIRST_FEED_URL)).thenReturn(null);
		
		service.run();
		
		verify(feedDAO).loadFeedItems(FIRST_FEED_URL);
		verifyNoMoreInteractions(twitterUpdater);
	}

}
