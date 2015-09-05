package cn.nukkit.plugin;

import cn.nukkit.permission.Permission;
import cn.nukkit.utils.PluginException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

/**
 * Author: iNevet & MagicDroidX
 * Nukkit Project
 */
public class PluginDescription {

    private String name;
    private String main;
    private List<String> api;
    private List<String> depend = new ArrayList<>();
    private List<String> softDepend = new ArrayList<>();
    private List<String> loadBefore = new ArrayList<>();
    private String version;
    private Map<String, Object> commands = new HashMap<>();
    private String description;
    private List<String> authors = new ArrayList<>();
    private String website;
    private String prefix;
    private PluginLoadOrder order = PluginLoadOrder.POSTWORLD;

    private List<Permission> permissions = new ArrayList<>();

    public PluginDescription(Map<String, Object> yamlMap) {
        this.loadMap(yamlMap);
    }

    public PluginDescription(String yamlString) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        this.loadMap(yaml.loadAs(yamlString, LinkedHashMap.class));
    }

    private void loadMap(Map<String, Object> plugin) throws PluginException {
        this.name = ((String) plugin.get("name")).replaceAll("[^A-Za-z0-9 _.-]", "");
        if (this.name.equals("")) {
            throw new PluginException("Invalid PluginDescription name");
        }
        this.name = this.name.replace(" ", "_");
        this.version = (String) plugin.get("version");
        this.main = (String) plugin.get("main");
        Object api = plugin.get("api");
        if (api instanceof List) {
            this.api = (List<String>) api;
        } else {
            List<String> list = new ArrayList<>();
            list.add((String) api);
            this.api = list;
        }
        if (this.main.startsWith("cn.nukkit.")) {
            throw new PluginException("Invalid PluginDescription main, cannot start within the cn.nukkit. package");
        }

        if (plugin.containsKey("commands") && plugin.get("commands") instanceof Map) {
            this.commands = (Map<String, Object>) plugin.get("commands");
        }

        if (plugin.containsKey("depend")) {
            this.depend = (List<String>) plugin.get("depend");
        }

        if (plugin.containsKey("softdepend")) {
            this.softDepend = (List<String>) plugin.get("softdepend");
        }

        if (plugin.containsKey("loadbefore")) {
            this.loadBefore = (List<String>) plugin.get("loadbefore");
        }

        if (plugin.containsKey("website")) {
            this.website = (String) plugin.get("website");
        }

        if (plugin.containsKey("description")) {
            this.description = (String) plugin.get("description");
        }

        if (plugin.containsKey("prefix")) {
            this.prefix = (String) plugin.get("prefix");
        }

        if (plugin.containsKey("load")) {
            String order = (String) plugin.get("load");
            try {
                this.order = PluginLoadOrder.valueOf(order);
            } catch (Exception e) {
                throw new PluginException("Invalid PluginDescription load");
            }
        }

        if (plugin.containsKey("author")) {
            this.authors.add((String) plugin.get("author"));
        }

        if (plugin.containsKey("authors")) {
            this.authors.addAll((Collection<? extends String>) plugin.get("authors"));
        }

        if (plugin.containsKey("permissions")) {
            this.permissions = Permission.loadPermissions((Map<String, Object>) plugin.get("permissions"));
        }
    }

    public String getFullName() {
        return this.name + " v" + this.version;
    }

    public List<String> getCompatibleApis() {
        return api;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, Object> getCommands() {
        return commands;
    }

    public List<String> getDepend() {
        return depend;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getLoadBefore() {
        return loadBefore;
    }

    public String getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public PluginLoadOrder getOrder() {
        return order;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public List<String> getSoftDepend() {
        return softDepend;
    }

    public String getVersion() {
        return version;
    }

    public String getWebsite() {
        return website;
    }
}
