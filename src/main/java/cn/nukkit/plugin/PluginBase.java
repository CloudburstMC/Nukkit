package cn.nukkit.plugin;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class PluginBase implements Plugin {
    //todo
    private boolean isEnabled = false;

    public void onLoad() {

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
