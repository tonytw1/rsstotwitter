package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.Feed;

import org.springframework.orm.hibernate3.HibernateTemplate;

// TODO Rename to FeedDAO
public class TwitteredFeedDAO {

    private HibernateTemplate hibernateTemplate;

    public TwitteredFeedDAO(HibernateTemplate hibernateTemplate) {      
        this.hibernateTemplate = hibernateTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<Feed> getAllFeeds() {       
        return hibernateTemplate.loadAll(Feed.class);
    }

}
