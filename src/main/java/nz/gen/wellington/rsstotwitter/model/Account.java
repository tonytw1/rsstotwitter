package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

public class Account {

    @Id
    ObjectId objectId;

    // This user's Twitter id
    private long id;
    // This user's Twitter screen name
    private String username;

    private String token;
    private String tokenSecret;

    public Account() {
    }

    public Account(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {    // TODO rename to accessToken
        this.token = token;
    }

    public String getTokenSecret() {    // TODO rename to accessTokenSecret
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

}
