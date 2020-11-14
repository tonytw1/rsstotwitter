package nz.gen.wellington.rsstotwitter.controllers;

import nz.gen.wellington.rsstotwitter.feeds.FeedService;
import nz.gen.wellington.rsstotwitter.forms.FeedDetails;
import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoFeedToTwitterJobDAO;
import nz.gen.wellington.rsstotwitter.repositories.mongo.MongoTwitterHistoryDAO;
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
import java.util.List;

@Controller
public class FeedsController {

  private final static Logger log = Logger.getLogger(FeedsController.class);

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
  public ModelAndView newFeed(@ModelAttribute("feedDetails") FeedDetails feedDetails) {
    return renderNewFeedForm(feedDetails);
  }

  @RequestMapping(value = "/new", method = RequestMethod.POST)
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
      FeedToTwitterJob job = feedToTwitterJobDAO.getByObjectId(id);
      mv.addObject("job", job);

      mv.addObject("tweets", twitterHistoryDAO.getTweets(job.getFeed()));
      mv.addObject("lastHour", twitterHistoryDAO.getNumberOfTwitsInLastHour(job.getFeed()));
      mv.addObject("lastTwentyFourHours", twitterHistoryDAO.getNumberOfTwitsInLastTwentyFourHours(job.getFeed()));

      List<FeedItem> feedItems = feedService.loadFeedItems(job.getFeed());
      mv.addObject("feedItems", feedItems);
    }
    return mv;
  }

  private ModelAndView renderNewFeedForm(FeedDetails feedDetails) {
    return new ModelAndView("newfeed").addObject("feedDetails", feedDetails);
  }

}
