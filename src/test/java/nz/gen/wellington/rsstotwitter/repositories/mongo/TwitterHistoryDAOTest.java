package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TwitterHistoryDAOTest {

    String mongoDatabase = "rsstotwittertest" + UUID.randomUUID();

    @Test
    public void canRecordTweetedItemsByFeedAndGUID() {
        String mongoHost = System.getenv("MONGO_HOST");
        if (mongoHost == null) {
            mongoHost = "localhost";
        }

        DataStoreFactory dataStoreFactory = new DataStoreFactory(mongoHost + ":27017", mongoDatabase, "", "", false);
        TwitterHistoryDAO twitterHistoryDAO = new TwitterHistoryDAO(dataStoreFactory);

        Feed feed = new Feed("https://wellington.gen.nz/rss");
        String link = "https://wellington.gen.nz/a-post";
        FeedItem feedItem = new FeedItem(feed, "A post", link, link, null, null, null);
        Tweet tweet = new Tweet();

        twitterHistoryDAO.markAsTweeted(feedItem, tweet);

        assertTrue(twitterHistoryDAO.hasAlreadyBeenTweeted(link));
        assertFalse(twitterHistoryDAO.hasAlreadyBeenTweeted("http://localhost/not-seen-before"));
    }

}