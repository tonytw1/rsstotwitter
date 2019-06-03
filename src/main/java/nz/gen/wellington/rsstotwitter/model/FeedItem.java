package nz.gen.wellington.rsstotwitter.model;

import java.util.Date;
import java.util.Optional;

public class FeedItem {

    private Feed feed;
    private String title;
    private String uri;
    private String link;
    private Date publishedDate;
    private String author;
    private Optional<LatLong> latLong;

    public FeedItem(Feed feed, String title, String uri, String link, Date publishedDate, String author, Optional<LatLong> latLong) {
        this.feed = feed;
        this.title = title;
        this.uri = uri;
        this.link = link;
        this.publishedDate = publishedDate;
        this.author = author;
        this.latLong = latLong;
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

    public Optional<LatLong> getLatLong() {
        return latLong;
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
                ", latLong=" + latLong +
                '}';
    }
}
