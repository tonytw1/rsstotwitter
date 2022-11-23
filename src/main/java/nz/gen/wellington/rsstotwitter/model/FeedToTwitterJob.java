package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

public class FeedToTwitterJob {

    @Id
    ObjectId objectId;

    private Feed feed;
    private Destination destination;

    @Reference
    private Account account;

    public FeedToTwitterJob() {
    }

    public FeedToTwitterJob(Feed feed, Account account, Destination destination) {
        this.feed = feed;
        this.account = account;
        this.destination = destination;
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

    public Destination getDestination() {
        return destination;
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
