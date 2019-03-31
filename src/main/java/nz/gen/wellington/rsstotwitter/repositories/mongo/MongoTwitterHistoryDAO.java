package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.TwitterHistoryDAO;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoTwitterHistoryDAO implements TwitterHistoryDAO {

    private final static Logger log = Logger.getLogger(MongoTwitterHistoryDAO.class);

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoTwitterHistoryDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }


    @Override
    public boolean hasAlreadyBeenTwittered(String guid) {
        return !dataStoreFactory.getDs().
                find(TwitterEvent.class).
                filter("guid", guid).asList().isEmpty();
    }

    @Override
    public void markAsTwittered(FeedItem feedItem, Tweet sentTweet) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(), feedItem.getFeed(), sentTweet);
        saveTwitterEvent(newEvent);
    }

    @Override
    public int getNumberOfTwitsInLastTwentyFourHours(Feed feed) {
        return 0;   // TODO
    }

    @Override
    public int getNumberOfTwitsInLastTwentyFourHours(Feed feed, String publisher) {
        return 0;   // TODO
    }

    private void saveTwitterEvent(TwitterEvent event) {
        dataStoreFactory.getDs().save(event);
    }

}