package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.*;

public interface Updater {

    public void updateFeed(Account account, Feed feed, List<FeedItem> feedItems, Destination destination);

}
