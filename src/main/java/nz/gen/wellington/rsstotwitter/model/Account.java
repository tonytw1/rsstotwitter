package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.Objects;

@Entity
public class Account {

    @Id
    ObjectId objectId;

    // This user's Twitter id
    private Long id;
    // This user's Twitter screen name
    private String username;
    private String twitterAccessToken;
    private String twitterRefreshToken;

    private Long mastodonId;
    private String mastodonUsername;
    private String mastodonUrl;
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

    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public void setTwitterAccessToken(String twitterAccessToken) {    // TODO rename to accessToken
        this.twitterAccessToken = twitterAccessToken;
    }

    public String getTwitterRefreshToken() {    // TODO rename to accessTokenSecret
        return twitterRefreshToken;
    }

    public void setTwitterRefreshToken(String twitterRefreshToken) {
        this.twitterRefreshToken = twitterRefreshToken;
    }

    public Long getMastodonId() {
        return mastodonId;
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

    public String getMastodonUrl() {
        return mastodonUrl;
    }

    public void setMastodonUrl(String mastodonUrl) {
        this.mastodonUrl = mastodonUrl;
    }

    public String getMastodonAccessToken() {
        return mastodonAccessToken;
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
