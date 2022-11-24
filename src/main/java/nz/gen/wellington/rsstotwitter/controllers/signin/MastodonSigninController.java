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
public class MastodonSigninController {

    private final static Logger log = LogManager.getLogger(MastodonSigninController.class);

    private final TwitterAccountDAO accountDAO;
    private final SigninHandler signinHandler;
    private final LoggedInUserFilter loggedInUserFilter;
    private final String homePageUrl;

    @Autowired
    public MastodonSigninController(TwitterAccountDAO accountDAO, MastodonSignHandler signinHandler,
                                    LoggedInUserFilter loggedInUserFilter,
                                    @Value("${homepage.url}") String homePageUrl) {
        this.accountDAO = accountDAO;
        this.signinHandler = signinHandler;
        this.loggedInUserFilter = loggedInUserFilter;
        this.homePageUrl = homePageUrl;
        log.info("Home page url: " + homePageUrl);
    }

    @RequestMapping(value = "/mastodon/oauth/login", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView loginView = signinHandler.getLoginView(request, response);
        if (loginView != null) {
            return loginView;
        }
        log.warn("Null sign in view; returning error");
        return signinErrorView(request);
    }

    @RequestMapping(value = "/mastodon/oauth/callback", method = RequestMethod.GET)
    public ModelAndView callback(HttpServletRequest request) {
        final Object externalIdentifier = signinHandler.getExternalUserIdentifierFromCallbackRequest(request);
        if (externalIdentifier != null) {
            log.info("External user identifier is: " + externalIdentifier);

            Account account = signinHandler.getUserByExternalIdentifier(externalIdentifier);

            final boolean localAccountAlreadyExistsForThisUser = account != null;
            if (!localAccountAlreadyExistsForThisUser) {
                log.info("Creating new user account for external identifier: " + externalIdentifier);
                account = createNewUser(externalIdentifier);

            } else {
                log.info("Existing local account found for external identifier: " + externalIdentifier);
                signinHandler.decorateUserWithExternalSigninIdentifier(account, externalIdentifier);
            }

            loggedInUserFilter.setLoggedInUser(request, account);
            return new ModelAndView(new RedirectView(homePageUrl));
        }

        return signinErrorView(request);
    }

    private Account createNewUser(Object externalIdentifier) {
        Account newUser = new Account();
        signinHandler.decorateUserWithExternalSigninIdentifier(newUser, externalIdentifier);
        accountDAO.saveAccount(newUser);
        log.info("Created new user with external identifier: " + externalIdentifier.toString());
        return newUser;
    }

    private ModelAndView signinErrorView(HttpServletRequest request) {
        log.warn("Sign in error; redirecting to home page");
        return new ModelAndView(new RedirectView(homePageUrl));
    }

}
