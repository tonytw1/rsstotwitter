package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import org.joda.time.DateTime;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;


public class TwitterHistoryDAOTest extends HibernateTestBase {
    
    TwitterHistoryDAO twitterHistoryDAO;
    
    
    protected void setUp() throws Exception { 
        super.setUp();
        twitterHistoryDAO = new TwitterHistoryDAO(hibernateTemplate);
    }

    
    public void testShouldBeAbleToRetrieveAllTwitterEvents() throws Exception {       
        List<TwitterEvent> events = twitterHistoryDAO.getAllEvents();
        assertTrue(events.size() > 0);
        
        TwitterEvent event = events.get(0);
        assertEquals("http://testdata/rss/123", event.getGuid());
        assertEquals("hello world", event.getTwit());
      
        final DateTime tenthOfJan = new DateTime(2009, 1, 10, 0, 0, 0, 0);       
        assertTrue(tenthOfJan.equals(new DateTime(event.getDate())));
    }
    
    
    public void testEventsShouldBeLinkedToFeed() throws Exception {
        List<TwitterEvent> events = twitterHistoryDAO.getAllEvents();        
        TwitterEvent event = events.get(0);
        assertNotNull(event.getFeed());
        assertNotNull(event.getFeed().getUrl());
    }
    
    
    public void testShouldBeAbleToRecordEvent() throws Exception {        
        DateTime eventDate = new DateTime(2009, 2, 12, 0, 0, 0, 0);
        
        TwitteredFeedDAO feedDAO = new TwitteredFeedDAO(super.hibernateTemplate);
        TwitteredFeed feed = feedDAO.getAllFeeds().get(0);
        
        final String publisher = "Someone";
        Tweet sentTweet = new Tweet();
        sentTweet.setId(new Long(12345));
        TwitterEvent newEvent = new TwitterEvent("http://testdata/rss/new", "The quick brown fox...", eventDate.toDate(), publisher, feed, sentTweet);
        
        final int numberOfEvents =  twitterHistoryDAO.getAllEvents().size();
        assertNotNull(newEvent.getTweet());
        
        twitterHistoryDAO.saveTwitterEvent(newEvent);
        assertEquals(numberOfEvents+1, twitterHistoryDAO.getAllEvents().size());
        
        assertEquals(feed.getId(), twitterHistoryDAO.getAllEvents().get(numberOfEvents).getFeed().getId());
        assertEquals(publisher, twitterHistoryDAO.getAllEvents().get(numberOfEvents).getPublisher());
        assertNotNull(twitterHistoryDAO.getAllEvents().get(numberOfEvents).getTweet());
    }
    

    
    public void testShouldCheckIfHasBeenTwitteredAlready() throws Exception {        
        assertTrue(twitterHistoryDAO.hasAlreadyBeenTwittered("http://riverconditions.visitthames.co.uk/#273-20093523113541"));
    }
    
}
