package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.rsstotwitter.feeds.FeedDAO;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateServiceTest {
	
	private static final String TWITTER_USERNAME = "testuser";

	private static final String FEED_URL = "http://localhost/rss";
	
	@Mock FeedToTwitterJobDAO tweetFeedJobDAO;
	@Mock FeedDAO feedDAO;
	@Mock Updater twitterUpdater;

	private UpdateService service;

	private List<FeedToTwitterJob> feedToTwitterJobs;
	
	Feed feed;
	@Mock List<FeedItem> feedItems;
	TwitterAccount account;
	private String tag;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		feedToTwitterJobs = new ArrayList<FeedToTwitterJob>();
		service = new UpdateService(tweetFeedJobDAO, feedDAO, twitterUpdater);

		feed = new Feed(FEED_URL);
		account = new TwitterAccount(1, TWITTER_USERNAME);
		feedToTwitterJobs.add(new FeedToTwitterJob(feed, account, tag));
		when(tweetFeedJobDAO.getAllTweetFeedJobs()).thenReturn(feedToTwitterJobs);	
	}
	
	@Test
	public void shouldFetchFeedItemsAndPassThemToTheTwitterUpdaterForTweeting() throws Exception {
		when(feedDAO.loadFeedItems(feed)).thenReturn(feedItems);

		service.run();
		
		verify(twitterUpdater).updateFeed(feed, feedItems, account, tag);
	}
	
	@Test
	public void shouldGracefullyDoNothingIfFeedFailsToLoad() throws Exception {		
		when(feedDAO.loadFeedItems(feed)).thenReturn(null);
		
		service.run();
		
		verify(feedDAO).loadFeedItems(feed);
		verifyNoMoreInteractions(twitterUpdater);
	}

}
