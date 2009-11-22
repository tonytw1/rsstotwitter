package nz.gen.wellington.twitter;

import nz.gen.wellington.tinyurl.TinyUrlService;

public class TwitBuilderService {

    TinyUrlService tinyUrlService;
    
    public TwitBuilderService(TinyUrlService tinyUrlService) {       
        this.tinyUrlService = tinyUrlService;
    }

    public String buildTwitForItem(String title, String link, String publisher, String tag) {
        StringBuffer twit = new StringBuffer();
        twit.append(title);
        if (link != null) {
            final String tinyUrlLink = tinyUrlService.makeTinyUrl(link);            
            twit.append(" ");
            twit.append(tinyUrlLink);
        }
        if (tag != null && !tag.isEmpty()) {
            appendTag(tag, twit);
        }
        
        if (publisher != null && !publisher.isEmpty()) {
        	return prependPublisher(publisher, twit);
        }        
        return twit.toString();
    }

    private String prependPublisher(String publisher, StringBuffer twit) {
    	boolean willFit = (twit.length() + (3 + publisher.length())) <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH;
    	if (willFit) {
    		StringBuilder published = new StringBuilder();
    		published.append(publisher);
    		published.append(" - ");
    		published.append(twit);
    		return published.toString();
    	}
    	return twit.toString();
	}

	private void appendTag(String tag, StringBuffer twit) {
        boolean tagWillFit = (twit.length() + (2 + tag.length())) <= TwitterService.MAXIMUM_TWITTER_MESSAGE_LENGTH;
        if (tagWillFit) {
            twit.append(" #");
            twit.append(tag);
        }
    }

}