package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.github.scribejava.core.model.OAuth1AccessToken;
import twitter4j.User;

public class TwitterCredentials {

    private final twitter4j.User user;
    private final OAuth1AccessToken token;

    public TwitterCredentials(User user, OAuth1AccessToken token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public OAuth1AccessToken getToken() {
        return token;
    }
}
