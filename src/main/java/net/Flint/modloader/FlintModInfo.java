package net.Flint.modloader;

import java.util.List;

public class FlintModInfo {
    private final String displayName;  // 模组显示名称
    private final String modID;
    private final String version;
    private final String description; // 模组描述（description 字段）
    private final List<String> authors;

    public FlintModInfo(String displayName, String modId, String version, String description, List<String> authors) {
        this.displayName = displayName == null ? "Unknown Mod" : displayName;
        this.modID = modId == null ? "unknown_mod" : modId;
        this.version = version == null ? "1.0.0" : version;
        this.description = description == null ? "" : description;
        this.authors = authors == null ? List.of() : authors;
    }

    // Getter 方法
    public String getDisplayName() {
        return displayName;
    }

    public String getModID() {
        return modID;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAuthors() {
        return authors;
    }
}
