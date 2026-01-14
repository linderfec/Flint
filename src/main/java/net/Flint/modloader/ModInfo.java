package net.Flint.modloader;

public class ModInfo {
    private String modId;
    private String version;
    private String mainClass;
    private String name;
    private String description;
    private String[] dependencies;

    public ModInfo() {}

    public ModInfo(String modId, String version, String mainClass, String name, String description, String[] dependencies) {
        this.modId = modId;
        this.version = version;
        this.mainClass = mainClass;
        this.name = name;
        this.description = description;
        this.dependencies = dependencies;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }
    
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