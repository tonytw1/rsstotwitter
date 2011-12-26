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
	private String callBackUrl;
	
	private Map<String, Token> requestTokens;
	private Map<Integer, Token> accessTokens;
	
	public TwitterLoginHandler(AccountDAO accountDAO, TwitterService twitterService) {
		this.accountDAO = accountDAO;
		this.twitterService = twitterService;
		this.requestTokens = new HashMap<String, Token>();
		this.accessTokens = new HashMap<Integer, Token>();
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	@Override
	public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {			
			log.info("Getting request token");			
			OAuthService service = getOauthService(request);
			
			Token requestToken = service.getRequestToken();		
			if (requestToken != null) {
				log.info("Got request token: " + requestToken.getToken());
				requestTokens.put(requestToken.getToken(), requestToken);
				
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
			Token requestToken = requestTokens.get(token);
			if (requestToken != null) {
				log.info("Found stored request token: " + requestToken.getToken());
				
				log.debug("Exchanging request token for access token");
				
				OAuthService service = getOauthService(request);
				Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));
				
				if (accessToken != null) {
					log.info("Got access token: '" + accessToken.getToken() + "', '" + accessToken.getSecret() + "'");
					requestTokens.remove(requestToken.getToken());
					
					log.debug("Using access token to lookup twitter user details");
					twitter4j.User twitterUser = twitterService.getTwitteUserCredentials(new AccessToken(accessToken.getToken(), accessToken.getSecret()));
					if (twitterUser != null) {
						accessTokens.put(twitterUser.getId(), accessToken);
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
		account.setId(twitterUser.getId());
		account.setUsername(twitterUser.getScreenName());		
		account.setToken(accessTokens.get(twitterUser.getId()).getToken());
		account.setTokenSecret(accessTokens.get(twitterUser.getId()).getSecret());
		log.info("Tokens set to: " + account.getToken() + ", " + account.getTokenSecret());
	}
	
	private OAuthService getOauthService(HttpServletRequest request) {
		if (oauthService == null) {
			log.info("Building oauth service with consumer key and consumer secret: " + consumerKey + ":" + consumerSecret);
			log.info("Oauth callback url is: " + callBackUrl);
			oauthService = new ServiceBuilder().provider(new TwitterApi()).apiKey(consumerKey).apiSecret(consumerSecret).callback(callBackUrl).build();
		}
		return oauthService;
	}
	
}
