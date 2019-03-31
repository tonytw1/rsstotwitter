package nz.gen.wellington.rsstotwitter.repositories.mysql;

import nz.gen.wellington.rsstotwitter.repositories.TweetDAO;
import org.springframework.beans.factory.annotation.Autowired;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class MysqlTweetDAO implements TweetDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public MysqlTweetDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@Override
	public void saveTweet(Tweet tweet) {
		hibernateTemplate.save(tweet);
	}

}
