package nz.gen.wellington.rsstotwitter.twitter;

import com.twitter.twittertext.TwitterTextParseResults;
import com.twitter.twittertext.TwitterTextParser;
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
            return prependPublisherIfRoom(twit, feedItem.getAuthor());
        }
        return twit.toString();
    }

    private String prependPublisherIfRoom(StringBuffer twit, String publisher) {
        final String publisherPrefix = publisher + DASH_SEPARATOR;
        final String proposedAppend = publisherPrefix + twit;

        TwitterTextParseResults twitterTextParseResults = TwitterTextParser.parseTweet(proposedAppend);
        final boolean publisherWillFit = twitterTextParseResults.isValid;
        if (publisherWillFit) {
            return proposedAppend;
        } else {
            return twit.toString();
        }
    }

}