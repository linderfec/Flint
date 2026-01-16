package net.Flint.modloader;

/**
 * 模组信息类 - 存储单个模组的详细信息
 * 包含模组的基本信息、主类名称和依赖关系
 */
public class ModInfo {
    /**
     * 模组的唯一标识符
     */
    private String modId;
    
    /**
     * 模组版本号
     */
    private String version;
    
    /**
     * 模组的主类完全限定名
     */
    private String mainClass;
    
    /**
     * 模组显示名称
     */
    private String name;
    
    /**
     * 模组描述信息
     */
    private String description;
    
    /**
     * 模组依赖的其他模组ID数组
     */
    private String[] dependencies;

    /**
     * 无参构造函数 - 创建空的模组信息实例
     */
    public ModInfo() {}

    /**
     * 构造函数 - 创建模组信息实例
     * 
     * @param modId 模组的唯一标识符
     * @param version 模组版本号
     * @param mainClass 模组的主类完全限定名
     * @param name 模组显示名称
     * @param description 模组描述信息
     * @param dependencies 模组依赖的其他模组ID数组
     */
    public ModInfo(String modId, String version, String mainClass, String name, String description, String[] dependencies) {
        this.modId = modId;
        this.version = version;
        this.mainClass = mainClass;
        this.name = name;
        this.description = description;
        this.dependencies = dependencies;
    }

    /**
     * 获取模组ID
     * 
     * @return 模组的唯一标识符
     */
    public String getModId() {
        return modId;
    }

    /**
     * 设置模组ID
     * 
     * @param modId 模组的唯一标识符
     */
    public void setModId(String modId) {
        this.modId = modId;
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
     * 设置模组版本
     * 
     * @param version 模组版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取模组主类名
     * 
     * @return 模组的主类完全限定名
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * 设置模组主类名
     * 
     * @param mainClass 模组的主类完全限定名
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * 获取模组名称
     * 
     * @return 模组显示名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置模组名称
     * 
     * @param name 模组显示名称
     */
    public void setName(String name) {
        this.name = name;
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
     * 设置模组描述
     * 
     * @param description 模组描述信息
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取模组依赖列表
     * 
     * @return 模组依赖的其他模组ID数组
     */
    public String[] getDependencies() {
        return dependencies;
    }

    /**
     * 设置模组依赖列表
     * 
     * @param dependencies 模组依赖的其他模组ID数组
     */
    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }
    
    /**
     * 返回模组信息的字符串表示
     * 
     * @return 包含模组基本信息的字符串
     */
    @Override
    public String toString() {
        return "ModInfo{" +
                "modId='" + modId + '\'' +
                ", version='" + version + '\'' +
                ", mainClass='" + mainClass + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}