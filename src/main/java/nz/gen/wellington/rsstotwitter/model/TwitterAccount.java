package nz.gen.wellington.rsstotwitter.model;

import java.util.Set;

public class TwitterAccount {

	private int id;
	private String username;
	private boolean autoFollow;
	
	private String token;
	private String tokenSecret;
	
	private Set<Tweet> mentions;

	public TwitterAccount() {
	}

	public TwitterAccount(int id, String username) {
		this.id = id;
		this.username = username;
	}
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Set<Tweet> getMentions() {
		return mentions;
	}

	public void setMentions(Set<Tweet> mentions) {
		this.mentions = mentions;
	}

	public void addMention(Tweet mention) {
		mentions.add(mention);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {	// TODO rename to accessToken
		this.token = token;
	}

	public String getTokenSecret() {	// TODO rename to accessTokenSecret
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public boolean isAutoFollow() {
		return autoFollow;
	}

	public void setAutoFollow(boolean autoFollow) {
		this.autoFollow = autoFollow;
	}
	
}
