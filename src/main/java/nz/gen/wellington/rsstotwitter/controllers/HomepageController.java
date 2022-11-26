package nz.gen.wellington.rsstotwitter.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterHistoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomepageController {

    private final LoggedInUserFilter loggedInUserFilter;
    private final JobDAO jobDAO;
    private final TwitterHistoryDAO twitterHistoryDAO;

    private final Set<Destination> allDestinations = Sets.newHashSet(Destination.TWITTER, Destination.MASTODON);

    @Autowired
    public HomepageController(LoggedInUserFilter loggedInUserFilter, JobDAO jobDAO,
                              TwitterHistoryDAO twitterHistoryDAO) {
        this.loggedInUserFilter = loggedInUserFilter;
        this.jobDAO = jobDAO;
        this.twitterHistoryDAO = twitterHistoryDAO;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView homepage(HttpServletRequest request) {
        Account loggedInUser = loggedInUserFilter.getLoggedInUser(request);
        if (loggedInUser != null) {

            List<JobWithActivity> jobsWithActivity = jobDAO.getJobsForAccount(loggedInUser).stream().
                    map(job -> {
                        Feed feed = job.getFeed();
                        ActivitySummary activity = new ActivitySummary(
                                allDestinations.stream().mapToLong( destination -> twitterHistoryDAO.getNumberOfTwitsInLastHour(feed, job.getAccount(), destination)).sum(),
                                allDestinations.stream().mapToLong( destination -> twitterHistoryDAO.getNumberOfPublisherTwitsInLastTwentyFourHours(feed, job.getAccount(), destination)).sum());
                        return new JobWithActivity(job, activity);
                    }).collect(Collectors.toList());


            return new ModelAndView("feeds").
                    addObject("accounts", connectedAccountsFor(loggedInUser)).
                    addObject("jobs", jobsWithActivity);

        } else {
            return new ModelAndView("homepage");
        }
    }

    private List<ConnectedAccount> connectedAccountsFor(Account account) {
        List<ConnectedAccount> accounts = Lists.newArrayList();
        if (account.getUsername() != null) {
            accounts.add(new ConnectedAccount(account.getUsername(), Destination.TWITTER, "https://twitter.com/" + account.getUsername()));
        }
        if (account.getMastodonUsername() != null) {
            accounts.add(new ConnectedAccount(account.getMastodonUsername(), Destination.MASTODON, account.getMastodonUrl()));
        }
        return accounts;
    }

}
