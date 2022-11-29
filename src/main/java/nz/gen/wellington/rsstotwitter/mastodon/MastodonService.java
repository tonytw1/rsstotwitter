package nz.gen.wellington.rsstotwitter.mastodon;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.Scope;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Accounts;
import com.sys1yagi.mastodon4j.api.method.Apps;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MastodonService {

    private final String instance;
    private final String clientId;
    private final String clientSecret;
    private final Apps apps;

    public MastodonService(@Value("${mastodon.instance}") String instance,
                           @Value("${mastodon.client.id}") String clientId,
                           @Value("${mastodon.client.secret}") String clientSecret) {
        this.instance = instance;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        MastodonClient client = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).build();
        this.apps = new Apps(client);
    }

    public Tweet post(String accessToken, String message) throws Mastodon4jRequestException {
        MastodonClient client = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).
                accessToken(accessToken).
                build();
        Statuses statuses = new Statuses(client);

        MastodonRequest<Status> request = statuses.postStatus(message, null, null, false, null, Status.Visibility.Public);
        Status status = request.execute();
        return new Tweet(status);
    }

    public String getOAuthUrl(Scope.Name scopeName, String callbackUrl) {
        return apps.getOAuthUrl(clientId, new Scope(scopeName), callbackUrl);
    }

    public MastodonRequest<AccessToken> getAccessToken(String redirectUri, String code, String grantType) {
        return apps.getAccessToken(clientId, clientSecret, redirectUri, code, grantType);
    }

    public com.sys1yagi.mastodon4j.api.entity.Account verifyCredentials(AccessToken accessToken) throws Mastodon4jRequestException {
        MastodonClient usersClient = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).accessToken(accessToken.getAccessToken()).build();

        Accounts accounts = new Accounts(usersClient);
        MastodonRequest<com.sys1yagi.mastodon4j.api.entity.Account> verifyCredentialsRequest = accounts.getVerifyCredentials();

        return verifyCredentialsRequest.execute();
    }

    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(instance) && !Strings.isNullOrEmpty(clientId) && !Strings.isNullOrEmpty(clientSecret);
    }

}
