package cn.nukkit.bootstrap;

public class Options {
    public boolean SHORT_TITLE = false;
    public boolean ANSI = false;
    public boolean SHOW_USER_INTERFACE = false;
    public String ROOT_DIRECTORY = System.getProperty("user.dir") + "/";
    public String DATA_PATH = this.ROOT_DIRECTORY;
    public String PLUGIN_PATH = this.DATA_PATH + "plugins/";
    public int DEBUG_LEVEL = 1;
}
