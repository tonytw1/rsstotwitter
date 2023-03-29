package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static dev.morphia.query.filters.Filters.eq;

@Component
public class AccountDAO {

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public AccountDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public void saveAccount(Account account) {
        dataStoreFactory.getDs().save(account);
    }

    public Account getUserByTwitterId(long id) {
        return dataStoreFactory.getDs().find(Account.class).filter(eq("id", id)).first();
    }

    public Account getUserByMastodonId(long id) {
        return dataStoreFactory.getDs().find(Account.class).filter(eq( "mastodonId", id)).first();
    }

}