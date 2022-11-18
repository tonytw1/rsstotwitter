package nz.gen.wellington.rsstotwitter.mastodon;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import com.sys1yagi.mastodon4j.api.method.Timelines;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MastodonService {

    private MastodonClient client;

    public MastodonService(@Value("${mastodon.instance}") String instance, @Value("${mastodon.access.token}") String accessKey) {
        this.client = new MastodonClient.Builder(instance, new OkHttpClient.Builder(), new Gson()).
                accessToken(accessKey).
                build();
    }

    public void post(String message) throws Mastodon4jRequestException {
        Timelines timelines = new Timelines(client);

        Statuses statuses = new Statuses(client);
        MastodonRequest<Status> request = statuses.postStatus(message, null, null, false, null, Status.Visibility.Public);

        request.execute();
    }

}
