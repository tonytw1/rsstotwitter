package nz.gen.wellington.rsstotwitter.timers;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.log4j.Logger;

import twitter4j.Status;

public class TwitterArchiver {

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
		for (TwitterAccount account : accountDAO.getAllTwitterAccounts()) {
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
        		account.addMention(mentionTweet);
        	}
        }        
        accountDAO.saveAccount(account);        		
        log.info("Finished mention archiver run for: " + account.getUsername());
	}
		
}
