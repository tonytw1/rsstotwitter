package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import twitter4j.v1.Status;

import java.util.Date;

@Entity
public class Tweet {

    @Id
    ObjectId objectId;

    private Long id;
    private long userId;
    private Date date;
    private String text;
    private String author;
    private String uri;
    private String url;

    public Tweet() {
    }

    public Tweet(String text) {
        this.text = text;
    }

    public Tweet(Status status) {
        this.id = status.getId();
        this.userId = status.getUser().getId();
        this.text = status.getText();
        this.author = status.getUser().getScreenName();

        DateTime time = new DateTime(status.getCreatedAt());
        this.date = time.toDate();
    }

    public Tweet(com.sys1yagi.mastodon4j.api.entity.Status status) {
        this.id = status.getId();
        this.uri = status.getUri();
        this.url = status.getUrl();
        this.userId = status.getAccount().getId();
        this.text = status.getContent();
        this.author = status.getAccount().getUserName();

        DateTime time = new DateTime(status.getCreatedAt());
        this.date = time.toDate();
    }

    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getPreviewUrl() {
        if (url != null) {
            return url;
        }
        return "https://twitter.com/" + author + "/status/" + id;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "objectId=" + objectId +
                ", id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

}
