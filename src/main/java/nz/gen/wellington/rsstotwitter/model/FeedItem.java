package nz.gen.wellington.rsstotwitter.model;

import java.util.Date;

public class FeedItem {

    private Feed feed;
    private String title;
    private String uri;
    private String link;
    private Date publishedDate;
    private String author;
    private Double latitude;
    private Double longitude;

    public FeedItem(Feed feed, String title, String uri, String link, Date publishedDate, String author, Double latitude, Double longitude) {
        this.feed = feed;
        this.title = title;
        this.uri = uri;
        this.link = link;
        this.publishedDate = publishedDate;
        this.author = author;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Feed getFeed() {
        return feed;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public String getLink() {
        return link;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public String getAuthor() {
        return author;
    }

    public String getGuid() {
        return uri;
    }

    public boolean isGeocoded() {
        return latitude != null && longitude != null;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "feed=" + feed +
                ", title='" + title + '\'' +
                ", uri='" + uri + '\'' +
                ", link='" + link + '\'' +
                ", publishedDate=" + publishedDate +
                ", author='" + author + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
