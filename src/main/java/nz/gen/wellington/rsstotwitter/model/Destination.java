package nz.gen.wellington.rsstotwitter.model;

public enum Destination {
    TWITTER, MASTODON;

    public String getLogo() {
        if (this == MASTODON) {
            return "/logos/mastodon.svg";
        } else if (this == TWITTER) {
            return "/logos/twitter.png";
        }
        return null;
    }

}
