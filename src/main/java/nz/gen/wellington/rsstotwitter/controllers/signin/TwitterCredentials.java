package nz.gen.wellington.rsstotwitter.controllers.signin;

import twitter4j.User2;

public class TwitterCredentials {

    private final User2 user;
    private final String token;
    private final String refreshToken;

    public TwitterCredentials(User2 user, String token, String refreshToken) {
        this.user = user;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public User2 getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
