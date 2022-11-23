package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import nz.gen.wellington.rsstotwitter.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TwitterHistoryDAO {

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public TwitterHistoryDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @SuppressWarnings("unchecked")
    public boolean hasAlreadyBeenTweeted(String guid, Destination destination) { // TODO should be by account as well.
        return !tweetsForGuid(guid, destination).isEmpty();
    }

    public List<TwitterEvent> tweetsForGuid(String guid, Destination destination) {
        return dataStoreFactory.getDs().
                find(TwitterEvent.class).
                filter("guid", guid).
                filter("destination", destination).
                asList();
    }

    public void markAsTweeted(FeedItem feedItem, Tweet sentTweet, Destination destination) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(),
                feedItem.getFeed(), sentTweet, destination);
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