package io.freddi.personalizedfavicon.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class Messages {

    @Getter
    private static Messages instance = load();

    private static Messages load(){
        try (FileReader reader = new FileReader("plugins/PersonalizedFavicon/messages.json")) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Messages.class);
        }catch (IOException e) {
            System.out.println("Failed to load messages.json");
        }
        if(instance == null)
            instance = new Messages();
        save();
        return instance;
    }
    private static void save(){
        try (FileWriter writer = new FileWriter("plugins/PersonalizedFavicon/messages.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(instance, writer);
        } catch (Exception e) {
            System.out.println("Failed to save messages.json");
        }finally {
            System.out.println("Saved messages.json");
        }
    }

    public Component reload() {
        instance = Messages.load();
        return Component.text("§aReloaded messages");
    }

    private String prefix = "§8[§6PersonalizedFavicon§8] §7";

    private String noPermission = prefix + "§cYou don't have permission to do that";

    private String updateAvailable = prefix + "§aUpdate available: §7%version%";

}
