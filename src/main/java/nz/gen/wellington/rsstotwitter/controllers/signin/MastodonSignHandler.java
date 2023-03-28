package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.Scope;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.gen.wellington.rsstotwitter.mastodon.MastodonService;
import nz.gen.wellington.rsstotwitter.model.Account;
import nz.gen.wellington.rsstotwitter.repositories.mongo.AccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Component
public class MastodonSignHandler implements SigninHandler<MastodonCredentials> {

    private final AccountDAO accountDAO;
    private final MastodonService mastodonService;

    private final String redirectUri;

    private final static Logger log = LogManager.getLogger(MastodonSignHandler.class);

    @Autowired
    public MastodonSignHandler(AccountDAO accountDAO,
                               MastodonService mastodonService,
                               @Value("${homepage.url}") String homepageUrl
    ) {
        this.accountDAO = accountDAO;
        this.mastodonService = mastodonService;
        this.redirectUri = homepageUrl + "/mastodon/oauth/callback";
    }

    @Override
    public ModelAndView getLoginView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Mastodon is an OAuth2 platform so this will be a redirect to the OAuth flow
        Scope.Name scopeName = Scope.Name.ALL;    // TODO we only really need "write:statuses" and verify account
        String callbackUrl = redirectUri;
        String authorizeUrl = mastodonService.getOAuthUrl(scopeName, callbackUrl);

        return new ModelAndView(new RedirectView(authorizeUrl));
    }

    @Override
    public MastodonCredentials getExternalUserIdentifierFromCallbackRequest(HttpServletRequest request) {
        if (request.getParameter("code") != null) {
            String code = request.getParameter("code");
            log.info("Got callback code: " + code);

            // Exchange for access token
            MastodonRequest<AccessToken> accessTokenRequest = mastodonService.getAccessToken(redirectUri, code, "authorization_code");
            try {
                AccessToken accessToken = accessTokenRequest.execute();
                log.info("Got access token: " + accessToken.getAccessToken() + " with scope " + accessToken.getScope());

                // Look up the owner of this access token
                com.sys1yagi.mastodon4j.api.entity.Account mastodonAccount = mastodonService.verifyCredentials(accessToken);
                log.info("Got Mastodon account: " + mastodonAccount.getUserName() + " / " + mastodonAccount.getUrl());

                return new MastodonCredentials(mastodonAccount, accessToken);

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
        account.setMastodonUsername(externalIdentifier.getAccount().getUserName());
        account.setMastodonUrl(externalIdentifier.getAccount().getUrl());
    }

}
