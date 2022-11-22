package nz.gen.wellington.rsstotwitter.controllers;

import javax.servlet.http.HttpServletRequest;

import nz.gen.wellington.rsstotwitter.model.Account;
import org.springframework.stereotype.Component;

@Component
public class LoggedInUserFilter {

    private static final String LOGGED_IN_USER = "loggedInUser";

    public void setLoggedInUser(HttpServletRequest request, Account account) {
        request.getSession().setAttribute(LOGGED_IN_USER, account);
    }

    public Account getLoggedInUser(HttpServletRequest request) {
        return (Account) request.getSession().getAttribute(LOGGED_IN_USER);
    }

    public void removeLoggedInUser(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGGED_IN_USER);
    }

}
