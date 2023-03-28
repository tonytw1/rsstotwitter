package nz.gen.wellington.rsstotwitter.controllers.signin;

import com.github.scribejava.core.model.OAuth1AccessToken;
import twitter4j.v1.User;

public class TwitterCredentials {

    private final User user;
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
