package cn.nukkit.registry;

import cn.nukkit.Server;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
class RegistryUtils {

    static InputStream getOrAssertResource(String resourcePath) {
        InputStream stream = Server.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new AssertionError("Unable to locate " + resourcePath);
        }
        return stream;
    }
}
