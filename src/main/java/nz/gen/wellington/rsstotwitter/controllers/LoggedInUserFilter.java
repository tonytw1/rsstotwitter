package nz.gen.wellington.rsstotwitter.controllers;

import javax.servlet.http.HttpServletRequest;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

public class LoggedInUserFilter {

	private static final String LOGGED_IN_USER = "loggedInUser";

	public void setLoggedInUser(HttpServletRequest request, TwitterAccount account) {
		request.getSession().setAttribute(LOGGED_IN_USER, account);
		
	}

}
