package nz.gen.wellington.rsstotwitter.controllers.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.gen.wellington.rsstotwitter.controllers.LoggedInUserFilter;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

public class SigninController extends MultiActionController {

	private static Logger log = Logger.getLogger(SigninController.class);
	
	private AccountDAO accountDAO;
	private SigninHandler signinHandler;
	private LoggedInUserFilter loggedInUserFilter;	
	private String homePageUrl;
	
	public SigninController(AccountDAO accountDAO, SigninHandler signinHandler, LoggedInUserFilter loggedInUserFilter) {
		this.accountDAO = accountDAO;
		this.signinHandler = signinHandler;
		this.loggedInUserFilter = loggedInUserFilter;
	}
	
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}
	
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView loginView = signinHandler.getLoginView(request, response);
		if (loginView != null) {
			return loginView;
		}
		return signinErrorView(request);		
	}
	
	@Transactional
	public ModelAndView callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final Object externalIdentifier = signinHandler.getExternalUserIdentifierFromCallbackRequest(request);
		if (externalIdentifier != null) {
			log.info("External user identifier is: " + externalIdentifier.toString());			
			
			TwitterAccount account = signinHandler.getUserByExternalIdentifier(externalIdentifier);
			
			final boolean localAccountAlreadyExistsForThisUser = account != null;
			if (!localAccountAlreadyExistsForThisUser) {
				log.info("Creating new user account for external identifier: " + externalIdentifier.toString());
				account = createNewUser(externalIdentifier);				
			} else {
				log.info("Existing local account found for external identifier: " + externalIdentifier.toString());
				signinHandler.decorateUserWithExternalSigninIdentifier(account, externalIdentifier);
			}
			
			loggedInUserFilter.setLoggedInUser(request, account);
			return new ModelAndView(new RedirectView("/"));								
		}			
		return signinErrorView(request);
	}
	
	private ModelAndView signinErrorView(HttpServletRequest request) {
		return new ModelAndView(new RedirectView(homePageUrl));
	}
		
	private TwitterAccount createNewUser(Object externalIdentifier) {
		TwitterAccount newUser = new TwitterAccount();
		signinHandler.decorateUserWithExternalSigninIdentifier(newUser, externalIdentifier);
		accountDAO.saveAccount(newUser);
		log.info("Created new user with external identifier: " + externalIdentifier.toString());
		return newUser;
	}
	
}
