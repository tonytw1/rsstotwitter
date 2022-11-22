package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

public class FeedToTwitterJob {

    @Id
    ObjectId objectId;

    private Feed feed;

    @Reference
    private Account account;

    public FeedToTwitterJob() {
    }

    public FeedToTwitterJob(Feed feed, Account account) {
        this.feed = feed;
        this.account = account;
    }

    public String getObjectId() {
        return objectId.toHexString();
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
