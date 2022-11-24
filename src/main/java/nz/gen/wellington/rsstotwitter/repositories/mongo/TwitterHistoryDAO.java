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
    public boolean hasAlreadyBeenTweeted(Account account, String guid, Destination destination) {
        return !tweetsForGuid(account, guid, destination).isEmpty();
    }

    public List<TwitterEvent> tweetsForGuid(Account account, String guid, Destination destination) {
        return dataStoreFactory.getDs().
                find(TwitterEvent.class).
                filter("guid", guid).
                filter("destination", destination).
                filter("account", account).
                asList();
    }

    public void markAsTweeted(Account account, FeedItem feedItem, Tweet sentTweet, Destination destination) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(),
                feedItem.getFeed(), sentTweet, destination, account);
        saveTwitterEvent(newEvent);
    }

    public List<TwitterEvent> getTweetEvents(Feed feed, Account account) {
        Query<TwitterEvent> limit = dataStoreFactory.getDs().find(TwitterEvent.class).
                filter("feed.url", feed.getUrl()).
                filter("account", account).
                order(Sort.descending("date")).
                limit(20);
        return limit.asList();
    }

    public long getNumberOfTwitsInLastHour(Feed feed, Account account, Destination destination) {
        return getNumberOfTweetsSince(feed, account, DateTime.now().minusHours(1).toDate(), destination);
    }

    public long getNumberOfPublisherTwitsInLastTwentyFourHours(Feed feed, Account account, Destination destination) {
        return getNumberOfTweetsSince(feed, account, DateTime.now().minusDays(1).toDate(), destination);
    }

    public int getNumberOfPublisherTwitsInLastTwentyFourHours(Feed feed, String publisher, Account account, Destination destination) {
        return 0;   // TODO
    }

    private void saveTwitterEvent(TwitterEvent event) {
        dataStoreFactory.getDs().save(event);
    }

    private long getNumberOfTweetsSince(Feed feed, Account account, Date since, Destination destination) {
        return dataStoreFactory.getDs().find(TwitterEvent.class).
                field("date").
                greaterThan(since).
                filter("feed.url", feed.getUrl()).
                filter("account", account).
                count();
    }

}