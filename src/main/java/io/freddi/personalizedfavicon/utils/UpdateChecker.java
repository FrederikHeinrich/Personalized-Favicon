package io.freddi.personalizedfavicon.utils;


import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class UpdateChecker {


    public static UpdateChecker instance = new UpdateChecker();

    @Accessors(fluent = true)
    @Getter
    boolean available = false;
    String latestVersion = "";


    public static void main(String[] args) {
        UpdateChecker checker = new UpdateChecker();
        System.out.println(checker.getLatestVersion());
        checker.checkForUpdates("2.0.0-aaaaaa");
    }
    private static final String VERSION_URL = "https://api.github.com/repos/FrederikHeinrich/Personalized-Favicon/releases";

    private String getLatestVersion(){
        try {
            URL url = new URL(VERSION_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                int startIndex = response.indexOf("\"tag_name\":") + 11;
                int endIndex = response.indexOf("\"", startIndex);
                return response.substring(startIndex, endIndex);
            } finally {
                connection.disconnect();
            }
                } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void checkForUpdates(String version) {

    }

    public Component updateAvailable() {
        return Component.text("§aUpdate available: §7" + latestVersion);
    }
}
