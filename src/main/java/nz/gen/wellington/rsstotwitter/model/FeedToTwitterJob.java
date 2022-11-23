package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.Set;

public class FeedToTwitterJob {

    @Id
    ObjectId objectId;

    private Feed feed;
    private Set<Destination> destinations;

    @Reference
    private Account account;

    public FeedToTwitterJob() {
    }

    public FeedToTwitterJob(Feed feed, Account account, Set<Destination> destinations) {
        this.feed = feed;
        this.account = account;
        this.destinations = destinations;
    }

    public String getObjectId() {
        return objectId.toHexString();
    }

    public Feed getFeed() {
        return feed;
    }

    public Account getAccount() {
        return account;
    }

    public Set<Destination> getDestination() {
        return destinations;
    }

    @Override
    public String toString() {
        return "FeedToTwitterJob{" +
                "objectId=" + objectId +
                ", feed=" + feed +
                ", account=" + account +
                '}';
    }
}
