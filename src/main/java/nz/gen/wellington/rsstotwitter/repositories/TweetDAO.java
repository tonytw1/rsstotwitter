package nz.gen.wellington.rsstotwitter.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import org.springframework.stereotype.Component;

@Component
public class TweetDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public TweetDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public boolean isStoredLocally(long id) {
		return loadTweet(id) != null;
	}

	public void saveTweet(Tweet tweet) {
		hibernateTemplate.save(tweet);
	}

	public Tweet loadTweet(long id) {
		return (Tweet) hibernateTemplate.get(Tweet.class, id);
	}	

}
