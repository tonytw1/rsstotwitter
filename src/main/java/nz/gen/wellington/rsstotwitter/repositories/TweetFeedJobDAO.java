package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TweetFeedJob;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class TweetFeedJobDAO {
	
    private HibernateTemplate hibernateTemplate;
    
	@SuppressWarnings("unchecked")
	public List<TweetFeedJob> getAllTweetFeedJobs() {
		return hibernateTemplate.loadAll(TweetFeedJob.class);
	}

}
