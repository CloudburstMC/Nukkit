package cn.nukkit.plugin;

/**
 * Created by iNevet.
 * Nukkit Project
 */
public class PluginDescription {

    private String name;
    private String main;
    private String apiVersion;
    private String[] depend;
    private String[] softDepend;
    private String[] loadBefore;
    private String version;
    private String[] commands;
    private String description;
    private String[] authors;
    private String website;
    private String prefix;
    private PluginLoadOrder order = PluginLoadOrder.POSTWORLD;

    private String[] permissions;

    public PluginDescription(String yamlString){

    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String[] getDepend() {
        return depend;
    }

    public String getFullName(){
        return name + "v" + this.version;
    }

    public String[] getSoftDepend() {
        return softDepend;
    }

    public String[] getLoadBefore() {
        return loadBefore;
    }

    public String getVersion() {
        return version;
    }

    public String[] getCommands() {
        return commands;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getWebsite() {
        return website;
    }

    public String getPrefix() {
        return prefix;
    }

    public PluginLoadOrder getOrder() {
        return order;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public String getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

}
