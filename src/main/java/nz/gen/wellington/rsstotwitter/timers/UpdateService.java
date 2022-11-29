package nz.gen.wellington.rsstotwitter.timers;

import com.google.common.collect.Lists;
import nz.gen.wellington.rsstotwitter.feeds.FeedService;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.Destination;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import nz.gen.wellington.rsstotwitter.twitter.TwitterService;
import nz.gen.wellington.rsstotwitter.twitter.TwitterUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateService implements Runnable {

    private final static Logger log = LogManager.getLogger(UpdateService.class);

    private final JobDAO feedToTwitterJobDAO;
    private final FeedService feedService;
    private final TwitterUpdater twitterUpdater;
    private final MastodonService mastodonService;
    private final TwitterService twitterService;

    @Autowired
    public UpdateService(JobDAO tweetFeedJobDAO, FeedService feedService, TwitterUpdater twitterUpdater,
                         MastodonService mastodonService, TwitterService twitterService) {
        this.feedToTwitterJobDAO = tweetFeedJobDAO;
        this.feedService = feedService;
        this.twitterUpdater = twitterUpdater;
        this.mastodonService = mastodonService;
        this.twitterService = twitterService;
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

    private void processJob(FeedToTwitterJob job) {
        // Load the feed items for this job.
        // Send them to each destination for this job.
        // Rate limiting is per job

        final Feed feed = job.getFeed();
        log.info("Running job: " + feed.getUrl() + " -> @" + job.getAccount().getUsername());
        try {
            List<FeedItem> feedItems = feedService.loadFeedItems(feed);
            if (feedItems != null && !feedItems.isEmpty()) {
                for (Destination destination : job.getDestinations()) {
                    // Filter this jobs requested destinations against currently available destinations
                    if (availableDestinations().contains(destination)) {
                        twitterUpdater.updateFeed(job.getAccount(), feed, feedItems, destination);
                    } else {
                        log.warn("Omitting unconfigured destination " + destination + " for job " + job.getFeed().getUrl());
                    }
                }

            } else {
                log.warn("Failed to load feed items from feed url or feed contained no items: " + feed.getUrl());
            }

        } catch (Exception e) {
            log.error("Uncaught Error running process job", e);
        }
    }

    private List<Destination> availableDestinations() {
        List<Destination> available = Lists.newArrayList();
        if (mastodonService.isConfigured()) {
            available.add(Destination.MASTODON);
        }
        if (twitterService.isConfigured()) {
            available.add(Destination.TWITTER);
        }
        return available;
    }

}
