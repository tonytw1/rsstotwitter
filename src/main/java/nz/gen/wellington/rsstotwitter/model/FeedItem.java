package nz.gen.wellington.rsstotwitter.model;

import java.util.Date;

public class FeedItem {

	private String title;
	private String uri;
	private String link;
	private Date publishedDate;
	private String author;

	public FeedItem(String title, String uri, String link, Date publishedDate, String author) {
		this.title = title;
		this.uri = uri;
		this.link = link;
		this.publishedDate = publishedDate;
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public String getUri() {
		return uri;
	}

	public String getLink() {
		return link;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public String getAuthor() {
		return author;
	}

	public String getGuid() {
		return uri;
	}
	
}
