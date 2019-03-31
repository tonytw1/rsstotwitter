package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.Tweet;

public interface TweetDAO {
    void saveTweet(Tweet tweet);
}
