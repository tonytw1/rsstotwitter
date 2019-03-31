package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoTweetDAO implements TweetDAO {

    private final static Logger log = Logger.getLogger(MongoTweetDAO.class);

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoTweetDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @Override
    public void saveTweet(Tweet tweet) {
        dataStoreFactory.getDs().save(tweet);
    }

}