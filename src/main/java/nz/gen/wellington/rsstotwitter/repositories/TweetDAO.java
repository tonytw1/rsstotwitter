package nz.gen.wellington.rsstotwitter.repositories;

import org.springframework.beans.factory.annotation.Autowired;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class TweetDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public TweetDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public void saveTweet(Tweet tweet) {
		hibernateTemplate.save(tweet);
	}

}
