package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import net.unto.twitter.TwitterProtos.Status;
import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class TwitterHistoryDAO {
    
    Logger log = Logger.getLogger(TwitterHistoryDAO.class);

    private HibernateTemplate hibernateTemplate;
    
    public TwitterHistoryDAO(HibernateTemplate hibernateTemplate) {        
        this.hibernateTemplate = hibernateTemplate;
    }
    
    @SuppressWarnings("unchecked")
    public boolean hasAlreadyBeenTwittered(String guid) {            
        DetachedCriteria previousEventsCriteria = DetachedCriteria.forClass( TwitterEvent.class ).add( Restrictions.eq( "guid", guid ));        
        List<TwitterEvent> previousEvents = hibernateTemplate.findByCriteria(previousEventsCriteria);
        log.info("Found " + previousEvents.size() + " previous events for guid: " + guid);
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

    public void markAsTwittered(String guid, String twit, String publisher, TwitteredFeed feed, Tweet sentTweet) {
        TwitterEvent newEvent = new TwitterEvent(guid, twit, new DateTime().toDate(), publisher, feed, sentTweet);
        saveTwitterEvent(newEvent);
    }
    
	public int getNumberOfTwitsInLastTwentyFourHours(TwitteredFeed feed) {				
		DetachedCriteria lastTwentyFourHoursCriteria = makeFeedLastTwentyFourHoursCriteria(feed);
		return hibernateTemplate.findByCriteria(lastTwentyFourHoursCriteria).size();
	}

	public int getNumberOfTwitsInLastTwentyFourHours(TwitteredFeed feed, String publisher) {		
		DetachedCriteria publisherLastTwentyFourHoursCriteria = makeFeedLastTwentyFourHoursCriteria(feed);
		publisherLastTwentyFourHoursCriteria.add( Restrictions.eq("publisher", publisher));						
		return hibernateTemplate.findByCriteria(publisherLastTwentyFourHoursCriteria).size();
	}
	
	private DetachedCriteria makeFeedLastTwentyFourHoursCriteria(TwitteredFeed feed) {
		DateTime twentyFourHoursAgo = new DateTime().minusDays(1);
		DetachedCriteria lastTwentyFourHoursCriteria = DetachedCriteria.forClass(TwitterEvent.class).
			add( Restrictions.eq( "feed", feed )).
			add( Restrictions.gt("date", twentyFourHoursAgo.toDate()));
		return lastTwentyFourHoursCriteria;
	}

}
