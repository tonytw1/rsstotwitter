package nz.gen.wellington.rsstotwitter.model;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import net.unto.twitter.TwitterProtos.Status;

public class Tweet {

	private Long id;
	private long userId;
	private Date date;
	private String text;
	private String author;
	private long inReplyToUserId;
	
	
	public Tweet() {
		super();
	}

	public Tweet(Status status) {
		this.id = status.getId();
		this.userId = status.getUser().getId();
		this.text = status.getText();
		this.author = status.getUser().getScreenName();
		
		DateTimeFormatter parser = DateTimeFormat.forPattern("E MMM dd HH:mm:ss Z YYYY");
		DateTime time = parser.parseDateTime(status.getCreatedAt());
		this.date = time.toDate();
		
		this.inReplyToUserId = status.getInReplyToUserId();		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}
		
}
