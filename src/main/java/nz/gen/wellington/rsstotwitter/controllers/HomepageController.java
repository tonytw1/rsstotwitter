package nz.gen.wellington.rsstotwitter.controllers;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomepageController {

	private LoggedInUserFilter loggedInUserFilter;
	private FeedToTwitterJobDAO feedToTwitterJobDAO;

	@Autowired
	public HomepageController(LoggedInUserFilter loggedInUserFilter, FeedToTwitterJobDAO feedToTwitterJobDAO) {
		this.loggedInUserFilter = loggedInUserFilter;
		this.feedToTwitterJobDAO = feedToTwitterJobDAO;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView homepage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("homepage");
        TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
        if (loggedInUser != null) {
        	mv.addObject("account", loggedInUser);
        	mv.addObject("jobs", feedToTwitterJobDAO.getJobsForAccount(loggedInUser));
        }
        return mv;
    }

}
