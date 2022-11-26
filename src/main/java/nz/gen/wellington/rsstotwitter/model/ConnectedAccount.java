package nz.gen.wellington.rsstotwitter.model;

public class ConnectedAccount {

    private final String username;
    private final Destination destination;

    public ConnectedAccount(String username, Destination destination) {
        this.username = username;
        this.destination = destination;
    }

    public String getUsername() {
        return username;
    }

    public Destination getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "ConnectedAccount{" +
                "username='" + username + '\'' +
                ", destination=" + destination +
                '}';
    }
}
