package nz.gen.wellington.rsstotwitter.controllers;

import nz.gen.wellington.rsstotwitter.feeds.FeedService;
import nz.gen.wellington.rsstotwitter.forms.FeedDetails;
import nz.gen.wellington.rsstotwitter.model.*;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoFeedToTwitterJobDAO;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoTwitterHistoryDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.List;

@Controller
public class FeedsController {

  private final static Logger log = LogManager.getLogger(FeedsController.class);

  private final LoggedInUserFilter loggedInUserFilter;
  private final MongoFeedToTwitterJobDAO feedToTwitterJobDAO;
  private final FeedService feedService;
  private final MongoTwitterHistoryDAO twitterHistoryDAO;

  @Autowired
  public FeedsController(LoggedInUserFilter loggedInUserFilter, MongoFeedToTwitterJobDAO feedToTwitterJobDAO, FeedService feedService, MongoTwitterHistoryDAO twitterHistoryDAO) {
    this.loggedInUserFilter = loggedInUserFilter;
    this.feedToTwitterJobDAO = feedToTwitterJobDAO;
    this.feedService = feedService;
    this.twitterHistoryDAO = twitterHistoryDAO;
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newFeed(@ModelAttribute("feedDetails") FeedDetails feedDetails, HttpServletRequest request) {
    TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
    if (loggedInUser != null) {
      return renderNewFeedForm(feedDetails, loggedInUser);

    } else {
      return redirectToSignInPage();
    }
  }

  @RequestMapping(value = "/new", method = RequestMethod.POST)
  public ModelAndView newFeedSubmit(@Valid @ModelAttribute("feedDetails") FeedDetails feedDetails, BindingResult result, HttpServletRequest request) {
    TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
    if (loggedInUser != null) {
      if (result.hasErrors()) {
        log.info("Feed form errors: " + result.getAllErrors());
        return renderNewFeedForm(feedDetails, loggedInUser);
      }

      Feed feed = new Feed(feedDetails.getUrl());
      FeedToTwitterJob job = new FeedToTwitterJob(feed, loggedInUser);
      log.info("Creating job: " + job);

      feedToTwitterJobDAO.save(job);

      return new ModelAndView(new RedirectView("/"));

    } else {
      return redirectToSignInPage();
    }
  }

  @RequestMapping(value = "/feeds/{id}", method = RequestMethod.GET)
  public ModelAndView feed(@PathVariable String id, HttpServletRequest request) {
    TwitterAccount loggedInUser = loggedInUserFilter.getLoggedInUser(request);
    if (loggedInUser != null) {
      FeedToTwitterJob job = feedToTwitterJobDAO.getByObjectId(id);
      List<FeedItem> feedItems = feedService.loadFeedItems(job.getFeed());

      return new ModelAndView("feed").
              addObject("account", loggedInUser).
              addObject("job", job).
              addObject("tweetEvents", twitterHistoryDAO.getTweetEvents(job.getFeed(), loggedInUser.getId())).
              addObject("lastHour", twitterHistoryDAO.getNumberOfTwitsInLastHour(job.getFeed())).
              addObject("lastTwentyFourHours", twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(job.getFeed())).
              addObject("feedItems", feedItems);

    } else {
      return redirectToSignInPage();
    }
  }

  private ModelAndView renderNewFeedForm(FeedDetails feedDetails, TwitterAccount account) {
    return new ModelAndView("newfeed").
            addObject("feedDetails", feedDetails).
            addObject("account", account);
  }

  private ModelAndView redirectToSignInPage() {
    return new ModelAndView(new RedirectView("/"));
  }

}
