package nz.gen.wellington.rsstotwitter.mastodon;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

@Component
public class MastodonService {

    public Tweet post(String instance, String accessToken, String message) throws Mastodon4jRequestException {
        MastodonClient client = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).
                accessToken(accessToken).
                build();
        Statuses statuses = new Statuses(client);

        MastodonRequest<Status> request = statuses.postStatus(message, null, null, false, null, Status.Visibility.Public);
        Status status = request.execute();
        return new Tweet(status);
    }

}
