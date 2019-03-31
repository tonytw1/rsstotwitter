package nz.gen.wellington.rsstotwitter.model;

import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.Date;

public class TwitterEvent {

    @Id
    ObjectId objectId;

    private int id;
    private String guid;
    private Date date;
    private String twit;
    private String publisher;
    private Feed feed;
    private Tweet tweet;
    
    public TwitterEvent() {		
	}

	public TwitterEvent(String guid, String twit, Date date, String publisher, Feed feed, Tweet tweet) {
        this.id = 0;
        this.guid = guid;
        this.twit = twit;
        this.date = date;
        this.feed = feed;
        this.publisher = publisher;
        this.tweet = tweet;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getTwit() {
        return twit;
    }
    public void setTwit(String twit) {
        this.twit = twit;
    }
    
    public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}
    
}
