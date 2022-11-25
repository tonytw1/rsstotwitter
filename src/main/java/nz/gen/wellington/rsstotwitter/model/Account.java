package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.Objects;

public class Account {

    @Id
    ObjectId objectId;

    // This user's Twitter id
    private Long id;
    // This user's Twitter screen name
    private String username;
    private String token;
    private String tokenSecret;

    private Long mastodonId;
    private String mastodonUsername;
    private String mastodonAccessToken;

    public Account() {
    }

    public Account(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
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

    public Long getMastodonId() {
        return mastodonId;
    }

    public String getMastodonAccessToken() {
        return mastodonAccessToken;
    }

    public void setMastodonId(Long mastodonId) {
        this.mastodonId = mastodonId;
    }

    public String getMastodonUsername() {
        return mastodonUsername;
    }

    public void setMastodonUsername(String mastodonUsername) {
        this.mastodonUsername = mastodonUsername;
    }

    public void setMastodonAccessToken(String mastodonAccessToken) {
        this.mastodonAccessToken = mastodonAccessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(objectId, account.objectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId);
    }

}
