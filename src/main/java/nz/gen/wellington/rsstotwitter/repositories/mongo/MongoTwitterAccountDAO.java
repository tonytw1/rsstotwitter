package nz.gen.wellington.rsstotwitter.repositories.mongo;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoTwitterAccountDAO implements AccountDAO {

    private final static Logger log = Logger.getLogger(MongoTwitterAccountDAO.class);

    private DataStoreFactory dataStoreFactory;

    @Autowired
    public MongoTwitterAccountDAO(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    @Override
    public void saveAccount(TwitterAccount account) {
        dataStoreFactory.getDs().save(account);
    }

    @Override
    public TwitterAccount getUserByTwitterId(long id) {
        return dataStoreFactory.getDs().find(TwitterAccount.class, "id", id).get();
    }
}