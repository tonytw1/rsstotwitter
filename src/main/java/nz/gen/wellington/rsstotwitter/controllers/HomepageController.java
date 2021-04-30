package nz.gen.wellington.rsstotwitter.controllers;

import nz.gen.wellington.rsstotwitter.model.ActivitySummary;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.JobWithActivity;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.mongo.JobDAO;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoTwitterHistoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomepageController {

    private final LoggedInUserFilter loggedInUserFilter;
    private final JobDAO jobDAO;
    private final MongoTwitterHistoryDAO twitterHistoryDAO;

    @Autowired
    public HomepageController(LoggedInUserFilter loggedInUserFilter, JobDAO jobDAO,
                              MongoTwitterHistoryDAO twitterHistoryDAO) {
        this.loggedInUserFilter = loggedInUserFilter;
        this.jobDAO = jobDAO;
        this.twitterHistoryDAO = twitterHistoryDAO;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView homepage(HttpServletRequest request) {
        TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
        if (loggedInUser != null) {

            List<JobWithActivity> jobsWithActivity = jobDAO.getJobsForAccount(loggedInUser).stream().
                    map(job -> {
                        Feed feed = job.getFeed();
                        ActivitySummary activity = new ActivitySummary(
                                twitterHistoryDAO.getNumberOfTwitsInLastHour(feed),
                                twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(feed));
                        return new JobWithActivity(job, activity);
                    }).collect(Collectors.toList());

            return new ModelAndView("feeds").
                    addObject("account", loggedInUser).
                    addObject("jobs", jobsWithActivity);

        } else {
            return new ModelAndView("homepage");
        }
    }

}
