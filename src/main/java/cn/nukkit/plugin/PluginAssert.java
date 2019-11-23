package cn.nukkit.plugin;

import cn.nukkit.utils.PluginException;

public class PluginAssert {

    static void isPluginBaseChild(Class<?> javaClass,String main){
        if (!PluginBase.class.isAssignableFrom(javaClass)) {
            throw new PluginException("Main class `" + main + "' does not extend PluginBase");
        }
    }

    static void findMainClass(String name) throws PluginException{
        throw new PluginException("Couldn't load plugin "+name+": main class not found");
    }
}
