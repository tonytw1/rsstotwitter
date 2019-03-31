package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import java.util.List;

public interface FeedToTwitterJobDAO {
    List<FeedToTwitterJob> getAllTweetFeedJobs();

    @SuppressWarnings("unchecked")
    List<FeedToTwitterJob> getJobsForAccount(TwitterAccount account);

    FeedToTwitterJob getByObjectId(String id);

    void save(FeedToTwitterJob job);
}
