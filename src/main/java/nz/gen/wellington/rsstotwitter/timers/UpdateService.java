package nz.gen.wellington.rsstotwitter.timers;

import nz.gen.wellington.rsstotwitter.feeds.FeedService;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Job;
import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateService implements Runnable {

    private final static Logger log = LogManager.getLogger(UpdateService.class);

    private JobDAO feedToTwitterJobDAO;
    private FeedService feedService;
    private Updater twitterUpdater;

    @Autowired
    public UpdateService(JobDAO tweetFeedJobDAO, FeedService feedService, Updater twitterUpdater) {
        this.feedToTwitterJobDAO = tweetFeedJobDAO;
        this.feedService = feedService;
        this.twitterUpdater = twitterUpdater;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void run() {
        try {
            log.info("Starting feed to twitter update.");
            feedToTwitterJobDAO.getAllTweetFeedJobs().forEach(this::processJob);

        } catch (Exception e) {
            log.error("Uncaught Error running update task", e);
        }
    }

    private void processJob(Job job) {
        final Feed feed = job.getFeed();
        log.info("Running feed to twitter job: " + feed.getUrl() + " -> " + job.getAccount().getUsername());
        try {
            List<FeedItem> feedItems = feedService.loadFeedItems(feed);
            if (feedItems != null && !feedItems.isEmpty()) {
                twitterUpdater.updateFeed(feed, feedItems, job.getAccount());

            } else {
                log.warn("Failed to load feed items from feed url or feed contained no items: " + feed.getUrl());
            }

        } catch (Exception e) {
            log.error("Uncaught Error running process job", e);
        }
    }

}
