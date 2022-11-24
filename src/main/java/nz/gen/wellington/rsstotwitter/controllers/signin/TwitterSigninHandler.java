package nz.gen.wellington.rsstotwitter.controllers.signin;

import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterAccountDAO;
import nz.gen.wellington.rsstotwitter.twitter.TwitterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import twitter4j.auth.AccessToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class TwitterSigninHandler implements SigninHandler {

    private final static Logger log = LogManager.getLogger(TwitterSigninHandler.class);

    private final TwitterAccountDAO accountDAO;
    private final TwitterService twitterService;
    private final OAuthService oauthService;

    private final String consumerKey;
    private final String consumerSecret;

    private final Map<String, Token> requestTokens;
    private final Map<Long, Token> accessTokens;    // TODO this is weird

    @Autowired
    public TwitterSigninHandler(TwitterAccountDAO accountDAO,
                                TwitterService twitterService,
                                @Value("${consumer.key}") String consumerKey,
                                @Value("${consumer.secret}") String consumerSecret,
                                @Value("${homepage.url}") String homepageUrl) {
        this.accountDAO = accountDAO;
        this.twitterService = twitterService;

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;

        this.requestTokens = new HashMap<>();
        this.accessTokens = new HashMap<>();

        this.oauthService = makeOauthService(homepageUrl + "/oauth/callback");
    }

    @Override
    public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("Getting request token");
            Token requestToken = oauthService.getRequestToken();
            if (requestToken != null) {
                log.info("Got request token: " + requestToken.getToken());
                requestTokens.put(requestToken.getToken(), requestToken);

                final String authorizeUrl = oauthService.getAuthorizationUrl(requestToken);
                log.info("Redirecting user to authorize url : " + authorizeUrl);
                return new ModelAndView(new RedirectView(authorizeUrl));
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

                Token accessToken = oauthService.getAccessToken(requestToken, new Verifier(verifier));

                if (accessToken != null) {
                    log.info("Got access token: '" + accessToken.getToken() + "', '" + accessToken.getSecret() + "'");
                    requestTokens.remove(requestToken.getToken());

                    log.debug("Using access token to lookup twitter user details");
                    twitter4j.User twitterUser = twitterService.getTwitterUserCredentials(new AccessToken(accessToken.getToken(), accessToken.getSecret()));
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
    public Account getUserByExternalIdentifier(Object externalIdentifier) {
        twitter4j.User twitterUser = (twitter4j.User) externalIdentifier;
        return accountDAO.getUserByTwitterId(twitterUser.getId());
    }

    @Override
    public void decorateUserWithExternalSigninIdentifier(Account account, Object externalIdentifier) {
        twitter4j.User twitterUser = (twitter4j.User) externalIdentifier;
        account.setId(twitterUser.getId());
        account.setUsername(twitterUser.getScreenName());
        account.setToken(accessTokens.get(twitterUser.getId()).getToken());
        account.setTokenSecret(accessTokens.get(twitterUser.getId()).getSecret());
    }

    private OAuthService makeOauthService(String callBackUrl) {
        log.info("Building oauth service with consumer key and consumer secret: " + consumerKey + ":" + consumerSecret);
        log.info("Oauth callback url is: " + callBackUrl);
        return new ServiceBuilder().provider(new TwitterApi()).apiKey(consumerKey).apiSecret(consumerSecret).callback(callBackUrl).build();
    }

}
