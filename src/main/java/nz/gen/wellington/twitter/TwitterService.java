package nz.gen.wellington.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.http.AccessToken;

public class TwitterService {
    
	private static Logger log = Logger.getLogger(TwitterService.class);
	
    public static final int MAXIMUM_TWITTER_MESSAGE_LENGTH = 140;
	private static final int REPLY_PAGES_TO_FETCH = 1;
    
	private String consumerKey;
	private String consumerSecret;
	
    public TwitterService() {
	}
    
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	
	public Tweet twitter(Tweet tweet, TwitterAccount account) {
		String tweetText = tweet.getText();
		
		log.info("Attempting to tweet: " + tweetText);
		if (!(tweetText.length() <= MAXIMUM_TWITTER_MESSAGE_LENGTH)) {
			log.warn("Message too long to twitter; not twittered: " + tweetText);
			return null;
		}
		
		log.debug("Checking for valid characters.");
		char[] charArray = tweetText.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char letter = tweetText.charAt(i);
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
		
		AccessToken accessToken = new AccessToken(account.getToken(), account.getTokenSecret());
		Twitter twitter = getAuthenticatedApiForAccount(accessToken);
		if (twitter == null) {
			log.error("Failed to get authenticated twitter connection for account: " + account.getUsername());
    		return null;
    	}
				
		try {
			if (tweet.getGeoLocation() != null) {
				log.info("Twittering with geolocation: " + tweetText);
				Status updateStatus = twitter.updateStatus(tweetText, tweet.getGeoLocation());
				if (updateStatus != null) {
					return new Tweet(updateStatus);
				}
				return null;
								
			}			
			
			log.info("Twittering: " + tweet.getText());
			Status updateStatus = twitter.updateStatus(tweet.getText());
			if (updateStatus != null) {
				return new Tweet(updateStatus);
			}
			return null;
			
		} catch (TwitterException e) {
        	 log.warn("A TwitterException occured while trying to tweet: " + e.getMessage());
		}
        	
		return null;
    }
    
    public List<Status> getReplies(TwitterAccount account) {
		AccessToken accessToken = new AccessToken(account.getToken(), account.getTokenSecret());
    	Twitter twitter = getAuthenticatedApiForAccount(accessToken);
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
    
    public List<Integer> getFollowers(TwitterAccount account) {
    	Twitter twitter = new TwitterFactory().getInstance();
    	IDs followersIDs;
		try {
			followersIDs = twitter.getFollowersIDs(account.getUsername());
			log.info("Found " + followersIDs.getIDs().length + " follower ids");
			return Arrays.asList(ArrayUtils.toObject(followersIDs.getIDs()));
			
		} catch (TwitterException e) {
			log.error("Error while fetching follows of '" + account.getUsername() + "'", e);
		}
		return null;
    }
    
    public List<Integer> getFriends(TwitterAccount account) {
    	Twitter twitter = new TwitterFactory().getInstance();
    	IDs friendIds;
		try {
			friendIds = twitter.getFriendsIDs((account.getUsername()));
			log.info("Found " + friendIds.getIDs().length + " friend ids");
			return Arrays.asList(ArrayUtils.toObject(friendIds.getIDs()));
			
		} catch (TwitterException e) {
			log.error("Error while fetching friends of '" + account.getUsername() + "'" + e.getMessage());
		}
		return null;
    }
    
    public boolean follow(TwitterAccount account, int userId) throws TwitterException {
    	AccessToken accessToken = new AccessToken(account.getToken(), account.getTokenSecret());
    	Twitter twitter = getAuthenticatedApiForAccount(accessToken);
    	try {
    		User followed = twitter.createFriendship(userId, true);
    		if (followed != null) {
    			log.info("Followed: " + followed.getScreenName());
    			return true;
    		} else {
    			log.warn("Failed to follow");
    		}
    	} catch (Exception e) {
    		log.error("Error while attempting to follow: " + e.getMessage());
    	}
    	return false;
    }
    
    public twitter4j.User getTwitteUserCredentials(AccessToken accessToken) {
		Twitter twitterApi = getAuthenticatedApiForAccount(accessToken);
		try {
			return twitterApi.verifyCredentials();
		} catch (TwitterException e) {
			log.warn("Failed up obtain twitter user details due to Twitter exception: " + e.getMessage());
			return null;
		}
	}
    
	private Twitter getAuthenticatedApiForAccount(AccessToken accessToken) {
		return new TwitterFactory().getOAuthAuthorizedInstance(consumerKey, consumerSecret, accessToken);
	}

	public ResponseList<User> getUserDetails(List<Integer> toFollow, TwitterAccount account) throws TwitterException {
		AccessToken accessToken = new AccessToken(account.getToken(), account.getTokenSecret());
    	Twitter twitter = getAuthenticatedApiForAccount(accessToken);
    	
    	int[] array = new int[toFollow.size()];
    	for (int i = 0; i < toFollow.size(); i++) {
			array[i] = toFollow.get(i);			
		}
    	
    	return twitter.lookupUsers(array);		
	}
    
}
