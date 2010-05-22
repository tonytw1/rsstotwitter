package nz.gen.wellington.rsstotwitter.model;

import java.util.Set;

public class TwitterAccount {

	private int id;
	private String username;
	private String password;
	
	private String token;
	private String tokenSecret;
	
	private Set<Tweet> mentions;

	public TwitterAccount() {
	}

	public TwitterAccount(int id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
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
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
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

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	
}
