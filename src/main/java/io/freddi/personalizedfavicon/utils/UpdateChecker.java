package io.freddi.personalizedfavicon.utils;


import com.google.gson.Gson;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

@Getter
@Accessors(fluent = true)
public class UpdateChecker {


    public static UpdateChecker instance = new UpdateChecker("@version@");

    private Release[] releases;

    private String currentVersion;
    private String currentBuild;

    private Release latestRelease;
    private String latestVersion;
    private String latestDownloadUrl;

    private Release latestDevRelease;
    private String latestDevBuild;
    private String latestDevDownloadUrl;


    public UpdateChecker(String currentVersion) {
        instance = this;
        this.currentVersion = (currentVersion.contains("-") ? currentVersion.split("-")[0] : currentVersion);
        this.currentBuild = (currentVersion.contains("-") ? currentVersion.split("-")[1] : "0");
        check();
        Thread checker = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 60 * 24);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                check();
            }
        });
        checker.start();
        Runtime.getRuntime().addShutdownHook(new Thread(checker::interrupt));
    }

    private void check(){
        String json = getJsonFromUrl("https://api.github.com/repos/FrederikHeinrich/Personalized-Favicon/releases");
        Gson gson = new Gson();
        releases = gson.fromJson(json, Release[].class);
        System.out.println("Releases: " + Arrays.toString(releases));

        latestRelease = releases[0];
        String latestTagName = latestRelease.tag_name;
        latestVersion = (latestTagName.startsWith("v") ? latestTagName.substring(1) : latestTagName);
        latestDownloadUrl = latestRelease.assets[0].browser_download_url;

        latestDevRelease = Arrays.stream(releases).filter(release -> release.tag_name.equalsIgnoreCase("dev")).findFirst().orElse(null);
        assert latestDevRelease != null;
        latestDevBuild = latestDevRelease.target_commitish.substring(0, 7);
        latestDevDownloadUrl = latestDevRelease.assets[0].browser_download_url;

    }

    public static void main(String[] args) {
        UpdateChecker checker = new UpdateChecker("2.9.887-aaaaaaa");
        System.out.println("Version: "+ checker.latestVersion());
        System.out.println("Dev: " + checker.latestDevBuild());
        checker.available();
        checker.consoleCheck();
    }

    public Component updateAvailable() {
        return Component.text("§aUpdate available: §7" + latestVersion);
    }

    public boolean available(){
        if (currentVersion.compareTo(latestVersion) < 0 ) return true;

        if(currentVersion.compareTo(latestVersion) == 0){
            if(Config.instance().updateChecker().devBuilds()){
                return currentBuild.compareTo(latestDevBuild) < 0;
            }
        }
        return false;
    }
    public void consoleCheck() {
        if (available()) {
            System.out.println("Update available: " + latestVersion);
            if(Config.instance().updateChecker().autoDownload()){
                System.out.println("Downloading... " + (Config.instance().updateChecker().devBuilds()? "Dev" : "Release"));
                String downloadUrl = (Config.instance().updateChecker().devBuilds() ? latestDevDownloadUrl : latestDownloadUrl);
                try {
                    downloadFile(downloadUrl, Config.instance().updateChecker().downloadFolder());
                } catch (IOException e) {
                    System.out.println("Error while downloading: " + e.getMessage());
                }
            }
        }
    }

    public static void downloadFile(String fileUrl, String destinationDirectory) throws IOException {
        URL url = new URL(fileUrl);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String destinationPath = destinationDirectory + File.separator + fileName;

        try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
             FileOutputStream outputStream = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private static String getJsonFromUrl(String urlString) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
    public class Release {
        private String url;
        private String assets_url;
        private String upload_url;
        private String html_url;
        private long id;
        private Author author;
        private String node_id;
        private String tag_name;
        private String target_commitish;
        private String name;
        private boolean draft;
        private boolean prerelease;
        private String created_at;
        private String published_at;
        private Asset[] assets;
        private String tarball_url;
        private String zipball_url;
        private String body;

        // Konstruktor, Getter und Setter hier einfügen

        // Innere Klasse für die Autor-Informationen
        public static class Author {
            private String login;
            private long id;
            private String node_id;
            private String avatar_url;
            private String gravatar_id;
            private String url;
            private String html_url;
            private boolean site_admin;

            // Konstruktor, Getter und Setter hier einfügen
        }

        // Innere Klasse für die Asset-Informationen
        public static class Asset {
            private String url;
            private long id;
            private String node_id;
            private String name;
            private String label;
            private Uploader uploader;
            private String content_type;
            private String state;
            private long size;
            private int download_count;
            private String created_at;
            private String updated_at;
            private String browser_download_url;

            // Konstruktor, Getter und Setter hier einfügen

            // Innere Klasse für den Uploader
            public static class Uploader {
                private String login;
                private long id;
                private String nodeId;
                private String avatarUrl;
                private String url;
                private String html_url;
                private boolean site_admin;

                // Konstruktor, Getter und Setter hier einfügen
            }
        }
    }
}
