package nz.gen.wellington.rsstotwitter.timers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.rsstotwitter.feeds.FeedService;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.Account;

import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateServiceTest {

    private static final String TWITTER_USERNAME = "testuser";

    private static final String FEED_URL = "http://localhost/rss";
    private static final String SECOND_FEED_URL = "http://localhost/2/rss";

    @Mock
    JobDAO jobDAO;
    @Mock
    FeedService feedService;
    @Mock
    Updater twitterUpdater;

    private UpdateService service;

    private List<FeedToTwitterJob> jobs;

    Feed feed;
    Feed secondFeed;
    Account account;
    Account secondAccount;
    @Mock
    List<FeedItem> feedItems;
    @Mock
    private List<FeedItem> secondFeedItems;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        jobs = new ArrayList<>();
        service = new UpdateService(jobDAO, feedService, twitterUpdater);

        feed = new Feed(FEED_URL);
        account = new Account(1, TWITTER_USERNAME);
        secondFeed = new Feed(SECOND_FEED_URL);
        secondAccount = new Account(2, TWITTER_USERNAME);
        jobs.add(new FeedToTwitterJob(feed, account));
        jobs.add(new FeedToTwitterJob(secondFeed, secondAccount));

        when(jobDAO.getAllTweetFeedJobs()).thenReturn(jobs);
    }

    @Test
    public void shouldFetchFeedItemsAndPassThemToTheTwitterUpdaterForTweeting() throws Exception {
        when(feedService.loadFeedItems(feed)).thenReturn(feedItems);

        service.run();

        verify(twitterUpdater).updateFeed(feed, feedItems, account);
    }

    @Test
    public void shouldGracefullyDoNothingIfFeedFailsToLoad() throws Exception {
        when(feedService.loadFeedItems(feed)).thenReturn(null);

        service.run();

        verifyNoMoreInteractions(twitterUpdater);
    }

    @Test
    public void shouldContinueProcessingRemainingFeedsIfOneFailsToLoad() throws Exception {
        when(feedService.loadFeedItems(feed)).thenReturn(null);
        when(feedService.loadFeedItems(secondFeed)).thenReturn(secondFeedItems);

        service.run();

        verify(twitterUpdater).updateFeed(secondFeed, secondFeedItems, secondAccount);
    }

}
