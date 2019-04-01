package nz.gen.wellington.rsstotwitter.repositories.mongo;

import com.google.common.base.Strings;
import com.mongodb.*;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataStoreFactory {

	private static final Logger log = Logger.getLogger(DataStoreFactory.class);

	private final Datastore datastore;

	@Autowired
	public DataStoreFactory(@Value("${mongo.hosts}") String mongoHosts,
                            @Value("${mongo.database}") String mongoDatabase,
                            @Value("${mongo.user}") String mongoUser,
                            @Value("${mongo.password}") String mongoPassword,
                            @Value("${mongo.ssl}") Boolean mongoSSL) throws MongoException {

		List<ServerAddress> addresses = Arrays.stream(mongoHosts.split(",")).
						map(mongoHost -> new ServerAddress((mongoHost))).
						collect(Collectors.toList());

		log.debug("Mongo addresses: " + addresses);
		log.debug("Mongo database: " + mongoDatabase);
		log.debug("Mongo credentials: " + "'" + mongoUser + "'" + " / " + "'" + mongoPassword + "'" + " / " + mongoSSL);

		MongoClientOptions mongoClientOptions = MongoClientOptions.builder().sslEnabled(mongoSSL).build();
		MongoCredential credential = !Strings.isNullOrEmpty(mongoUser) ? MongoCredential.createScramSha1Credential(mongoUser, mongoDatabase, mongoPassword.toCharArray()) : null;

		datastore = createDataStore(addresses, mongoDatabase, credential, mongoClientOptions);
		datastore.ensureIndexes();
	}
	
	Datastore getDs() {
		return datastore;
	}
	
	private Datastore createDataStore(List<ServerAddress> addresses, String database, MongoCredential credential, MongoClientOptions mongoClientOptions) {
		Morphia morphia = new Morphia();
		morphia.map(TwitterAccount.class);

		try {
			MongoClient m = credential != null ? new MongoClient(addresses, credential, mongoClientOptions) : new MongoClient(addresses, mongoClientOptions);
			return morphia.createDatastore(m, database);
			
		} catch (MongoException e) {
			log.error(e);
			throw new RuntimeException(e);
		}		
	}
	
}