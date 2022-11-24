package nz.gen.wellington.rsstotwitter.controllers.signin;

import nz.gen.wellington.rsstotwitter.controllers.LoggedInUserFilter;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterAccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MastodonSigninController extends AbstractSigninController {

    @Autowired
    public MastodonSigninController(TwitterAccountDAO accountDAO, MastodonSignHandler signinHandler,
                                    LoggedInUserFilter loggedInUserFilter,
                                    @Value("${homepage.url}") String homePageUrl) {
        this.accountDAO = accountDAO;
        this.signinHandler = signinHandler;
        this.loggedInUserFilter = loggedInUserFilter;
        this.homePageUrl = homePageUrl;
    }

    @Override
    @RequestMapping(value = "/mastodon/oauth/login", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.login(request, response);
    }

    @Override
    @RequestMapping(value = "/mastodon/oauth/callback", method = RequestMethod.GET)
    public ModelAndView callback(HttpServletRequest request) {
        return super.callback(request);
    }
}
