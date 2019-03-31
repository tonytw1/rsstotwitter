package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.Datastore;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;
import org.apache.log4j.Logger;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoFeedToTwitterJobDAO implements FeedToTwitterJobDAO {

    private final static Logger log = Logger.getLogger(MongoTwitterAccountDAO.class);

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoFeedToTwitterJobDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @Override
    public List<FeedToTwitterJob> getAllTweetFeedJobs() {
        final Datastore ds = dataStoreFactory.getDs();
        return ds.find(FeedToTwitterJob.class).asList();
    }

    @Override
    public List<FeedToTwitterJob> getJobsForAccount(TwitterAccount account) {
        return dataStoreFactory.getDs().
                find(FeedToTwitterJob.class).
                filter("account.id", account.getId()).asList();
    }

    @Override
    public FeedToTwitterJob getByObjectId(String id) {
        return dataStoreFactory.getDs().find(FeedToTwitterJob.class, "_id", new ObjectId(id)).get();
    }

    @Override
    public void save(FeedToTwitterJob job) {
        dataStoreFactory.getDs().save(job);
    }

}
