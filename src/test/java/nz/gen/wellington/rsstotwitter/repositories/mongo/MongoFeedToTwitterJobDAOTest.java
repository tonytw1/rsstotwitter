package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MongoFeedToTwitterJobDAOTest {

    String mongoDatabase = "rsstotwittertest" + UUID.randomUUID().toString();

    @Test
    public void canSaveJobAndReloadById() {
        String mongoHost = System.getenv("MONGO_HOST");
        if (mongoHost == null) {
            mongoHost = "localhost";
        }

        DataStoreFactory dataStoreFactory = new DataStoreFactory(mongoHost + ":27017", mongoDatabase, "", "", false);
        MongoFeedToTwitterJobDAO dao = new MongoFeedToTwitterJobDAO(dataStoreFactory);

        FeedToTwitterJob job = new FeedToTwitterJob();

        dao.save(job);
        assertNotNull(job.getObjectId());

        FeedToTwitterJob reloaded = dao.getByObjectId(job.getObjectId());
        assertEquals(job.getObjectId(), reloaded.getObjectId());
    }

}