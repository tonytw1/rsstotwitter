package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import org.springframework.stereotype.Component;

@Component
public class TwitTextBuilderService {

    private static final String DASH_SEPARATOR = " - ";

    public TwitTextBuilderService() {
    }

    public String buildTwitForItem(FeedItem feedItem) {
        StringBuffer twit = new StringBuffer();
        twit.append(feedItem.getTitle());
        if (feedItem.getLink() != null) {
            twit.append(" ");
            twit.append(feedItem.getLink());
        }

        if (feedItem.getAuthor() != null && !feedItem.getAuthor().isEmpty()) {
            return prependPublisher(feedItem.getAuthor(), twit);
        }
        return twit.toString();
    }

    private String prependPublisher(String publisher, StringBuffer twit) {
        final String publisherPrefix = publisher + DASH_SEPARATOR;
        final boolean publisherWillFit = (twit.length() + publisherPrefix.length()) <= TwitterSettings.MAXIMUM_TWITTER_MESSAGE_LENGTH;
        if (publisherWillFit) {
            return publisherPrefix + twit;
        } else {
            return twit.toString();
        }
    }

}