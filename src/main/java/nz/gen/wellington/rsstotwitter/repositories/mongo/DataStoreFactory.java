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

	private final Datastore datastore;

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

		log.info("Mongo addresses: " + addresses);
		log.info("Mongo credentials: " + mongoUser + " / " + mongoPassword);

		MongoClientOptions mongoClientOptions = MongoClientOptions.builder().sslEnabled(mongoSSL).build();
		List<MongoCredential> credentials = !Strings.isNullOrEmpty(mongoUser) ? Lists.newArrayList(MongoCredential.createMongoCRCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray())) : null;

		datastore = createDataStore(addresses, mongoDatabase, credentials, mongoClientOptions);
		datastore.ensureIndexes();
	}
	
	Datastore getDs() {
		return datastore;
	}
	
	private Datastore createDataStore(List<ServerAddress> addresses, String database, List<MongoCredential> credentials, MongoClientOptions mongoClientOptions) {
		Morphia morphia = new Morphia();
		morphia.map(TwitterAccount.class);

		try {
			log.info("Mongo credentials: "  + credentials);
			MongoClient m = credentials != null ? new MongoClient(addresses, credentials, mongoClientOptions) : new MongoClient(addresses, mongoClientOptions);
			return morphia.createDatastore(m, database);
			
		} catch (MongoException e) {
			log.error(e);
			throw new RuntimeException(e);
		}		
	}
	
}