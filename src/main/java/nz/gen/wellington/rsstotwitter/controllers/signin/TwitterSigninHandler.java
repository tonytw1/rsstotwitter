package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.google.common.collect.Maps;
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
import java.util.Map;

@Component
public class TwitterSigninHandler implements SigninHandler<TwitterCredentials> {

    private final static Logger log = LogManager.getLogger(TwitterSigninHandler.class);

    private final TwitterAccountDAO accountDAO;
    private final TwitterService twitterService;
    private final OAuthService oauthService;

    private final String consumerKey;
    private final String consumerSecret;

    private final Map<String, Token> requestTokens;

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

        this.requestTokens = Maps.newConcurrentMap();

        this.oauthService = makeOauthService(homepageUrl + "/oauth/callback");
    }

    @Override
    public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("Getting request token");
            Token requestToken = oauthService.getRequestToken();    // TODO can we use the code flow like the Mastadon handler?
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
    public TwitterCredentials getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request) {
        if (request.getParameter("oauth_token") != null && request.getParameter("oauth_verifier") != null) {
            final String token = request.getParameter("oauth_token");
            final String verifier = request.getParameter("oauth_verifier");

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
                        return new TwitterCredentials(twitterUser, accessToken);

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
    public Account getUserByExternalIdentifier(TwitterCredentials externalIdentifier) {
        return accountDAO.getUserByTwitterId(externalIdentifier.getUser().getId());
    }

    @Override
    public void decorateUserWithExternalSigninIdentifier(Account account, TwitterCredentials externalIdentifier) {
        account.setId(externalIdentifier.getUser().getId());
        account.setUsername(externalIdentifier.getUser().getScreenName());
        account.setToken(externalIdentifier.getToken().getToken());
        account.setTokenSecret(externalIdentifier.getToken().getSecret());
    }

    private OAuthService makeOauthService(String callBackUrl) {
        log.info("Building oauth service with consumer key and consumer secret: " + consumerKey + ":" + consumerSecret);
        log.info("Oauth callback url is: " + callBackUrl);
        return new ServiceBuilder().provider(new TwitterApi()).apiKey(consumerKey).apiSecret(consumerSecret).callback(callBackUrl).build();
    }

}
