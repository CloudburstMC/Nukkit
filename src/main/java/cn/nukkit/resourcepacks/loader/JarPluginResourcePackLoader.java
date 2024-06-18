package cn.nukkit.resourcepacks.loader;

import cn.nukkit.Server;
import cn.nukkit.lang.BaseLang;
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
        BaseLang baseLang = Server.getInstance().getLanguage();
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        for (File jar : Objects.requireNonNull(jarPath.listFiles())) {
            try {
                ResourcePack resourcePack = null;
                String fileExt = Files.getFileExtension(jar.getName());
                if (!jar.isDirectory()) {
                    if (fileExt.equals("jar") && JarPluginResourcePack.hasResourcePack(jar)) {
                        Server.getInstance().getLogger().info(baseLang.translateString("nukkit.resources.plugin.loading", jar.getName()));
                        resourcePack = new JarPluginResourcePack(jar);
                    }
                }
                if (resourcePack != null) {
                    loadedResourcePacks.add(resourcePack);
                    Server.getInstance().getLogger().info(baseLang.translateString("nukkit.resources.plugin.loaded", jar.getName(), resourcePack.getPackName()));
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(baseLang.translateString("nukkit.resources.fail", jar.getName(), e.getMessage()), e);
            }
        }
        return loadedResourcePacks;
    }
}
