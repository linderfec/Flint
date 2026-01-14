package net.Flint.modloader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModConfigParser {

    public static ModInfo parseModInfoFromJar(File jarFile) throws IOException {
        try (ZipFile zipFile = new ZipFile(jarFile)) {
            ZipEntry entry = zipFile.getEntry("flint.mods.json");
            if (entry == null) {
                entry = zipFile.getEntry("fabric.mod.json");
            }
            
            if (entry != null) {
                try (InputStream inputStream = zipFile.getInputStream(entry)) {
                    String jsonString = readInputStreamToString(inputStream);
                    return parseModInfoFromJson(jsonString);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse mod config from JAR: " + jarFile.getName(), e);
        }
        
        throw new IOException("No valid mod configuration file found in JAR: " + jarFile.getName());
    }

    private static ModInfo parseModInfoFromJson(String jsonString) throws JsonSyntaxException {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        
        ModInfo modInfo = new ModInfo();
        
        if (jsonObject.has("id")) {
            modInfo.setModId(jsonObject.get("id").getAsString());
        } else if (jsonObject.has("modid")) {
            modInfo.setModId(jsonObject.get("modid").getAsString());
        }
        
        if (jsonObject.has("version")) {
            modInfo.setVersion(jsonObject.get("version").getAsString());
        }
        
        if (jsonObject.has("name")) {
            modInfo.setName(jsonObject.get("name").getAsString());
        }
        
        if (jsonObject.has("description")) {
            modInfo.setDescription(jsonObject.get("description").getAsString());
        }
        
        if (jsonObject.has("mainClass")) {
            modInfo.setMainClass(jsonObject.get("mainClass").getAsString());
        } else if (jsonObject.has("mainclass")) {
            modInfo.setMainClass(jsonObject.get("mainclass").getAsString());
        } else if (jsonObject.has("entrypoints")) {
            JsonObject entrypoints = jsonObject.getAsJsonObject("entrypoints");
            if (entrypoints.has("main")) {
                if (entrypoints.getAsJsonArray("main").size() > 0) {
                    modInfo.setMainClass(entrypoints.getAsJsonArray("main").get(0).getAsString());
                }
            }
        } else if (jsonObject.has("jar-in-jar")) {
            modInfo.setMainClass(jsonObject.get("jar-in-jar").getAsString());
        }
        
        if (jsonObject.has("depends") || jsonObject.has("dependencies")) {
        }
        
        return modInfo;
    }

    private static String readInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().trim();
    }
}