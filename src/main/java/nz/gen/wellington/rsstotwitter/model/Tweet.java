package nz.gen.wellington.rsstotwitter.model;

import java.util.Date;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import twitter4j.GeoLocation;
import twitter4j.Status;

public class Tweet {

    @Id
    ObjectId objectId;

    private Long id;
    private long userId;
    private Date date;
    private String text;
    private String author;
    private long inReplyToUserId;
    private GeoLocation geoLocation;

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

        this.inReplyToUserId = status.getInReplyToUserId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getInReplyToUserId() {
        return inReplyToUserId;
    }

    public void setInReplyToUserId(long inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", text='" + text + '\'' +
                ", author='" + author + '\'' +
                ", inReplyToUserId=" + inReplyToUserId +
                ", geoLocation=" + geoLocation +
                '}';
    }
}
