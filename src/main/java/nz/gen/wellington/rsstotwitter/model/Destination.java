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

    public String getAccountUrl(Account account) {
        if (this == MASTODON) {
            return account.getMastodonUrl();
        }
        if (this == TWITTER) {
            return "https://twitter.com/" + account.getUsername();
        }
        return null;
    }

    public String getAccountUsername(Account account) {
        if (this == MASTODON) {
            return account.getMastodonUsername();
        }
        if (this == TWITTER) {
            return account.getUsername();
        }
        return null;
    }

}
