package nz.gen.wellington.rsstotwitter.controllers.signin;

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
import twitter4j.OAuth2TokenProvider;
import twitter4j.UsersResponse;

@Component
public class TwitterSigninHandler implements SigninHandler<TwitterCredentials> {

    private final static Logger log = LogManager.getLogger(TwitterSigninHandler.class);

    private final AccountDAO accountDAO;
    private final TwitterService twitterService;

    private final String callbackUrl;

    @Autowired
    public TwitterSigninHandler(AccountDAO accountDAO,
                                TwitterService twitterService,
                                @Value("${homepage.url}") String homepageUrl) {
        this.accountDAO = accountDAO;
        this.twitterService = twitterService;

        this.callbackUrl = homepageUrl + "/oauth/callback";
    }

    @Override
    public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) {
        if (!twitterService.isConfigured()) {
            log.warn("Twitter is not configured; can not login");
            return null;
        }

        final String authorizeUrl = twitterService.getAuthorizeUrl(callbackUrl);
        log.info("Redirecting user to authorize url : " + authorizeUrl);
        return new ModelAndView(new RedirectView(authorizeUrl));
    }

    @Override
    public TwitterCredentials getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request) {
        if (request.getParameter("code") != null) {
            final String code = request.getParameter("code");

            log.info("Exchanging code " + code + " for access token");
            try {
                OAuth2TokenProvider.Result result = twitterService.getOAuth2Token(code, callbackUrl);
                String accessToken = result.getAccessToken();
                log.info("Got access token " + accessToken);

                log.info("Using access token to lookup twitter user details");
                UsersResponse twitterUserResponse = twitterService.getTwitterUserCredentials(accessToken);
                if (twitterUserResponse != null && !twitterUserResponse.getUsers().isEmpty()) {
                    return new TwitterCredentials(twitterUserResponse.getUsers().get(0), accessToken);

                } else {
                    log.warn("Failed up obtain twitter user details");
                }

            } catch (Exception e) {
                log.error("Failed to obtain access token and get authed user", e);
                return null;
            }

        } else {
            log.error("code missing from callback request");
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
        account.setToken(externalIdentifier.getToken());
        account.setTokenSecret(null);
    }

}
