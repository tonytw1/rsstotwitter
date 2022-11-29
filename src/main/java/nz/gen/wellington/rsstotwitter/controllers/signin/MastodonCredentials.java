package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.sys1yagi.mastodon4j.api.entity.Account;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;

public class MastodonCredentials {

    private final Account account;
    private final AccessToken accessToken;

    public MastodonCredentials(Account account, AccessToken accessToken) {
        this.account = account;
        this.accessToken = accessToken;
    }

    public Account getAccount() {
        return account;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

}
