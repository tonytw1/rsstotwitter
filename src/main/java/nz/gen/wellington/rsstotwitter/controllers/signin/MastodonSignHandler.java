package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.Scope;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Accounts;
import com.sys1yagi.mastodon4j.api.method.Apps;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.repositories.mongo.TwitterAccountDAO;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MastodonSignHandler implements SigninHandler<MastodonCredentials> {

    private final TwitterAccountDAO accountDAO;
    private final String instance;
    private final String clientId;
    private final String clientSecret;

    private final Apps apps;
    private final String redirectUri;

    private final static Logger log = LogManager.getLogger(MastodonSignHandler.class);

    @Autowired
    public MastodonSignHandler(TwitterAccountDAO accountDAO,
                               @Value("${mastodon.instance}") String instance,
                               @Value("${mastodon.client.id}") String clientId,
                               @Value("${mastodon.client.secret}") String clientSecret,
                               @Value("${homepage.url}") String homepageUrl
    ) {
        this.accountDAO = accountDAO;
        this.instance = instance;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        MastodonClient client = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).build();
        this.apps = new Apps(client);
        this.redirectUri = homepageUrl + "/mastodon/oauth/callback";
    }

    @Override
    public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Mastodon is an OAuth2 platform so this will be a redirect to the OAuth flow
        Scope.Name scopeName = Scope.Name.READ;
        String callbackUrl = redirectUri;
        String authorizeUrl = apps.getOAuthUrl(clientId, new Scope(scopeName), callbackUrl);

        return new ModelAndView(new RedirectView(authorizeUrl));
    }

    @Override
    public MastodonCredentials getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request) {
        if (request.getParameter("code") != null) {
            String code = request.getParameter("code");
            log.info("Got callback code: " + code);

            // Exchange for access token
            MastodonRequest<AccessToken> accessTokenRequest = apps.getAccessToken(clientId, clientSecret, redirectUri, code, "authorization_code");
            try {
                AccessToken accessToken = accessTokenRequest.execute();
                log.info("Got access token: " + accessToken.getAccessToken() + " with scope " + accessToken.getScope());

                // Look up the owner of this access token
                MastodonClient usersClient = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).accessToken(accessToken.getAccessToken()).build();

                Accounts accounts = new Accounts(usersClient);
                MastodonRequest<com.sys1yagi.mastodon4j.api.entity.Account> verifyCredentialsRequest = accounts.getVerifyCredentials();

                com.sys1yagi.mastodon4j.api.entity.Account account = verifyCredentialsRequest.execute();
                log.info("Got Mastodon account: " + account.getUserName() + " / " + account.getUrl());
                return new MastodonCredentials(account, accessToken);

            } catch (Mastodon4jRequestException e) {
                log.error(e);
                return null;
            }

        }
        return null;
    }

    @Override
    public Account getUserByExternalIdentifier(MastodonCredentials externalIdentifier) {
        return accountDAO.getUserByMastodonId(externalIdentifier.getAccount().getId());
    }

    @Override
    public void decorateUserWithExternalSigninIdentifier(Account account, MastodonCredentials externalIdentifier) {
        account.setMastodonId(externalIdentifier.getAccount().getId());
        account.setMastodonAccessToken(externalIdentifier.getAccessToken().getAccessToken());
    }

}
