package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class TwitteredFeedDAO {

    private HibernateTemplate hibernateTemplate;

    public TwitteredFeedDAO(HibernateTemplate hibernateTemplate) {      
        this.hibernateTemplate = hibernateTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<TwitteredFeed> getAllFeeds() {       
        return hibernateTemplate.loadAll(TwitteredFeed.class);
    }

}
