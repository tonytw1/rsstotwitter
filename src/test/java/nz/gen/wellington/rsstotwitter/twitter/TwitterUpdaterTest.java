package nz.gen.wellington.rsstotwitter.twitter;

import com.google.common.collect.Lists;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterHistoryDAO;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class TwitterUpdaterTest {

    @Mock
    TwitterHistoryDAO twitterHistoryDAO;
    @Mock
    TweetFromFeedItemBuilder tweetFromFeedItemBuilder;
    @Mock
    TwitterService twitterService;
    @Mock
    MastodonService mastodonService;

    @Mock
    Feed feed;

    @Mock
    Tweet tweetToSend;
    @Mock
    Tweet sentTweet;

    Account account = new Account();

    TwitterUpdater service;

    private List<FeedItem> feedItems;
    private FeedItem feedItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, account, Destination.TWITTER)).thenReturn(2L);
        feedItem = new FeedItem(feed, "title", "guid", "link", Calendar.getInstance().getTime(), "author", null);
        feedItems = Lists.newArrayList(feedItem);

        account.setToken("a-connected-twitter-token");
        account.setTokenSecret("a-connected-twitter-token-secret");

        service = new TwitterUpdater(twitterHistoryDAO, twitterService, tweetFromFeedItemBuilder, mastodonService);
    }

    @Test
    public void shouldNotTwitIfFeedWasInitiallyOverFeedRateLimit() {
        when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, account, Destination.TWITTER)).thenReturn(55L);

        service.updateFeed(account, feed, feedItems, Destination.TWITTER);

        verifyNoMoreInteractions(twitterService);
    }

    @Test
    public void shouldTweetFeedItems() throws IOException {
        when(tweetFromFeedItemBuilder.buildTweetFromFeedItem(feedItem)).thenReturn(tweetToSend);
        when(twitterService.tweet(tweetToSend, account)).thenReturn(sentTweet);

        service.updateFeed(account, feed, feedItems, Destination.TWITTER);

        verify(twitterService).tweet(tweetToSend, account);
        verify(twitterHistoryDAO).markAsTweeted(account, feedItem, sentTweet,  Destination.TWITTER);
    }

    @Test
    public void shouldNotTweetFeedItemsOlderThanOneWeek() {
        final Date oldDate = new DateTime().minusDays(10).toDate();
        FeedItem oldFeedItem = new FeedItem(feed, "title", "guid", "link", oldDate, "author", null);
        feedItems.clear();
        feedItems.add(oldFeedItem);

        service.updateFeed(account, feed, feedItems, Destination.TWITTER);

        verifyNoMoreInteractions(tweetFromFeedItemBuilder);
        verifyNoMoreInteractions(twitterService);
    }

    @Test
    public void shouldNotExceedRateLimitDuringRun() {
        when(twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, account, Destination.TWITTER)).thenReturn(29L);
    }

}
