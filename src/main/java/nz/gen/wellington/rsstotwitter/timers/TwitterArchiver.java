package nz.gen.wellington.rsstotwitter.timers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import nz.gen.wellington.rsstotwitter.repositories.TwitteredFeedDAO;
import nz.gen.wellington.twitter.TwitterService;

import org.apache.log4j.Logger;

import twitter4j.Status;

public class TwitterArchiver {

	Logger log = Logger.getLogger(TwitterUpdateService.class);

	private TwitterService twitterService;
	private TweetDAO tweetDAO;
	private TwitteredFeedDAO twitteredFeedDAO;
	private AccountDAO accountDAO;
	
		
	public TwitterArchiver(TwitterService twitterService, TwitteredFeedDAO twitteredFeedDAO, TweetDAO tweetDAO, AccountDAO accountDAO) {
		this.twitterService = twitterService;
		this.twitteredFeedDAO = twitteredFeedDAO;
		this.tweetDAO = tweetDAO;
		this.accountDAO = accountDAO;
	}


	public void run() {
		Set<TwitterAccount> accountsToArchive = new HashSet<TwitterAccount>();		
		for (TwitteredFeed feed : twitteredFeedDAO.getAllFeeds()) {
			accountsToArchive.add(feed.getAccount());
	    }
		
		for (TwitterAccount account : accountsToArchive) {
			archiveMentions(account);
		}
	}
	
	
	private void archiveMentions(TwitterAccount account) {
        log.info("Running mention archiver for: " + account.getUsername());        
        List<Status> replies = twitterService.getReplies(account.getUsername(), account.getPassword());
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
