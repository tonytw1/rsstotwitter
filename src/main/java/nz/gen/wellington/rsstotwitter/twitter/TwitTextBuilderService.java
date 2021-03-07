package nz.gen.wellington.rsstotwitter.twitter;

import nz.gen.wellington.rsstotwitter.model.FeedItem;
import nz.gen.wellington.rsstotwitter.tinyurl.TinyUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TwitTextBuilderService {

    private static final String DASH_SEPARATOR = " - ";

    private final TinyUrlService tinyUrlService;

    @Autowired
    public TwitTextBuilderService(TinyUrlService tinyUrlService) {
        this.tinyUrlService = tinyUrlService;
    }

    public String buildTwitForItem(FeedItem feedItem) throws IOException {
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
        final String publisherPrefix = publisher + DASH_SEPARATOR;
        final boolean publisherWillFit = (twit.length() + publisherPrefix.length()) <= TwitterSettings.MAXIMUM_TWITTER_MESSAGE_LENGTH;
        if (publisherWillFit) {
            return publisherPrefix + twit.toString();
        } else {
            return twit.toString();
        }
    }

}