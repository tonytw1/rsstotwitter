package nz.gen.wellington.rsstotwitter.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import twitter4j.TwitterException;

public class AutoFollower implements Runnable {

	private static Logger log = Logger.getLogger(AutoFollower.class);

	private TwitterService twitterService;
	private AccountDAO accountDAO;
			
	public AutoFollower(TwitterService twitterService, AccountDAO accountDAO) {
		this.twitterService = twitterService;
		this.accountDAO = accountDAO;
	}
	
	public void run() {
		log.info("Starting auto follow job");
		List<TwitterAccount> allAccounts = accountDAO.getAllTwitterAccounts();
		log.info("Found " + allAccounts.size() + " accounts");
		for (TwitterAccount account : allAccounts) {			
	        if (account.getUsername().equals("wellynews")) {
	        	autoFollowForAccount(account);
			}
		}
	}
	
	private void autoFollowForAccount(TwitterAccount account) {
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
