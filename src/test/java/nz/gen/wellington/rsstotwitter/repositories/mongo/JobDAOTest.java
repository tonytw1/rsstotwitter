package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.Job;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JobDAOTest {

    String mongoDatabase = "rsstotwittertest" + UUID.randomUUID().toString();

    @Test
    public void canSaveJobAndReloadById() {
        String mongoHost = System.getenv("MONGO_HOST");
        if (mongoHost == null) {
            mongoHost = "localhost";
        }

        DataStoreFactory dataStoreFactory = new DataStoreFactory(mongoHost + ":27017", mongoDatabase, "", "", false);
        JobDAO dao = new JobDAO(dataStoreFactory);

        Job job = new Job();

        dao.save(job);
        assertNotNull(job.getObjectId());

        Job reloaded = dao.getByObjectId(job.getObjectId());
        assertEquals(job.getObjectId(), reloaded.getObjectId());
    }

}