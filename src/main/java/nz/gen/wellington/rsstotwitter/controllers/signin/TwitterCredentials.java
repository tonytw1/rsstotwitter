package nz.gen.wellington.rsstotwitter.controllers.signin;

import twitter4j.AccessToken;
import twitter4j.v1.User;

public class TwitterCredentials {

    private final User user;
    private final AccessToken token;

    public TwitterCredentials(User user, AccessToken token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public AccessToken getToken() {
        return token;
    }
}
