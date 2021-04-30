package nz.gen.wellington.rsstotwitter.repositories.mongo;

import dev.morphia.Datastore;
import nz.gen.wellington.rsstotwitter.model.Job;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobDAO {

    private final static Logger log = LogManager.getLogger(MongoTwitterAccountDAO.class);

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public JobDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public List<Job> getAllTweetFeedJobs() {
        final Datastore ds = dataStoreFactory.getDs();
        return ds.find(Job.class).asList();
    }

    @SuppressWarnings("unchecked")
    public List<Job> getJobsForAccount(TwitterAccount account) {
        return dataStoreFactory.getDs().
                find(Job.class).
                filter("account.id", account.getId()).asList();
    }

    public Job getByObjectId(String id) {
        return dataStoreFactory.getDs().find(Job.class, "_id", new ObjectId(id)).get();
    }

    public void save(Job job) {
        dataStoreFactory.getDs().save(job);
    }

}
