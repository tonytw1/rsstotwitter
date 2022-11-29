package nz.gen.wellington.rsstotwitter.model;

public enum Destination {
    MASTODON("Mastodon", "/logos/mastodon.svg", "oauth/login"),
    TWITTER("Twitter", "/logos/twitter.png", "mastodon/oauth/login");

    private final String displayName;
    private final String logo;
    private final String signin;

    Destination(String displayName, String logo, String signin) {
        this.displayName = displayName;
        this.logo = logo;
        this.signin = signin;
    }

    public String getLogo() {
       return logo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSignin() {
        return signin;
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
