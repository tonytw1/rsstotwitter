package nz.gen.wellington.rsstotwitter.model;

public class FeedToTwitterJob {

	private Feed feed;
	private TwitterAccount account;
	private String tag;
	
	public FeedToTwitterJob(Feed feed, TwitterAccount account, String tag) {
		this.feed = feed;
		this.account = account;
		this.tag = tag;
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
}