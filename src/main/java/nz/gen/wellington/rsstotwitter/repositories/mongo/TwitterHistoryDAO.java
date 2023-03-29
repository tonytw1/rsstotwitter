package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import nz.gen.wellington.rsstotwitter.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static dev.morphia.query.filters.Filters.eq;
import static dev.morphia.query.filters.Filters.gt;

@Component
public class TwitterHistoryDAO {

    private final DataStoreFactory dataStoreFactory;

    @Autowired
    public TwitterHistoryDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public boolean hasAlreadyBeenTweeted(Account account, String guid, Destination destination) {
        return !tweetsForGuid(account, guid, destination).isEmpty();
    }

    public List<TwitterEvent> tweetsForGuid(Account account, String guid, Destination destination) {
        return dataStoreFactory.getDs().
                find(TwitterEvent.class).
                filter(eq("guid", guid),
                        eq("destination", destination),
                        eq("account", account)).stream().toList();
    }

    public void markAsTweeted(Account account, FeedItem feedItem, Tweet sentTweet, Destination destination) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(),
                feedItem.getFeed(), sentTweet, destination, account);
        saveTwitterEvent(newEvent);
    }

    public List<TwitterEvent> getTweetEvents(Feed feed, Account account) {
        return dataStoreFactory.getDs().find(TwitterEvent.class).
                filter(eq("feed.url", feed.getUrl()),
                        eq("account", account)).
                stream(new FindOptions().sort(Sort.descending("date")).limit(20)).toList();
    }

    public long getNumberOfTwitsInLastHour(Feed feed, Account account, Destination destination) {
        return getNumberOfTweetsSince(feed, account, DateTime.now().minusHours(1).toDate(), destination);
    }

    public long getNumberOfTwitsInLastTwentyFourHours(Feed feed, Account account, Destination destination) {
        return getNumberOfTweetsSince(feed, account, DateTime.now().minusDays(1).toDate(), destination);
    }

    public long getNumberOfPublisherTwitsInLastTwentyFourHours(Feed feed, String publisher, Account account, Destination destination) {
        Date since = DateTime.now().minusDays(1).toDate();
        return dataStoreFactory.getDs().find(TwitterEvent.class).
                filter(gt("date", since),
                        eq("feed.url", feed.getUrl()),
                        eq("account", account),
                        eq("destination", destination),
                        eq("publisher", publisher)).count();
    }

    private long getNumberOfTweetsSince(Feed feed, Account account, Date since, Destination destination) {
        return dataStoreFactory.getDs().find(TwitterEvent.class).
                filter(gt("date", since),
                        eq("feed.url", feed.getUrl()),
                        eq("account", account),
                        eq("destination", destination)).
                count();
    }

    private void saveTwitterEvent(TwitterEvent event) {
        dataStoreFactory.getDs().save(event);
    }

}