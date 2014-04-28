package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class FeedToTwitterJobDAO {
	
    private final HibernateTemplate hibernateTemplate;
    
	public FeedToTwitterJobDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public List<FeedToTwitterJob> getAllTweetFeedJobs() {
		return hibernateTemplate.loadAll(FeedToTwitterJob.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<FeedToTwitterJob> getJobsForAccount(TwitterAccount account) {
		return hibernateTemplate.findByCriteria(DetachedCriteria.forClass( FeedToTwitterJob.class ).add( Restrictions.eq( "account", account)));
	}

}
