package io.freddi.personalizedfavicon.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.filters.Filters;
import io.freddi.personalizedfavicon.utils.Config;
import io.freddi.personalizedfavicon.utils.MongoConnection;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@Entity("favicon")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j(topic = "PersonalizedFavicon")
public class PersonalizedFavicon {

    // IP -> Favicon
    private static final HashMap<String, PersonalizedFavicon> local = new HashMap<>();

    private static final ArrayList<CachedPersonalizedFavicon> cache = new ArrayList<>();

    @Accessors(fluent = true)
    public static class CachedPersonalizedFavicon {
        @Getter
        private final PersonalizedFavicon favicon;
        private Long time = System.currentTimeMillis();

        public CachedPersonalizedFavicon(PersonalizedFavicon favicon) {
            this.favicon = favicon;
        }
    }

    @Id
    private ObjectId id;
    private String ip;
    private String uuid;
    private String name;
    private Binary favicon;
    @Builder.Default
    private Date created = new Date();
    @Builder.Default
    private Date lastUsed = new Date();


    public static @Nullable PersonalizedFavicon find(String ip){
        PersonalizedFavicon favicon;
        if(MongoConnection.instance != null){
            favicon = cache.stream().filter(cachedPersonalizedFavicon -> cachedPersonalizedFavicon.favicon.ip.equalsIgnoreCase(ip))
                    .filter(cachedPersonalizedFavicon -> System.currentTimeMillis() - cachedPersonalizedFavicon.time> (Config.instance().mongoDBConnection().cacheTime() * 1000L)).findFirst().map(cachedPersonalizedFavicon -> cachedPersonalizedFavicon.favicon).orElseGet(() -> MongoConnection.instance.getDatastore().find(PersonalizedFavicon.class).filter(Filters.eq("ip", ip)).first());
            cache.stream().filter(cachedPersonalizedFavicon -> System.currentTimeMillis() - cachedPersonalizedFavicon.time > (Config.instance().mongoDBConnection().cacheTime() * 1000L)).forEach(cache::remove);
        }else {
            favicon = local.get(ip);
        }
        if(favicon != null) {
            favicon.setLastUsed(new Date());
            favicon.save();
        }
        return favicon;
    }

    private static final Random random = new Random();

    public static PersonalizedFavicon find(String uuid, String name, String ip){
        PersonalizedFavicon favicon;
        if(MongoConnection.instance != null){
            favicon = MongoConnection.instance.getDatastore().find(PersonalizedFavicon.class).filter(Filters.eq("ip", ip)).first();
        }else {
            favicon = local.get(ip);
        }
        if(favicon == null) {
            favicon = PersonalizedFavicon.builder()
                    .uuid(uuid)
                    .name(name)
                    .ip(ip)
                    .favicon(generateFavicon(name, uuid))
                    .created(new Date())
                    .lastUsed(new Date())
                    .build();
        }
        favicon.setIp(ip);
        favicon.setFavicon(generateFavicon(name, uuid));
        favicon.setLastUsed(new Date());
        favicon.save();
        return favicon;
    }

    private static Binary generateFavicon(String name, String uuid){
        Config.ImageProvider provider = Config.instance().providers().get(random.nextInt(Config.instance().providers().size()));
        String imageUrl = provider.url();
        for (Config.ImageProviderReplacer replacer : provider.replacer()) {
            String value = (replacer.value() == Config.ImageProviderReplacements.NAME ? name : uuid);
            imageUrl = imageUrl.replace(replacer.key(), value);
        }
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            return new Binary(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        log.info("Saving favicon for " + ip);
        if(MongoConnection.instance != null)
            MongoConnection.instance.getDatastore().save(this);
        if(MongoConnection.instance != null)
            if(Config.instance().mongoDBConnection().cache())
                cache.add(new CachedPersonalizedFavicon(this));
        else local.put(ip, this);
    }

    public BufferedImage toImage(){
        final byte[] bytes = favicon.getData();
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer count() {
        return Math.toIntExact((MongoConnection.instance != null ? MongoConnection.instance.getDatastore().find(PersonalizedFavicon.class).count() : local.size()));
    }

    public static Component clear(){
       final int count = count();
        if(MongoConnection.instance != null)
            MongoConnection.instance.getDatastore().find(PersonalizedFavicon.class).delete();
         else local.clear();
        return Component.text("Cleared " + count + " favicons");
    }
}
