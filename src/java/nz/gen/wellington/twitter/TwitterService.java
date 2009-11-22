package nz.gen.wellington.twitter;

import java.util.ArrayList;
import java.util.List;

import net.unto.twitter.Api;
import net.unto.twitter.TwitterProtos.Status;
import net.unto.twitter.methods.RepliesRequest;

import org.apache.log4j.Logger;

public class TwitterService {
    
    public static final int MAXIMUM_TWITTER_MESSAGE_LENGTH = 140;

	private static final int REPLY_PAGES_TO_FETCH = 1;
    
    Logger log = Logger.getLogger(TwitterService.class);

	
    public Status twitter(String twit, String username, String password) {
        if (twit.length() <= MAXIMUM_TWITTER_MESSAGE_LENGTH) {
            Api api = new Api.Builder().username(username).password(password).build();
            log.info("Twittering: " + twit);
            
            Status post = api.updateStatus(twit).build().post();
            return post;
        	
        } else {
            log.warn("Message to long to twitter; not twittered: " + twit);
        }        
        return null;
    }
    
    
    
    
    public List<Status> getReplies(String username, String password) {
		log.info("Getting twitter replies from live api for " + username);
        Api api = new Api.Builder().username(username).password(password).build();
        List<Status> all = new ArrayList<Status>();
        
        // TODO how to paginate this correctly?
		for (int i = 1; i <= REPLY_PAGES_TO_FETCH; i++) {
			RepliesRequest repliesRequest = api.replies().page(i).build();
			repliesRequest.get();
			all.addAll(repliesRequest.get());
		}
		return all;
	}
    
}
