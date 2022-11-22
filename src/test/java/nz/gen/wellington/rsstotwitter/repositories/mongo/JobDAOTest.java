package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JobDAOTest {

    String mongoDatabase = "rsstotwittertest" + UUID.randomUUID();

    @Test
    public void canSaveJobAndReloadJobById() {
        String mongoHost = System.getenv("MONGO_HOST");
        if (mongoHost == null) {
            mongoHost = "localhost";
        }

        DataStoreFactory dataStoreFactory = new DataStoreFactory(mongoHost + ":27017", mongoDatabase, "", "", false);
        TwitterAccountDAO accountDAO = new TwitterAccountDAO(dataStoreFactory);
        JobDAO jobDAO = new JobDAO(dataStoreFactory);

        TwitterAccount account = new TwitterAccount();
        account.setId(123L);
        account.setUsername("a-user");
        accountDAO.saveAccount(account);

        Feed feed = new Feed("https://wellington.gen.nz/rss");

        FeedToTwitterJob job = new FeedToTwitterJob();
        job.setAccount(account);
        job.setFeed(feed);

        jobDAO.save(job);
        assertNotNull(job.getObjectId());

        FeedToTwitterJob reloaded = jobDAO.getByObjectId(job.getObjectId());
        assertEquals(job.getObjectId(), reloaded.getObjectId());
        assertEquals(feed, reloaded.getFeed());
        assertEquals(account.getId(), reloaded.getAccount().getId());
        assertEquals(account.getUsername(), reloaded.getAccount().getUsername());
    }

    @Test
    public void canLoadJobsByAccount() {
        String mongoHost = System.getenv("MONGO_HOST");
        if (mongoHost == null) {
            mongoHost = "localhost";
        }

        DataStoreFactory dataStoreFactory = new DataStoreFactory(mongoHost + ":27017", mongoDatabase, "", "", false);
        TwitterAccountDAO accountDAO = new TwitterAccountDAO(dataStoreFactory);
        JobDAO jobDAO = new JobDAO(dataStoreFactory);

        TwitterAccount account = new TwitterAccount();
        account.setId(123L);
        account.setUsername("a-user");
        accountDAO.saveAccount(account);

        Feed feed = new Feed("https://wellington.gen.nz/rss");

        FeedToTwitterJob job = new FeedToTwitterJob();
        job.setAccount(account);
        job.setFeed(feed);
        jobDAO.save(job);


        TwitterAccount anotherAccount = new TwitterAccount();
        anotherAccount.setId(456L);
        anotherAccount.setUsername("another-user");
        accountDAO.saveAccount(anotherAccount);

        Feed anotherFeed = new Feed("https://wellington.govt.nz/rss");

        FeedToTwitterJob anotherJob = new FeedToTwitterJob();
        anotherJob.setAccount(anotherAccount);
        anotherJob.setFeed(anotherFeed);
        jobDAO.save(anotherJob);

        List<FeedToTwitterJob> jobsForAccount = jobDAO.getJobsForAccount(account);

        assertEquals(1, jobsForAccount.size());
        assertEquals(account.getId(), jobsForAccount.get(0).getAccount().getId());
    }

}
