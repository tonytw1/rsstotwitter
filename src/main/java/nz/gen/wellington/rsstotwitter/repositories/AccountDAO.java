package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import java.util.List;

public interface AccountDAO {
    void saveAccount(TwitterAccount account);

    TwitterAccount getUserByTwitterId(long id);

    List<TwitterAccount> getAllTwitterAccounts();
}
