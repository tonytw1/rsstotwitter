package nz.gen.wellington.rsstotwitter.twitter;

import org.junit.Test;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

public class ExchangeBasicForXauthTokenTest {

	// TODO Annotate to run
	public void testgetXauthAccessToken() throws Exception {
		AccessToken accessToken = exchangeBasicForToken("USERNAME", "PASSWORD");
		final String token = accessToken.getToken();
		final String tokenSecret = accessToken.getTokenSecret();
		System.out.println(token + ":" + tokenSecret);
	}
		
	private AccessToken exchangeBasicForToken(String username, String password) {
		Twitter twitter = new TwitterFactory().getInstance(username, password);
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken();
			return accessToken;
		} catch (TwitterException e) {
			System.out.println(e);
		}
		return null;
	}  
}
