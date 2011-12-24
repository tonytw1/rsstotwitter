package nz.gen.wellington.rsstotwitter.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.TwitterException;

public class TwitterArchiver implements Runnable {

	private static Logger log = Logger.getLogger(UpdateService.class);

	private TwitterService twitterService;
	private TweetDAO tweetDAO;
	private AccountDAO accountDAO;
			
	public TwitterArchiver(TwitterService twitterService, TweetDAO tweetDAO, AccountDAO accountDAO) {
		this.twitterService = twitterService;
		this.tweetDAO = tweetDAO;
		this.accountDAO = accountDAO;
	}
	
	public void run() {
		log.info("Starting tweet archiver job");
		List<TwitterAccount> allAccounts = accountDAO.getAllTwitterAccounts();
		log.info("Found " + allAccounts.size() + " accounts");
		for (TwitterAccount account : allAccounts) {
			try {
				archiveMentions(account);
			} catch (Exception e) {
				log.error("Unexpected error while attempting to archive mentions for twitter account: " + account.getUsername());
			}
		}
	}
	
	private void archiveMentions(TwitterAccount account) {
        log.info("Running mention archiver for: " + account.getUsername());        
        List<Status> replies = twitterService.getReplies(account);
        log.info("Found " + replies.size() + " replies");
        for (Status status : replies) {
        	if (!tweetDAO.isStoredLocally(status.getId())) {
        		Tweet mentionTweet = new Tweet(status);
        		log.info("Saving new mention tweet: " + mentionTweet.getText());
        		tweetDAO.saveTweet(mentionTweet);
        		account.addMention(mentionTweet);	// TODO Should add from the other side of the relationship
        	}
        }        
        accountDAO.saveAccount(account);
        
        log.info("Finished mention archiver run for: " + account.getUsername());
        
        if (account.getUsername().equals("wellynews")) {
        	log.info("Running auto follower");
        	    		
    		Set<Integer> followers = new HashSet<Integer>(twitterService.getFollowers(account));
    		log.info("Found follows: " + followers.size());

    		List<Integer> friends = twitterService.getFriends(account);
    		log.info("Found friends: " + friends.size());
    		
    		Collection<Integer> toFollow = CollectionUtils.subtract(followers, friends);
    		log.info("Followers whom we don't follow back: " + toFollow);
    		
    		if (!toFollow.isEmpty()) {
    			try {
    				Integer follow = toFollow.iterator().next();
    				log.info("Attempting to follow: " + Integer.toString(follow));
					twitterService.follow(account, follow);
					
				} catch (TwitterException e) {
					log.error(e);
				}
    		}
        	log.info("Finished auto follower");
        }
	}
		
}
