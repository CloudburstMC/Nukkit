package cn.nukkit.plugin;

import cn.nukkit.Server;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class PluginBase implements Plugin {

    /** @var PluginLoader */
    private PluginLoader loader;
    /** @var \pocketmine\Server */
    private Server server;
    /** @var bool */
    private boolean isEnabled = false;
    /** @var bool */
    private boolean initialized = false;
    /** @var PluginDescription */
    private PluginDescription description;
    /** @var string */
    private String dataFolder;
    private String config;
    /** @var string */
    private String configFile;
    private String file;
    /** @var PluginLogger */
    private PluginLogger logger;


    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }


    /**
     * @return bool
     */
    public final boolean isEnabled(){
        return isEnabled == true;
    }

    /**
     * @param bool boolean
     */
    public final void setEnabled(boolean value){
        if(isEnabled != value){
            isEnabled = value;
            if(isEnabled == true){
                onEnable();
            }else{
                onDisable();
            }
        }
    }
    /**
     * @return bool
     */
    public final boolean isDisabled(){
        return isEnabled == false;
    }
    public final String getDataFolder(){
        return dataFolder;
    }
    public final PluginDescription getDescription(){
        return description;
    }
    public final void init(PluginLoader loader, Server server, PluginDescription description,String dataFolder,String file){
        if(initialized === false){
            initialized = true;
            loader = loader;
            server = server;
            description = description;
            this.dataFolder = dataFolder;
            if(!(dataFolder.endsWith("\\") || dataFolder.endsWith("/"))){
                StringBuffer stringBuffer = new StringBuffer(dataFolder);
                stringBuffer.append("\\");
                this.dataFolder = stringBuffer.toString();
            }

            this.file = file;
            if(!(file.endsWith("\\") || file.endsWith("/"))){
                StringBuffer stringBuffer = new StringBuffer(file);
                stringBuffer.append("\\");
                this.file = stringBuffer.toString();
            }
            configFile = this.dataFolder + "config.yml";
            logger = new PluginLogger(this);
        }
    }
    /**
     * @return PluginLogger
     */
    public PluginLogger getLogger(){
        return logger;
    }
    /**
     * @return bool
     */
    public final boolean isInitialized(){
        return initialized;
    }
    
}
