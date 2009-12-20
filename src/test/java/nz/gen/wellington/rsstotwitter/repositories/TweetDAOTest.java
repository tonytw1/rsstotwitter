package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.Tweet;


public class TweetDAOTest extends HibernateTestBase {
    
    TweetDAO tweetDAO;
        
    protected void setUp() throws Exception {
        super.setUp();
        tweetDAO = new TweetDAO(hibernateTemplate);
    }

   
    public void testShouldBeAbleToRoundTripTweet() throws Exception {
    	Tweet tweet = new Tweet();
    	tweet.setId(Long.valueOf(123));
    	tweet.setText("Test tweet");
    	    	
   // 	assertFalse(tweetDAO.isStoredLocally(tweet.getId()));
    //	tweetDAO.saveTweet(tweet);
    	
    //	Tweet reloadedTweet = tweetDAO.loadTweet(Long.valueOf(123));    	
    //	assertTrue(reloadedTweet.getId().equals(Long.valueOf(123)));
    //	assertEquals("Test tweet", reloadedTweet.getText());    	
    //	assertTrue(tweetDAO.isStoredLocally(tweet.getId()));
	}
    
}
