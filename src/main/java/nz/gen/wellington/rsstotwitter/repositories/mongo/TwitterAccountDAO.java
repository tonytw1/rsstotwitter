package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TwitterAccountDAO {

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public TwitterAccountDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public void saveAccount(Account account) {
        dataStoreFactory.getDs().save(account);
    }

    public Account getUserByTwitterId(long id) {
        return dataStoreFactory.getDs().find(Account.class, "id", id).get();
    }
}