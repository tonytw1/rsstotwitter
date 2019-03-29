package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedToTwitterJobDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public FeedToTwitterJobDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	public List<FeedToTwitterJob> getAllTweetFeedJobs() {
		return hibernateTemplate.loadAll(FeedToTwitterJob.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<FeedToTwitterJob> getJobsForAccount(TwitterAccount account) {
		return (List<FeedToTwitterJob>) hibernateTemplate.findByCriteria(DetachedCriteria.forClass(FeedToTwitterJob.class).add(Restrictions.eq("account", account)));
	}

}
