package nz.gen.wellington.rsstotwitter.controllers.signin;

import twitter4j.User2;

public class TwitterCredentials {

    private final User2 user;
    private final String token;

    public TwitterCredentials(User2 user, String token) {
        this.user = user;
        this.token = token;
    }

    public User2 getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
