package io.freddi.personalizedfavicon.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import io.freddi.personalizedfavicon.utils.Config;
import io.freddi.personalizedfavicon.utils.MongoConnection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@Entity("personalized_favicon")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonalizedFavicon {

    // IP -> Favicon
    private static final HashMap<String, PersonalizedFavicon> local = new HashMap<>();

    @Id
    private ObjectId id;
    private String ip;
    private String uuid;
    private Binary favicon;
    @Builder.Default
    private Date created = new Date();
    @Builder.Default
    private Date lastUsed = new Date();


    public static @Nullable PersonalizedFavicon find(String ip){
        PersonalizedFavicon favicon = (MongoConnection.instance != null ? MongoConnection.instance.getFavicon(ip) : local.getOrDefault(ip, (Config.instance().defaultFavicon().use() ? PersonalizedFavicon.builder().favicon(Config.instance().defaultFavicon().base64()).build() : null)));
        if(favicon != null) {
            favicon.setLastUsed(new Date());
            favicon.save();
        }
        return favicon;
    }

    private static final Random random = new Random();

    public static PersonalizedFavicon find(String uuid, String name, String ip){
        PersonalizedFavicon favicon = (MongoConnection.instance != null ? MongoConnection.instance.getFavicon(ip) : local.get(ip));
        if(MongoConnection.instance != null){
            if(favicon != null) {
                favicon.setIp(ip);
                favicon.setFavicon(generateFavicon(name, uuid));
                favicon.setLastUsed(new Date());
                favicon.save();
                return favicon;
            }
        }
        if(favicon == null) {
            favicon = PersonalizedFavicon.builder()
                    .uuid(uuid)
                    .ip(ip)
                    .favicon(generateFavicon(name, uuid))
                    .created(new Date())
                    .lastUsed(new Date())
                    .build();
        }
        favicon.setLastUsed(new Date());
        favicon.save();
        return favicon;
    }

    private static Binary generateFavicon(String name, String uuid){
        Config.ImageProvider provider = Config.instance().providers().get(random.nextInt(Config.instance().providers().size()));
        String imageUrl = provider.url();
        for (Config.ImageProviderReplacer replacer : provider.replacer()) {
            String value = switch (replacer.value()) {
                case NAME -> name;
                case UUID -> uuid;
                default -> "";
            };
            imageUrl = imageUrl.replace(replacer.key(), value);
        }
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            return new Binary(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        if(MongoConnection.instance != null)
        MongoConnection.instance.getDatastore().save(favicon);
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
}
