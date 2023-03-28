package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.Datastore;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobDAO {

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public JobDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public List<FeedToTwitterJob> getAllTweetFeedJobs() {
        final Datastore ds = dataStoreFactory.getDs();
        return ds.find(FeedToTwitterJob.class).asList();
    }

    @SuppressWarnings("unchecked")
    public List<FeedToTwitterJob> getJobsForAccount(Account account) {
        return dataStoreFactory.getDs().
                find(FeedToTwitterJob.class).
                filter("account", account).asList();
    }

    public FeedToTwitterJob getByObjectId(String id) {
        return dataStoreFactory.getDs().find(FeedToTwitterJob.class, "_id", new ObjectId(id)).get();
    }

    public void save(FeedToTwitterJob job) {
        dataStoreFactory.getDs().save(job);
    }

}
