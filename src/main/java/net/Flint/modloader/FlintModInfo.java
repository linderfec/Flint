package net.Flint.modloader;

import java.util.List;

/**
 * Flint模组信息类 - 存储模组的元数据信息
 * 包含模组的ID、版本、名称和描述等基本信息
 */
public class FlintModInfo {
    /**
     * 模组显示名称
     */
    private final String displayName;
    
    /**
     * 模组的唯一标识符
     */
    private final String modID;
    
    /**
     * 模组版本号
     */
    private final String version;
    
    /**
     * 模组描述信息
     */
    private final String description;
    
    /**
     * 模组作者列表
     */
    private final List<String> authors;

    /**
     * 构造函数 - 创建模组信息实例
     * 
     * @param displayName 模组的显示名称
     * @param modId 模组的唯一标识符
     * @param version 模组版本号
     * @param description 模组描述信息
     * @param authors 模组作者列表
     */
    public FlintModInfo(String displayName, String modId, String version, String description, List<String> authors) {
        this.displayName = displayName == null ? "Unknown Mod" : displayName;
        this.modID = modId == null ? "unknown_mod" : modId;
        this.version = version == null ? "1.0.0" : version;
        this.description = description == null ? "" : description;
        this.authors = authors == null ? List.of() : authors;
    }

    /**
     * 获取模组显示名称
     * 
     * @return 模组显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取模组ID
     * 
     * @return 模组的唯一标识符
     */
    public String getModID() {
        return modID;
    }

    /**
     * 获取模组版本
     * 
     * @return 模组版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 获取模组描述
     * 
     * @return 模组描述信息
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取模组作者列表
     * 
     * @return 模组作者列表
     */
    public List<String> getAuthors() {
        return authors;
    }
}
