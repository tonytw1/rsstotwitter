package nz.gen.wellington.twitter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class TwitterService {
    
    public static final int MAXIMUM_TWITTER_MESSAGE_LENGTH = 140;
	private static final int REPLY_PAGES_TO_FETCH = 1;
    
	
    static Logger log = Logger.getLogger(TwitterService.class);

	
    public Status twitter(String twit, String username, String password) {
        if (twit.length() <= MAXIMUM_TWITTER_MESSAGE_LENGTH) {        	
        	 Twitter twitter = getAuthenticatedApi(username, password);
        	 log.info("Twittering: " + twit);
        	 try {
        		 return twitter.updateStatus(twit);		 
        	 } catch (TwitterException e) {
        		 log.warn("A TwitterException occured while trying to tweet: " + e.getMessage());
        		 return null;
        	 }
        	
        } else {
            log.warn("Message to long to twitter; not twittered: " + twit);
        }        
        return null;
    }

    
    public List<Status> getReplies(String username, String password) {
		log.info("Getting twitter replies from live api for " + username);
   	 	Twitter twitter = getAuthenticatedApi(username, password);
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
    
    
    private Twitter getAuthenticatedApi(String username, String password) {
    	Twitter twitter = new TwitterFactory().getInstance(username, password);
    	return twitter;
    }
    
}
