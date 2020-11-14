package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.query.Query;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MongoTwitterHistoryDAO {

    private final static Logger log = Logger.getLogger(MongoTwitterHistoryDAO.class);

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoTwitterHistoryDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @SuppressWarnings("unchecked")
    public boolean hasAlreadyBeenTweeted(String guid) {
        return !dataStoreFactory.getDs().
                find(TwitterEvent.class).
                filter("guid", guid).asList().isEmpty();
    }

    public void markAsTweeted(FeedItem feedItem, Tweet sentTweet) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(), feedItem.getFeed(), sentTweet);
        saveTwitterEvent(newEvent);
    }

    public List<TwitterEvent> getTweets(Feed feed) {
        Query<TwitterEvent> limit = dataStoreFactory.getDs().find(TwitterEvent.class).limit(20);    // TODO filter by feed and ordering
        return limit.asList();
    }

    public long getNumberOfTwitsInLastHour(Feed feed) {
        return getNumberOfTweetsSince(DateTime.now().minusHours(1).toDate());
    }

    public long getNumberOfTwitsInLastTwentyFourHours(Feed feed) {
        return getNumberOfTweetsSince(DateTime.now().minusDays(1).toDate());
    }

    public int getNumberOfTwitsInLastTwentyFourHours(Feed feed, String publisher) {
        return 0;   // TODO
    }

    private void saveTwitterEvent(TwitterEvent event) {
        dataStoreFactory.getDs().save(event);
    }

    private long getNumberOfTweetsSince(Date since) {
        return dataStoreFactory.getDs().find(TwitterEvent.class).
                field("date").
                greaterThan(since)
                .count();
    }

}