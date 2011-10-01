package nz.gen.wellington.rsstotwitter.controllers.signin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import twitter4j.http.AccessToken;

public class TwitterLoginHandler implements SigninHandler {

	private static Logger log = Logger.getLogger(TwitterLoginHandler.class);
	
	private AccountDAO accountDAO;
	private TwitterService twitterService;
	private OAuthService oauthService;
	
	private String consumerKey;
	private String consumerSecret;
	private Map<String, Token> tokens;

	
	public TwitterLoginHandler(AccountDAO accountDAO, TwitterService twitterService, OAuthService oauthService) {
		this.accountDAO = accountDAO;
		this.twitterService = twitterService;
		this.oauthService = oauthService;
		this.tokens = new HashMap<String, Token>();
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	
	@Override
	public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {			
			log.info("Getting request token");			
			OAuthService service = getOauthService();
			
			Token requestToken = service.getRequestToken();		
			if (requestToken != null) {
				log.info("Got request token: " + requestToken.getToken());
				tokens.put(requestToken.getToken(), requestToken);
				
				final String authorizeUrl = service.getAuthorizationUrl(requestToken);		
				log.info("Redirecting user to authorize url : " + authorizeUrl);
				RedirectView redirectView = new RedirectView(authorizeUrl);
				return new ModelAndView(redirectView);
			}
			
		} catch (Exception e) {
			log.warn("Failed to obtain request token.", e);
		}
		return null;	
	}
	
	@Override
	public Object getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request) {
		if (request.getParameter("oauth_token") != null && request.getParameter("oauth_verifier") != null) {
			final String token = request.getParameter("oauth_token");
			final String verifier = request.getParameter("oauth_verifier");

			log.info("oauth_token: " + token);
			log.info("oauth_verifier: " + verifier);
			
			log.info("Looking for request token: " + token);
			Token requestToken = tokens.get(token);
			if (requestToken != null) {
				log.info("Found stored request token: " + requestToken.getToken());
				
				log.debug("Exchanging request token for access token");
				
				OAuthService service = getOauthService();
				Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));
				
				if (accessToken != null) {
					log.info("Got access token: '" + accessToken.getToken() + "', '" + accessToken.getSecret() + "'");
					tokens.remove(requestToken.getToken());
					
					log.debug("Using access token to lookup twitter user details");
					twitter4j.User twitterUser = twitterService.getTwitteUserCredentials(new AccessToken(accessToken.getToken(), accessToken.getSecret()));
					if (twitterUser != null) {
						return twitterUser;
					} else {
						log.warn("Failed up obtain twitter user details");
					}
					
				} else {
					log.warn("Could not get access token for: " + requestToken.getToken());
				}								
			} else {
				log.warn("Could not find request token for: " + token);
			}		
		
		} else {
			log.error("oauth token or verifier missing from callback request");
		}
		return null;
	}
		
	@Override
	public TwitterAccount getUserByExternalIdentifier(Object externalIdentifier) {
		twitter4j.User twitterUser = (twitter4j.User) externalIdentifier;
		return accountDAO.getUserByTwitterId(twitterUser.getId());
	}
	
	@Override
	public void decorateUserWithExternalSigninIdentifier(TwitterAccount account, Object externalIdentifier) {		
		twitter4j.User twitterUser = (twitter4j.User) externalIdentifier;
		if (account.getUsername() == null) {
			final String twitterScreenName = twitterUser.getScreenName();
			account.setUsername(twitterScreenName);			
		}
		account.setId(twitterUser.getId());	// TODO potential issue here - make new column
	}
	
	private OAuthService getOauthService() {
		if (oauthService == null) {
			log.info("Building oauth service with consumer key and consumer secret: " + consumerKey + ":" + consumerSecret);
			oauthService = new ServiceBuilder().provider(new TwitterApi()).apiKey(consumerKey).apiSecret(consumerSecret).callback("/oauth/callback").build();
		}
		return oauthService;
	}
	
}