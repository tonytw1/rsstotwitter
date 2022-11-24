package nz.gen.wellington.rsstotwitter.controllers.signin;

import org.scribe.model.Token;
import twitter4j.User;

public class TwitterCredentials {

    private final twitter4j.User user;
    private final Token token;

    public TwitterCredentials(User user, Token token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public Token getToken() {
        return token;
    }
}
