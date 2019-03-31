package nz.gen.wellington.rsstotwitter.controllers;

import nz.gen.wellington.rsstotwitter.forms.FeedDetails;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoTwitterAccountDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class HomepageController {

	private final static Logger log = Logger.getLogger(HomepageController.class);

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

	@RequestMapping(value = "/feeds/new", method = RequestMethod.GET)
	public ModelAndView newFeed(@ModelAttribute("feedDetails") FeedDetails feedDetails, HttpServletRequest request) {
		return renderNewFeedForm(feedDetails);
	}

	@RequestMapping(value = "/feeds/new", method = RequestMethod.POST)
	public ModelAndView newFeedSubmit(@Valid @ModelAttribute("feedDetails") FeedDetails feedDetails, BindingResult result, HttpServletRequest request) {
		TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
		if (loggedInUser != null) {

			if (result.hasErrors()) {
				log.info("Feed form errors: " + result.getAllErrors());
				return renderNewFeedForm(feedDetails);
			}


			Feed feed = new Feed(feedDetails.getUrl());
			FeedToTwitterJob job = new FeedToTwitterJob(feed, loggedInUser);
			log.info("Creating job: " + job);

			feedToTwitterJobDAO.save(job);

			return new ModelAndView(new RedirectView("/"));

		} else {
			log.warn("Not signed in");
			return new ModelAndView(new RedirectView("/"));
		}
	}

	@RequestMapping(value = "/feeds/{id}", method = RequestMethod.GET)
	public ModelAndView feed(@PathVariable String id, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("feed");
		TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
		if (loggedInUser != null) {
			mv.addObject("account", loggedInUser);
			mv.addObject("feed", feedToTwitterJobDAO.getByObjectId(id));
		}
		return mv;
	}

	private ModelAndView renderNewFeedForm(FeedDetails feedDetails) {
		return new ModelAndView("newfeed").addObject("feedDetails", feedDetails);
	}

}
