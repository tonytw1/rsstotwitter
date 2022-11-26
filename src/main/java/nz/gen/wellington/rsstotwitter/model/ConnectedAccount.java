package nz.gen.wellington.rsstotwitter.model;

public class ConnectedAccount {

    private final String username;
    private final Destination destination;
    private final String url;

    public ConnectedAccount(String username, Destination destination, String url) {
        this.username = username;
        this.destination = destination;
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public String getLogo() {
        if (destination == Destination.MASTODON) {
            return "/logos/mastodon.svg";
        } else if (destination == Destination.TWITTER) {
            return "/logos/twitter.png";
        }
        return null;
    }

    public Destination getDestination() {
        return destination;
    }

    public String getUrl() {
        return url;
    }

}
