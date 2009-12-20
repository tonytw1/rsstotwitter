package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;

public class TwitteredFeedDAOTest extends HibernateTestBase {
    
    TwitteredFeedDAO twitteredFeedDAO;
        
    protected void setUp() throws Exception {
        super.setUp();
        twitteredFeedDAO = new TwitteredFeedDAO(hibernateTemplate);
    }

    
    // TODO setup and rollback
    public void testShouldBeAbleToRetrieveAllTwitteredFeeds() throws Exception {       
     //   List<TwitteredFeed> feeds = twitteredFeedDAO.getAllFeeds();
      //  assertEquals(1, feeds.size());
      //  TwitteredFeed feed = feeds.get(0);
       // assertEquals("http://testdata/rss/rss", feed.getUrl());
      //  assertEquals("test", feed.getAccount().getUsername());
      //  assertEquals("password", feed.getAccount().getPassword());
      //  assertEquals("testtag", feed.getTwitterTag());
    }
        
}
