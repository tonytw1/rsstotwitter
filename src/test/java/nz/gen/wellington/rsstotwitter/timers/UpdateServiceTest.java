package nz.gen.wellington.rsstotwitter.timers;

import com.google.common.collect.Sets;
import nz.gen.wellington.rsstotwitter.feeds.FeedService;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import nz.gen.wellington.rsstotwitter.twitter.TwitterService;
import nz.gen.wellington.rsstotwitter.twitter.TwitterUpdater;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class UpdateServiceTest {

    private static final String TWITTER_USERNAME = "testuser";

    private static final String FEED_URL = "http://localhost/rss";
    private static final String SECOND_FEED_URL = "http://localhost/2/rss";

    @Mock
    JobDAO jobDAO;
    @Mock
    FeedService feedService;
    @Mock
    TwitterUpdater twitterUpdater;

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
    @Mock
    private MastodonService mastodonService;
    @Mock
    private TwitterService twitterService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        jobs = new ArrayList<>();
        service = new UpdateService(jobDAO, feedService, twitterUpdater, mastodonService, twitterService);

        feed = new Feed(FEED_URL);
        account = new Account(1, TWITTER_USERNAME);
        secondFeed = new Feed(SECOND_FEED_URL);
        secondAccount = new Account(2, TWITTER_USERNAME);
        jobs.add(new FeedToTwitterJob(feed, account, Sets.newHashSet(Destination.TWITTER)));
        jobs.add(new FeedToTwitterJob(secondFeed, secondAccount, Sets.newHashSet(Destination.TWITTER)));

        when(jobDAO.getAllTweetFeedJobs()).thenReturn(jobs);
        when(twitterService.isConfigured()).thenReturn(true);
    }

    @Test
    public void shouldFetchFeedItemsAndPassThemToTheTwitterUpdaterForTweeting() throws Exception {
        when(feedService.loadFeedItems(feed)).thenReturn(feedItems);

        service.run();

        verify(twitterUpdater).updateFeed(account, feed, feedItems, Destination.TWITTER);
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

        verify(twitterUpdater).updateFeed(secondAccount, secondFeed, secondFeedItems, Destination.TWITTER);
    }

}
