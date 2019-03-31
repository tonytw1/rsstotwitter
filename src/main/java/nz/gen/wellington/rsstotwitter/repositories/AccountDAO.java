package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

public interface AccountDAO {

    void saveAccount(TwitterAccount account);

    TwitterAccount getUserByTwitterId(long id);

}
