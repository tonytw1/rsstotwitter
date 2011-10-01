package nz.gen.wellington.rsstotwitter.controllers.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.springframework.web.servlet.ModelAndView;

public interface SigninHandler {

	public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	public Object getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request);
	public TwitterAccount getUserByExternalIdentifier(Object externalIdentifier);
	public void decorateUserWithExternalSigninIdentifier(TwitterAccount account, Object externalIdentifier);
	
}
