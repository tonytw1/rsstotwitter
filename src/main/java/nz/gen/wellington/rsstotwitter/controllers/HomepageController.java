package nz.gen.wellington.rsstotwitter.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class HomepageController extends MultiActionController {

	private LoggedInUserFilter loggedInUserFilter;
	private FeedToTwitterJobDAO feedToTwitterJobDAO;
	
	public HomepageController(LoggedInUserFilter loggedInUserFilter, FeedToTwitterJobDAO feedToTwitterJobDAO) {
		this.loggedInUserFilter = loggedInUserFilter;
		this.feedToTwitterJobDAO = feedToTwitterJobDAO;
	}
	
	public ModelAndView homepage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("homepage");
        TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
        if (loggedInUser != null) {
        	mv.addObject("account", loggedInUser);
        	mv.addObject("jobs", feedToTwitterJobDAO.getJobsForAccount(loggedInUser));
        }
        return mv;
    }
	
}
