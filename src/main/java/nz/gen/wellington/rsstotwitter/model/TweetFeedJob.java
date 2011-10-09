package nz.gen.wellington.rsstotwitter.model;

public class TweetFeedJob {

	private Feed feed;
	private TwitterAccount account;
	private String tag;
	
	public TweetFeedJob(Feed feed, TwitterAccount account, String tag) {
		this.feed = feed;
		this.account = account;
		this.tag = tag;
	}

	public Feed getFeed() {
		return feed;
	}

	public TwitterAccount getAccount() {
		return account;
	}

	public String getTag() {
		return tag;
	}
	
}
