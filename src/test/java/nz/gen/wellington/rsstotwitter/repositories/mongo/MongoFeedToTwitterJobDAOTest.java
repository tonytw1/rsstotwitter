package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MongoFeedToTwitterJobDAOTest {

    String mongoDatabase = "rsstotwittertest" + UUID.randomUUID().toString();

    DataStoreFactory dataStoreFactory = new DataStoreFactory("mongo:27017", mongoDatabase, "", "", false);
    MongoFeedToTwitterJobDAO dao = new MongoFeedToTwitterJobDAO(dataStoreFactory);

    @Test
    public void canSaveJobAndReloadById() {
        FeedToTwitterJob job = new FeedToTwitterJob();

        dao.save(job);
        assertNotNull(job.getObjectId());

        FeedToTwitterJob reloaded = dao.getByObjectId(job.getObjectId());
        assertEquals(job.getObjectId(), reloaded.getObjectId());
    }

}