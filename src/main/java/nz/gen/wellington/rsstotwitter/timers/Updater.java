package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;

public interface Updater {

   	public void updateFeed(TwitteredFeed feed, List<FeedItem> feedItems);

}
