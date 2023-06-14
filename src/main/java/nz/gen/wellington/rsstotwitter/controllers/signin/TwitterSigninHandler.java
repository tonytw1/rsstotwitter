package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.repositories.mongo.AccountDAO;
import nz.gen.wellington.rsstotwitter.twitter.TwitterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import twitter4j.AccessToken;
import twitter4j.RequestToken;

import java.util.Map;

@Component
public class TwitterSigninHandler implements SigninHandler<TwitterCredentials> {

    private final static Logger log = LogManager.getLogger(TwitterSigninHandler.class);

    private final AccountDAO accountDAO;
    private final TwitterService twitterService;

    private final String callbackUrl;

    private final Map<String, RequestToken> requestTokens;

    @Autowired
    public TwitterSigninHandler(AccountDAO accountDAO,
                                TwitterService twitterService,
                                @Value("${homepage.url}") String homepageUrl) {
        this.accountDAO = accountDAO;
        this.twitterService = twitterService;

        this.requestTokens = Maps.newConcurrentMap();
        this.callbackUrl = homepageUrl + "/oauth/callback";
    }

    @Override
    public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) {
        if (!twitterService.isConfigured()) {
            log.warn("Twitter is not configured; can not login");
            return null;
        }

        try {
            log.info("Getting request token for callback url: " + callbackUrl);
            RequestToken requestToken = twitterService.oauthAuthentication().getOAuthRequestToken(callbackUrl);

            if (requestToken != null) {
                log.info("Got request token: " + requestToken.getToken());
                requestTokens.put(requestToken.getToken(), requestToken);

                final String authorizeUrl = requestToken.getAuthorizationURL();
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
            RequestToken requestToken = requestTokens.get(token);
            if (requestToken != null) {
                log.info("Found stored request token: " + requestToken.getToken());

                log.debug("Exchanging request token for access token");
                try {
                    AccessToken accessToken = twitterService.oauthAuthentication().getOAuthAccessToken(requestToken, verifier);
                    if (accessToken != null) {
                        log.info("Got access token: '" + accessToken.getToken() + "', '" + accessToken.getTokenSecret() + "'");
                        requestTokens.remove(requestToken.getToken());

                        log.debug("Using access token to lookup twitter user details");
                        twitter4j.v1.User twitterUser = twitterService.getTwitterUserCredentials(accessToken.getToken(), accessToken.getTokenSecret());
                        if (twitterUser != null) {
                            return new TwitterCredentials(twitterUser, accessToken);

                        } else {
                            log.warn("Failed up obtain twitter user details");
                        }

                    } else {
                        log.warn("Could not get access token for: " + requestToken.getToken());
                    }

                } catch (Exception e) {
                    log.error(e);
                    return null;
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
        account.setTokenSecret(externalIdentifier.getToken().getTokenSecret());
    }

}
