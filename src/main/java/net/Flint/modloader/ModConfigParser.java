package net.Flint.modloader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 模组配置解析器类 - 解析模组配置文件（JSON格式）
 * 从JAR文件中读取模组配置信息并创建ModInfo对象
 */
public class ModConfigParser {

    /**
     * 从JAR文件中解析模组信息
     * 在JAR文件中查找flint.mods.json或fabric.mod.json配置文件并解析模组信息
     * 
     * @param jarFile 模组JAR文件
     * @return 解析得到的ModInfo对象
     * @throws IOException 当文件操作出现异常或未找到配置文件时抛出
     */
    public static ModInfo parseModInfoFromJar(File jarFile) throws IOException {
        try (ZipFile zipFile = new ZipFile(jarFile)) {
            // 尝试查找flint.mods.json配置文件
            ZipEntry entry = zipFile.getEntry("flint.mods.json");
            if (entry == null) {
                // 如果没有找到flint.mods.json，尝试查找fabric.mod.json
                entry = zipFile.getEntry("fabric.mod.json");
            }
            
            if (entry != null) {
                // 如果找到了配置文件，则读取并解析
                try (InputStream inputStream = zipFile.getInputStream(entry)) {
                    String jsonString = readInputStreamToString(inputStream);
                    return parseModInfoFromJson(jsonString);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse mod config from JAR: " + jarFile.getName(), e);
        }
        
        // 如果没有找到有效的配置文件，抛出异常
        throw new IOException("No valid mod configuration file found in JAR: " + jarFile.getName());
    }

    /**
     * 从JSON字符串解析模组信息
     * 解析JSON字符串中的模组元数据并填充到ModInfo对象中
     * 
     * @param jsonString 包含模组信息的JSON字符串
     * @return 解析得到的ModInfo对象
     * @throws JsonSyntaxException 当JSON格式错误时抛出
     */
    private static ModInfo parseModInfoFromJson(String jsonString) throws JsonSyntaxException {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        
        // 创建新的ModInfo对象
        ModInfo modInfo = new ModInfo();
        
        // 解析模组ID，支持"id"或"modid"字段
        if (jsonObject.has("id")) {
            modInfo.setModId(jsonObject.get("id").getAsString());
        } else if (jsonObject.has("modid")) {
            modInfo.setModId(jsonObject.get("modid").getAsString());
        }
        
        // 解析模组版本
        if (jsonObject.has("version")) {
            modInfo.setVersion(jsonObject.get("version").getAsString());
        }
        
        // 解析模组名称
        if (jsonObject.has("name")) {
            modInfo.setName(jsonObject.get("name").getAsString());
        }
        
        // 解析模组描述
        if (jsonObject.has("description")) {
            modInfo.setDescription(jsonObject.get("description").getAsString());
        }
        
        // 解析模组主类，支持多种可能的字段名
        if (jsonObject.has("mainClass")) {
            modInfo.setMainClass(jsonObject.get("mainClass").getAsString());
        } else if (jsonObject.has("mainclass")) {
            modInfo.setMainClass(jsonObject.get("mainclass").getAsString());
        } else if (jsonObject.has("entrypoints")) {
            // 如果是Fabric风格的配置，从entrypoints中获取主类
            JsonObject entrypoints = jsonObject.getAsJsonObject("entrypoints");
            if (entrypoints.has("main")) {
                if (entrypoints.getAsJsonArray("main").size() > 0) {
                    modInfo.setMainClass(entrypoints.getAsJsonArray("main").get(0).getAsString());
                }
            }
        } else if (jsonObject.has("jar-in-jar")) {
            // 如果是jar-in-jar风格的配置
            modInfo.setMainClass(jsonObject.get("jar-in-jar").getAsString());
        }
        
        // TODO: 解析模组依赖关系（目前是空实现）
        if (jsonObject.has("depends") || jsonObject.has("dependencies")) {
        }
        
        return modInfo;
    }

    /**
     * 将输入流读取为字符串
     * 从输入流中读取所有内容并转换为字符串
     * 
     * @param inputStream 要读取的输入流
     * @return 读取得到的字符串内容
     * @throws IOException 当输入流操作出现异常时抛出
     */
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