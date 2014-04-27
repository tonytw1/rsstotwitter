package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

public interface Updater {

   	public void updateFeed(List<FeedItem> feedItems, TwitterAccount account);

}
