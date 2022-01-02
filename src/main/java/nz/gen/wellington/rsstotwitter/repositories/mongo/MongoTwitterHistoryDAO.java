package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MongoTwitterHistoryDAO {

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoTwitterHistoryDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @SuppressWarnings("unchecked")
    public boolean hasAlreadyBeenTweeted(String guid) {
        return !tweetsForGuid(guid).isEmpty();
    }

    public List<TwitterEvent> tweetsForGuid(String guid) {
        return dataStoreFactory.getDs().
                find(TwitterEvent.class).
                filter("guid", guid).asList();
    }

    public void markAsTweeted(FeedItem feedItem, Tweet sentTweet) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(), feedItem.getFeed(), sentTweet);
        saveTwitterEvent(newEvent);
    }

    public List<TwitterEvent> getTweetEvents(Feed feed, Long twitterUserId) {
        Query<TwitterEvent> limit = dataStoreFactory.getDs().find(TwitterEvent.class).
                filter("feed.url", feed.getUrl()).
                filter("tweet.userId", twitterUserId).
                order(Sort.descending("date")).
                limit(20);
        return limit.asList();
    }

    public long getNumberOfTwitsInLastHour(Feed feed, long twitterUserId) {
        return getNumberOfTweetsSince(feed, twitterUserId, DateTime.now().minusHours(1).toDate());
    }

    public long getNumberOfTwitsInLastTwentyFourHours(Feed feed, long twitterUserId) {
        return getNumberOfTweetsSince(feed, twitterUserId, DateTime.now().minusDays(1).toDate());
    }

    public int getNumberOfTwitsInLastTwentyFourHours(Feed feed, String publisher) {
        return 0;   // TODO
    }

    private void saveTwitterEvent(TwitterEvent event) {
        dataStoreFactory.getDs().save(event);
    }

    private long getNumberOfTweetsSince(Feed feed, long twitterUserId, Date since) {
        return dataStoreFactory.getDs().find(TwitterEvent.class).
                field("date").
                greaterThan(since).
                filter("feed.url", feed.getUrl()).
                filter("tweet.userId", twitterUserId).
                count();
    }

}