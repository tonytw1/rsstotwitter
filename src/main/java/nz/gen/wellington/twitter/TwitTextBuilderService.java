package nz.gen.wellington.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.twitter.TweetFromFeedItemBuilder;
import nz.gen.wellington.tinyurl.TinyUrlService;

public class TwitTextBuilderService {

    private TinyUrlService tinyUrlService;
    
    public TwitTextBuilderService(TinyUrlService tinyUrlService) {       
        this.tinyUrlService = tinyUrlService;
    }

    public String buildTwitForItem(FeedItem feedItem) {
        StringBuffer twit = new StringBuffer();
        twit.append(feedItem.getTitle());
        if (feedItem.getLink() != null) {
            final String tinyUrlLink = tinyUrlService.makeTinyUrl(feedItem.getLink());            
            twit.append(" ");
            twit.append(tinyUrlLink);
        }
        
        if (feedItem.getAuthor() != null && !feedItem.getAuthor().isEmpty()) {
        	return prependPublisher(feedItem.getAuthor(), twit);
        }
        return twit.toString();
    }

    private String prependPublisher(String publisher, StringBuffer twit) {
    	boolean willFit = (twit.length() + (3 + publisher.length())) <= TweetFromFeedItemBuilder.MAXIMUM_TWITTER_MESSAGE_LENGTH;
    	if (willFit) {
    		StringBuilder published = new StringBuilder();
    		published.append(publisher);
    		published.append(" - ");
    		published.append(twit);
    		return published.toString();
    	}
    	return twit.toString();
	}
    
}