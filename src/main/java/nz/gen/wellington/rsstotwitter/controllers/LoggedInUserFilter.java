package nz.gen.wellington.rsstotwitter.controllers;

import javax.servlet.http.HttpServletRequest;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.springframework.stereotype.Component;

@Component
public class LoggedInUserFilter {

    private static final String LOGGED_IN_USER = "loggedInUser";

    public void setLoggedInUser(HttpServletRequest request, TwitterAccount account) {
        request.getSession().setAttribute(LOGGED_IN_USER, account);
    }

    public TwitterAccount getLoggedInUser(HttpServletRequest request) {
        return (TwitterAccount) request.getSession().getAttribute(LOGGED_IN_USER);
    }

    public void removeLoggedInUser(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGGED_IN_USER);
    }

}
