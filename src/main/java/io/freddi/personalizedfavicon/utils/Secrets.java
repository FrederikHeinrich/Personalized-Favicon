package io.freddi.personalizedfavicon.utils;

import lombok.Getter;

import java.io.*;

@Getter
public class Secrets {

    public static Secrets instance = new Secrets();
    private String mongodb = load("plugins/PersonalizedFavicon/mongodb.secret","mongodb://localhost:27017");

    public void reload(){
        instance = new Secrets();
    }
    private static String load(String filePath, String defaults) {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(defaults);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(file)) {
            int character;
            while ((character = reader.read()) != -1) {
                content.append((char) character);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return content.toString();
    }

}
