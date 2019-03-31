package nz.gen.wellington.rsstotwitter.model;

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
    public String toString() {
        return "Feed{" +
                "url='" + url + '\'' +
                '}';
    }
}
