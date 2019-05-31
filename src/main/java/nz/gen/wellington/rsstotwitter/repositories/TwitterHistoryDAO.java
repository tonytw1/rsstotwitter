package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.Feed;
import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;

public interface TwitterHistoryDAO {
    @SuppressWarnings("unchecked")
    boolean hasAlreadyBeenTwittered(String guid);

    void markAsTwittered(FeedItem feedItem, Tweet sentTweet);

    int getNumberOfTwitsInLastHour(Feed feed);
    int getNumberOfTwitsInLastTwentyFourHours(Feed feed);

    int getNumberOfTwitsInLastTwentyFourHours(Feed feed, String publisher);
}
