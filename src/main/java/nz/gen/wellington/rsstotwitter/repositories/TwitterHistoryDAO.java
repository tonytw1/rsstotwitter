package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;
import nz.gen.wellington.rsstotwitter.model.Feed;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class TwitterHistoryDAO {
    
    private final static Logger log = Logger.getLogger(TwitterHistoryDAO.class);

    private final HibernateTemplate hibernateTemplate;
    
    public TwitterHistoryDAO(HibernateTemplate hibernateTemplate) {        
        this.hibernateTemplate = hibernateTemplate;
    }
    
    @SuppressWarnings("unchecked")
    public boolean hasAlreadyBeenTwittered(String guid) {            
        DetachedCriteria previousEventsCriteria = DetachedCriteria.forClass( TwitterEvent.class ).add( Restrictions.eq( "guid", guid ));        
        List<TwitterEvent> previousEvents = hibernateTemplate.findByCriteria(previousEventsCriteria);
        log.debug("Found " + previousEvents.size() + " previous events for guid: " + guid);
        if (previousEvents.size() > 0) {
            return true;
        }
        return false;
    }

    public void saveTwitterEvent(TwitterEvent event) {
        hibernateTemplate.save(event);
    }
    
    @SuppressWarnings("unchecked")
    public List<TwitterEvent> getAllEvents() {
        return hibernateTemplate.loadAll(TwitterEvent.class);
    }
    
    public void markAsTwittered(FeedItem feedItem, Tweet sentTweet) {
        TwitterEvent newEvent = new TwitterEvent(feedItem.getGuid(), sentTweet.getText(), new DateTime().toDate(), feedItem.getAuthor(), feedItem.getFeed(), sentTweet);
        saveTwitterEvent(newEvent);
    }
    
	public int getNumberOfTwitsInLastTwentyFourHours(Feed feed) {				
		DetachedCriteria lastTwentyFourHoursCriteria = makeFeedLastTwentyFourHoursCriteria(feed);
		int result = hibernateTemplate.findByCriteria(lastTwentyFourHoursCriteria).size();
    	log.debug("Feed '" + feed.getUrl() + "' has made " + result + " twits in the last 24 hours");
		return result;
	}

	public int getNumberOfTwitsInLastTwentyFourHours(Feed feed, String publisher) {		
		DetachedCriteria publisherLastTwentyFourHoursCriteria = makeFeedLastTwentyFourHoursCriteria(feed);
		publisherLastTwentyFourHoursCriteria.add( Restrictions.eq("publisher", publisher));						
		return hibernateTemplate.findByCriteria(publisherLastTwentyFourHoursCriteria).size();
	}
	
	private DetachedCriteria makeFeedLastTwentyFourHoursCriteria(Feed feed) {
		DateTime twentyFourHoursAgo = new DateTime().minusDays(1);
		DetachedCriteria lastTwentyFourHoursCriteria = DetachedCriteria.forClass(TwitterEvent.class).
			add( Restrictions.eq( "feed", feed )).
			add( Restrictions.gt("date", twentyFourHoursAgo.toDate()));
		return lastTwentyFourHoursCriteria;
	}

}
