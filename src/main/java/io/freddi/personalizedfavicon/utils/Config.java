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

    private MongoDBConnection mongoDBConnection = new MongoDBConnection();

    private UpdateChecker updateChecker = new UpdateChecker();

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
        new File("plugins/PersonalizedFavicon").mkdirs();
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

    @Data
    public static class UpdateChecker {
        private boolean enabled = true;
        private boolean devBuilds = false;
        private UpdateCheckerNotification notification = new UpdateCheckerNotification();
    }

    @Data
    public static class UpdateCheckerNotification {
        boolean console = true;
        boolean user = true;
    }

}
