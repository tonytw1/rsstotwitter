package nz.gen.wellington.rsstotwitter.forms;

import org.hibernate.validator.constraints.NotBlank;

public class FeedDetails {

    @NotBlank
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FeedDetails{" +
                "url='" + url + '\'' +
                '}';
    }
}
