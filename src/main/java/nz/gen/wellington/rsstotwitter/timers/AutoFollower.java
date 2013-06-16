package nz.gen.wellington.rsstotwitter.timers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class AutoFollower implements Runnable {

	private static final int MAX_TO_FOLLOW = 10;

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
			log.info("Account '" + account.getUsername() + "' is auto follower: " + account.isAutoFollow());
			if (account.isAutoFollow()) {
				autoFollowForAccount(account);
			} else {
				log.info("Account is not set to auto follow; skipping");
			}
		}
		log.info("Finished auto follower");
	}
	
	public void autoFollowForAccount(TwitterAccount account) {
		log.info("Running auto follower for: " + account.getUsername());		    		
		ResponseList<User> toFollow = getToFollow(account);
		
		if (toFollow != null && !toFollow.isEmpty()) {
			for (User user : toFollow) {
				try {
					final boolean shouldFollow = !user.isProtected();
					if (shouldFollow) {
						twitterService.follow(account, user.getId());
					} else {
						log.info("Not following: " + user.getScreenName());
					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
		
		log.info("Finished auto follower for: " + account.getUsername());
	}

	private ResponseList<User> getToFollow(TwitterAccount account) {
		Set<Long> followers = new HashSet<Long>(twitterService.getFollowers(account));
		log.info("Found follows: " + followers.size());

		List<Long> friends = twitterService.getFriends(account);
		log.info("Found friends: " + friends.size());
		
		Collection<Long> toFollow = CollectionUtils.subtract(followers, friends);
		log.info("Followers whom we don't follow back: " + toFollow);
		
		List<Long> arrayList = new ArrayList<Long>(toFollow);
		if (arrayList.size() > MAX_TO_FOLLOW) {
			arrayList = arrayList.subList(0, MAX_TO_FOLLOW);
		}
		
		try {
			return twitterService.getUserDetails(arrayList, account);
		} catch (TwitterException e) {
			log.error("Twitter exception while getting user details: " + e.getMessage());
		}
		return null;
	}
	
}
