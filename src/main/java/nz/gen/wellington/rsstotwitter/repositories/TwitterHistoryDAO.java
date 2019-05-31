package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;

public interface TwitterHistoryDAO {
    @SuppressWarnings("unchecked")
    boolean hasAlreadyBeenTwittered(String guid);

    void markAsTwittered(FeedItem feedItem, Tweet sentTweet);

    long getNumberOfTwitsInLastHour(Feed feed);
    long getNumberOfTwitsInLastTwentyFourHours(Feed feed);

    int getNumberOfTwitsInLastTwentyFourHours(Feed feed, String publisher);
}
