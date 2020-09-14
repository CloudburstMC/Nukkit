package cn.nukkit;

import cn.nukkit.lang.BaseLang;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Config;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServerTest {
    public static void setInstance(Server server) {
        assertDoesNotThrow(()-> {
                    Field instance = Server.class.getDeclaredField("instance");
                    instance.setAccessible(true);
                    instance.set(null, server);
                });
    }
    
    public static void setConfig(Server server, Config config) {
        assertDoesNotThrow(()-> {
            Field instance = Server.class.getDeclaredField("config");
            instance.setAccessible(true);
            instance.set(server, config);
        });
    }

    public static void setLanguage(Server server, BaseLang lang) {
        assertDoesNotThrow(()-> {
            Field instance = Server.class.getDeclaredField("baseLang");
            instance.setAccessible(true);
            instance.set(server, lang);
        });
    }

    public static void setPluginManager(Server server, PluginManager pluginManager) {
        assertDoesNotThrow(()-> {
            Field instance = Server.class.getDeclaredField("pluginManager");
            instance.setAccessible(true);
            instance.set(server, pluginManager);
        });
    }
}
