package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

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

    public Tweet(Long id, long userId, Date date, String text, String author) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.text = text;
        this.author = author;
    }

    public Tweet(Long id, long userId, Date date, String text, String author, String uri, String url) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.text = text;
        this.author = author;
        this.uri = uri;
        this.url = url;
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
