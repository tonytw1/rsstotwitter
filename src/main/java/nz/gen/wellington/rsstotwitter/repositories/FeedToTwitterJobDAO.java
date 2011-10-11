package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class FeedToTwitterJobDAO {
	
    private HibernateTemplate hibernateTemplate;
    
	public FeedToTwitterJobDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public List<FeedToTwitterJob> getAllTweetFeedJobs() {
		return hibernateTemplate.loadAll(FeedToTwitterJob.class);
	}

}
