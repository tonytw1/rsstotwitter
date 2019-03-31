package nz.gen.wellington.rsstotwitter.repositories.mongo;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.*;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.List;

@Component
public class DataStoreFactory {

	private static final Logger log = Logger.getLogger(DataStoreFactory.class);
	    
	private final List<ServerAddress> serverAddresses;
	private final String mongoDatabase;
	private final MongoClientOptions mongoClientOptions;
	
	private final List<MongoCredential> credentials;

	private Datastore datastore;

	@Autowired
	public DataStoreFactory(@Value("${mongo.hosts}") String mongoHosts,
                            @Value("${mongo.database}") String mongoDatabase,
                            @Value("${mongo.user}") String mongoUser,
                            @Value("${mongo.password}") String mongoPassword,
                            @Value("${mongo.ssl}") Boolean mongoSSL) throws UnknownHostException, MongoException {

		List<ServerAddress> addresses = Lists.newArrayList();	// TODO It's 2019 - .map this
		for (String mongoHost : mongoHosts.split(",")) {
			addresses.add(new ServerAddress(mongoHost));
		}
		this.serverAddresses = addresses;

		this.mongoDatabase = mongoDatabase;
		this.mongoClientOptions = MongoClientOptions.builder().sslEnabled(mongoSSL).build();
		this.credentials = !Strings.isNullOrEmpty(mongoUser) ? Lists.newArrayList(MongoCredential.createPlainCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray())) : null;
	}
	
	Datastore getDs() {
		if (datastore == null) {
			datastore = createDataStore(mongoDatabase);
			datastore.ensureIndexes();
		}
		return datastore;
	}
	
	private Datastore createDataStore(String database) {
		Morphia morphia = new Morphia();
		morphia.map(TwitterAccount.class);

		try {
			MongoClient m = credentials != null ? new MongoClient(serverAddresses, credentials, mongoClientOptions) : new MongoClient(serverAddresses, mongoClientOptions);
			return morphia.createDatastore(m, database);
			
		} catch (MongoException e) {
			log.error(e);
			throw new RuntimeException(e);
		}		
	}
	
}