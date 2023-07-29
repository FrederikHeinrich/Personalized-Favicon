package io.freddi.personalizedfavicon.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.freddi.personalizedfavicon.entities.PersonalizedFavicon;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bson.types.Binary;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@Data
@Accessors(fluent = true)
public class Config {

    private static String configPath = "plugins/PersonalizedFavicon/config.json";

    public String editorBackend = "https://configs.freddi.io/personalizedfavicon";

    @Getter
    private static Config instance = Config.load();

    private final String configVersion = "1.0";

    private String siteUrl = "https://render.skinmc.net/3d.php?user=%uuid%&vr=-4&hr0&hrh=53&aa=true&headOnly=true&ratio=50";
    private DefaultFavicon defaultFavicon = new DefaultFavicon();
    private MongoDBConnection mongoDBConnection = new MongoDBConnection();

    private ArrayList<ImageProvider> providers = new ArrayList<>(){
        {
            add(ImageProvider.builder().build());
            add(ImageProvider.builder()
                    .name("minepic")
                    .url("https://minepic.org/head/%name%/64")
                    .replacer(
                            new ArrayList<>(
                                    Collections.singletonList(
                                            ImageProviderReplacer.builder().key("%name%").value(ImageProviderReplacements.NAME).build()
                                    )
                            )
                    )
                    .build());
        }
    };
    public Component editor(){
        //TODO: Open editor
        return Component.text("§cNot implemented yet", TextColor.color(0xFF0000));
    }
    public Component download(String key){
        return Component.text("§cNot implemented yet", TextColor.color(0xFF0000));
    }
    private static Config load() {
        new File("plugins/PersonalizedFavicon").mkdir();
        try (FileReader reader = new FileReader(configPath)) {
            //Read JSON file
            Gson gson = new Gson();
            instance = gson.fromJson(reader, Config.class);
            log.info("Loaded config");
            if(instance.mongoDBConnection.use)
                new MongoConnection(
                        instance.mongoDBConnection.databaseName
                );
        } catch (Exception e) {
            log.error("Failed to load config: " + e.getMessage());
            e.printStackTrace();
            instance = new Config();
        }
        save();
        return instance;
    }

    private static void save(){
        try (FileWriter writer = new FileWriter(configPath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(instance, writer);
        } catch (Exception e) {
            log.error("Failed to save config");
        }
    }
    public Component reload() {
        if(MongoConnection.instance != null)
            MongoConnection.instance.close();
        instance = Config.load();
        return Component.text("§aReloaded config");
    }



    @Data
    @Accessors(fluent = true)
    public static class DefaultFavicon {
        private boolean use = true;
        private Binary base64 = new Binary(Base64.getDecoder().decode("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"));

    }

    @Data
    @Accessors(fluent = true)
    public static class MongoDBConnection {
        private boolean use = false;
        private String databaseName = "personalized_favicon";
        private boolean cache = true;
        private int cacheTime = 100; // Seconds

    }

    @Data
    @Builder
    public static class ImageProvider{
        @Builder.Default
        private String url = "https://crafatar.com/avatars/%uuid%?overlay&size=64";
        @Builder.Default
        private String name = "crafatar";
        @Builder.Default
        private ArrayList<ImageProviderReplacer> replacer = new ArrayList<>(){
            {
                add(new ImageProviderReplacer("%uuid%", ImageProviderReplacements.UUID));
            }
        };
    }

    @Data
    @Builder
    public static class ImageProviderReplacer{
        String key;
        ImageProviderReplacements value;
    }

    public enum ImageProviderReplacements{
        UUID,
        NAME
    }

}
