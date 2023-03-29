package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Entity;

import java.util.Objects;

@Entity
public class Feed {

    private String url;

    public Feed() {
    }

    public Feed(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feed feed = (Feed) o;
        return Objects.equals(url, feed.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Feed{" +
                "url='" + url + '\'' +
                '}';
    }

}
