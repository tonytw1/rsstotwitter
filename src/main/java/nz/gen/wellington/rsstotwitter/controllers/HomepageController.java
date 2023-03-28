package nz.gen.wellington.rsstotwitter.controllers;

import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterHistoryDAO;
import nz.gen.wellington.rsstotwitter.twitter.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomepageController {

    private final LoggedInUserFilter loggedInUserFilter;
    private final JobDAO jobDAO;
    private final TwitterHistoryDAO twitterHistoryDAO;
    private final MastodonService mastodonService;
    private final TwitterService twitterService;

    private final List<Destination> allDestinations = Lists.newArrayList(Destination.values());

    @Autowired
    public <twitterService> HomepageController(LoggedInUserFilter loggedInUserFilter, JobDAO jobDAO,
                                               TwitterHistoryDAO twitterHistoryDAO,
                                               MastodonService mastodonService,
                              TwitterService twitterService) {
        this.loggedInUserFilter = loggedInUserFilter;
        this.jobDAO = jobDAO;
        this.twitterHistoryDAO = twitterHistoryDAO;
        this.mastodonService = mastodonService;
        this.twitterService = twitterService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView homepage(HttpServletRequest request) {
        Account loggedInUser = loggedInUserFilter.getLoggedInUser(request);
        if (loggedInUser != null) {

            List<JobWithActivity> jobsWithActivity = jobDAO.getJobsForAccount(loggedInUser).stream().
                    map(job -> {
                        Feed feed = job.getFeed();
                        ActivitySummary activity = new ActivitySummary(
                                allDestinations.stream().mapToLong(destination -> twitterHistoryDAO.getNumberOfTwitsInLastHour(feed, job.getAccount(), destination)).sum(),
                                allDestinations.stream().mapToLong(destination -> twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed, job.getAccount(), destination)).sum());
                        return new JobWithActivity(job, activity);
                    }).collect(Collectors.toList());


            return new ModelAndView("feeds").
                    addObject("accounts", loggedInUserFilter.connectedAccountsFor(loggedInUser)).
                    addObject("jobs", jobsWithActivity);

        } else {
            return new ModelAndView("homepage").
                    addObject("destinations", availableDestinations());
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
