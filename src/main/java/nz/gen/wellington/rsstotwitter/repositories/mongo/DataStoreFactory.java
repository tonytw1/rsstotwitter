package nz.gen.wellington.rsstotwitter.repositories.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.mongodb.client.MongoClients.create;

@Component
public class DataStoreFactory {

    private static final Logger log = LogManager.getLogger(DataStoreFactory.class);

    private final Datastore datastore;

    @Autowired
    public DataStoreFactory(@Value("${mongo.uri}") String mongoUri,
                            @Value("${mongo.database}")String mongoDatabase) throws MongoException {
        datastore = createDataStore(mongoUri, mongoDatabase);
        datastore.ensureIndexes();
    }

    Datastore getDs() {
        return datastore;
    }

    private Datastore createDataStore(String mongoUri, String mongoDatabase) {
        MapperOptions mapperOptions = MapperOptions.builder().build();

        MongoClient mongoClient = create(mongoUri);
        return Morphia.createDatastore(mongoClient, mongoDatabase, mapperOptions);
    }

}