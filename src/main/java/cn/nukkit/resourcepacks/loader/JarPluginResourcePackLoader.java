package cn.nukkit.resourcepacks.loader;

import cn.nukkit.Server;
import cn.nukkit.resourcepacks.JarPluginResourcePack;
import cn.nukkit.resourcepacks.ResourcePack;
import com.google.common.io.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JarPluginResourcePackLoader implements ResourcePackLoader {

    protected final File jarPath;

    public JarPluginResourcePackLoader(File jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public List<ResourcePack> loadPacks() {
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        for (File jar : Objects.requireNonNull(jarPath.listFiles())) {
            try {
                String fileExt = Files.getFileExtension(jar.getName());
                if (!jar.isDirectory()) {
                    if (fileExt.equals("jar") && JarPluginResourcePack.hasResourcePack(jar)) {
                        Server.getInstance().getLogger().info(Server.getInstance().getLanguage().translateString("nukkit.resources.plugin.loading", jar.getName()));
                        loadedResourcePacks.add(new JarPluginResourcePack(jar));
                    }
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(Server.getInstance().getLanguage().translateString("nukkit.resources.fail", jar.getName(), e.getMessage()), e);
            }
        }
        return loadedResourcePacks;
    }
}
