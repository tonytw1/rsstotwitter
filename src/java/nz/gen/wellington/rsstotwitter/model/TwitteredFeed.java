package nz.gen.wellington.rsstotwitter.model;

public class TwitteredFeed {
    
    private int id;
    private String url;
    private TwitterAccount account;
    private String twitterTag;

 
    public TwitteredFeed() {       
    }

    public  TwitteredFeed(String url, String twitterUsername, String twitterPassword, String twitterTag, TwitterAccount account) {
        this.id = 0;
        this.url = url;
        this.account = account;       
        this.twitterTag = twitterTag;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public TwitterAccount getAccount() {
		return account;
	}

	public void setAccount(TwitterAccount account) {
		this.account = account;
	}

	public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTwitterTag() {
        return twitterTag;
    }

    public void setTwitterTag(String twitterTag) {
        this.twitterTag = twitterTag;
    }
        
}
