package io.freddi.personalizedfavicon.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.filters.Filters;
import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class MongoConnection {

    public static MongoConnection instance;
    private MongoClient client;
    private Datastore datastore;

    public MongoConnection(String databaseName) {
        instance = this;
        client = MongoClients.create(Secrets.instance.getMongodb());
        datastore = Morphia.createDatastore(client, databaseName);
        datastore.getMapper().map(PersonalizedFavicon.class);
        datastore.ensureIndexes();
    }

    public void close(){
        client.close();
        instance = null;
        datastore = null;
    }

}
