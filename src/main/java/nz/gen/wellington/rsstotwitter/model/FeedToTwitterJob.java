package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.Objects;
import java.util.Set;

@Entity
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

    public Set<Destination> getDestinations() {
        return destinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedToTwitterJob that = (FeedToTwitterJob) o;
        return Objects.equals(objectId, that.objectId) && Objects.equals(feed, that.feed) && Objects.equals(destinations, that.destinations) && Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, feed, destinations, account);
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
