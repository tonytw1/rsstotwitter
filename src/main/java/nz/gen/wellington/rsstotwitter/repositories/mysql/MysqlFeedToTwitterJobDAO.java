package nz.gen.wellington.rsstotwitter.repositories.mysql;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.FeedToTwitterJob;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import nz.gen.wellington.rsstotwitter.repositories.FeedToTwitterJobDAO;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class MysqlFeedToTwitterJobDAO implements FeedToTwitterJobDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public MysqlFeedToTwitterJobDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@Override
	public List<FeedToTwitterJob> getAllTweetFeedJobs() {
		return hibernateTemplate.loadAll(FeedToTwitterJob.class);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<FeedToTwitterJob> getJobsForAccount(TwitterAccount account) {
		return (List<FeedToTwitterJob>) hibernateTemplate.findByCriteria(DetachedCriteria.forClass(FeedToTwitterJob.class).add(Restrictions.eq("account", account)));
	}

}
