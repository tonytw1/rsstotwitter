package nz.gen.wellington.rsstotwitter.repositories.mongo;

import com.google.common.collect.Sets;
import nz.gen.wellington.rsstotwitter.model.Destination;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.Account;
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
        System.out.println(mongoDatabase);

        DataStoreFactory dataStoreFactory = new DataStoreFactory(mongoHost + ":27017", mongoDatabase, "", "", false);
        TwitterAccountDAO accountDAO = new TwitterAccountDAO(dataStoreFactory);
        JobDAO jobDAO = new JobDAO(dataStoreFactory);

        Account account = new Account();
        account.setId(123L);
        account.setUsername("a-user");
        accountDAO.saveAccount(account);

        Feed feed = new Feed("https://wellington.gen.nz/rss");

        FeedToTwitterJob job = new FeedToTwitterJob(feed, account, Sets.newHashSet(Destination.TWITTER));
        jobDAO.save(job);
        assertNotNull(job.getObjectId());

        FeedToTwitterJob reloaded = jobDAO.getByObjectId(job.getObjectId());
        assertEquals(job.getObjectId(), reloaded.getObjectId());
        assertEquals(feed, reloaded.getFeed());
        assertEquals(account.getId(), reloaded.getAccount().getId());
        assertEquals(account.getUsername(), reloaded.getAccount().getUsername());
        assertEquals(Sets.newHashSet(Destination.TWITTER), reloaded.getDestination());
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

        Account account = new Account();
        account.setId(123L);
        account.setUsername("a-user");
        accountDAO.saveAccount(account);

        Feed feed = new Feed("https://wellington.gen.nz/rss");

        FeedToTwitterJob job = new FeedToTwitterJob(feed, account, Sets.newHashSet(Destination.TWITTER));
        jobDAO.save(job);

        Account anotherAccount = new Account();
        anotherAccount.setId(456L);
        anotherAccount.setUsername("another-user");
        accountDAO.saveAccount(anotherAccount);

        Feed anotherFeed = new Feed("https://wellington.govt.nz/rss");

        FeedToTwitterJob anotherJob = new FeedToTwitterJob(anotherFeed, anotherAccount, Sets.newHashSet(Destination.TWITTER));
        jobDAO.save(anotherJob);

        List<FeedToTwitterJob> jobsForAccount = jobDAO.getJobsForAccount(account);

        assertEquals(1, jobsForAccount.size());
        FeedToTwitterJob reloadedJob = jobsForAccount.get(0);
        assertEquals(account.getId(), reloadedJob.getAccount().getId());
    }

}
