package nz.gen.wellington.twitter;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.apache.log4j.Logger;

import twitter4j.GeoLocation;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;


public class TwitterService {
    
    public static final int MAXIMUM_TWITTER_MESSAGE_LENGTH = 140;
	private static final int REPLY_PAGES_TO_FETCH = 1;
    
	
    static Logger log = Logger.getLogger(TwitterService.class);

	
    public Status twitter(String twit, GeoLocation geoLocation, TwitterAccount account) {
		log.info("Attempting to tweet: " + twit);
		if (!(twit.length() <= MAXIMUM_TWITTER_MESSAGE_LENGTH)) {
			log.warn("Message too long to twitter; not twittered: " + twit);
			return null;
		}
		
		log.debug("Checking for valid characters.");
		char[] charArray = twit.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char letter = twit.charAt(i);
			log.debug(letter + "(" + Character.codePointAt(charArray, i) +"): " + Character.isValidCodePoint(letter));
			if (!Character.isValidCodePoint(letter)) {
				log.warn("Message has invalid code point: " + letter);
				return null;
			}
			if (65533 == Character.codePointAt(charArray, i)) {
				log.warn("Message has problem code point 65533: " + letter);
				return null;
			}						
		}
			        	        	
		Twitter twitter = getAuthenticatedApiForAccount(account);		
		if (twitter == null) {
    		return null;
    	}
				
		log.info("Twittering: " + twit);
		try {
			if (geoLocation != null) {
				return twitter.updateStatus(twit, geoLocation);
			}
			return twitter.updateStatus(twit);
		} catch (TwitterException e) {
        	 log.warn("A TwitterException occured while trying to tweet: " + e.getMessage());
		}
        	
		return null;
    }
    
    public List<Status> getReplies(TwitterAccount account) {
    	Twitter twitter = getAuthenticatedApiForAccount(account);
    	if (twitter == null) {
    		return null;
    	}
    	
		log.info("Getting twitter replies from live api");
        List<Status> all = new ArrayList<Status>();
        
        // TODO how to paginate this correctly?
		for (int i = 1; i <= REPLY_PAGES_TO_FETCH; i++) {
			ResponseList<Status> mentions;
			try {
				mentions = twitter.getMentions();
				for (Status status : mentions) {
					all.add(status);
				}
			} catch (TwitterException e) {
       		 	log.warn("A TwitterException occured while trying to fetch mentions: " + e.getMessage());
			}
		}
		return all;
	}
    
    
	private Twitter getAuthenticatedApiForAccount(TwitterAccount account) {
		boolean accountHasAccessToken = account.getToken() != null && account.getTokenSecret() != null;
		if (!accountHasAccessToken) {
			log.warn("Could connect to account '" + account.getUsername()
					+ "' as there is no access token available");
			return null;
		}

		return new TwitterFactory().getOAuthAuthorizedInstance(new AccessToken(
				account.getToken(), account.getTokenSecret()));
	}
    
}
