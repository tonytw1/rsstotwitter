package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

public class Job {

    @Id
    ObjectId objectId;

    private Feed feed;
    private TwitterAccount account;

    public Job() {
    }

    public Job(Feed feed, TwitterAccount account) {
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

    public TwitterAccount getAccount() {
        return account;
    }

    public void setAccount(TwitterAccount account) {
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
