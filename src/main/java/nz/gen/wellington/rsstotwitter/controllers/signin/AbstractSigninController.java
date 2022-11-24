package nz.gen.wellington.rsstotwitter.controllers.signin;

import nz.gen.wellington.rsstotwitter.controllers.LoggedInUserFilter;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterAccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractSigninController<T> {

    protected SigninHandler<T> signinHandler;
    protected LoggedInUserFilter loggedInUserFilter;
    protected String  homePageUrl;
    protected TwitterAccountDAO accountDAO;

    private final static Logger log = LogManager.getLogger(AbstractSigninController.class);

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView loginView = signinHandler.getLoginView(request, response);
        if (loginView != null) {
            return loginView;
        }
        log.warn("Null sign in view; returning error");
        return signinErrorView(request);
    }

    public ModelAndView callback(HttpServletRequest request) {
        final T externalIdentifier = signinHandler.getExternalUserIdentifierFromCallbackRequest(request);
        if (externalIdentifier != null) {
            log.info("External user identifier is: " + externalIdentifier);

            Account account = signinHandler.getUserByExternalIdentifier(externalIdentifier);

            final boolean localAccountAlreadyExistsForThisUser = account != null;
            if (!localAccountAlreadyExistsForThisUser) {
                // There is no local user for this external user?
                // If not append to the currently signed in user or e an entirely new user
                Account loggedInUser = loggedInUserFilter.getLoggedInUser(request);
                if (loggedInUser != null) {
                    log.info("Attaching external user to currently logged in local user");
                    account = loggedInUser;

                } else {
                    log.info("Creating new user account for external identifier: " + externalIdentifier);
                    account = createNewUser(externalIdentifier);
                }

            } else {
                log.info("Existing local account found for external identifier: " + externalIdentifier);
            }

            // Always redecorate to update access tokens and external account details if the have changed
            signinHandler.decorateUserWithExternalSigninIdentifier(account, externalIdentifier);
            accountDAO.saveAccount(account);

            loggedInUserFilter.setLoggedInUser(request, account);
            return new ModelAndView(new RedirectView(homePageUrl));
        }

        return signinErrorView(request);
    }

    private ModelAndView signinErrorView(HttpServletRequest request) {
        log.warn("Sign in error; redirecting to home page");
        return new ModelAndView(new RedirectView(homePageUrl));
    }

    private Account createNewUser(T externalIdentifier) {
        Account newUser = new Account();
        signinHandler.decorateUserWithExternalSigninIdentifier(newUser, externalIdentifier);
        accountDAO.saveAccount(newUser);
        log.info("Created new user with external identifier: " + externalIdentifier.toString());
        return newUser;
    }
}
