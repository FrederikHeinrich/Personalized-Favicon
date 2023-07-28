package io.freddi.personalizedfavicon.utils;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.filters.Filters;
import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import lombok.Getter;

@Getter
public class MongoConnection {

    public static MongoConnection instance;
    private final Datastore datastore;

    public MongoConnection(String uri, String databaseName) {
        instance = this;
        datastore = Morphia.createDatastore(MongoClients.create(uri), databaseName);
        datastore.getMapper().mapPackage("io.freddi.personalizedfavicon.entities");
        datastore.ensureIndexes();
    }

    public PersonalizedFavicon getFavicon(String ip) {
        return datastore.find(PersonalizedFavicon.class).filter(Filters.eq("ip", ip)).first();
    }


}
