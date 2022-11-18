package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoTwitterAccountDAO {

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoTwitterAccountDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public void saveAccount(TwitterAccount account) {
        dataStoreFactory.getDs().save(account);
    }

    public TwitterAccount getUserByTwitterId(long id) {
        return dataStoreFactory.getDs().find(TwitterAccount.class, "id", id).get();
    }
}