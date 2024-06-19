package cn.nukkit.resourcepacks.loader;

import cn.nukkit.Server;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.resourcepacks.ZippedResourcePack;
import com.google.common.io.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZippedResourcePackLoader implements ResourcePackLoader {

    protected final File path;

    public ZippedResourcePackLoader(File path) {
        this.path = path;
        if (!path.exists()) {
            path.mkdirs();
        } else if (!path.isDirectory()) {
            throw new IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", path.getName()));
        }
    }

    @Override
    public List<ResourcePack> loadPacks() {
        List<ResourcePack> loadedResourcePacks = new ArrayList<>();
        for (File pack : path.listFiles()) {
            try {
                String fileExt = Files.getFileExtension(pack.getName());
                if (!pack.isDirectory() && !fileExt.equals("key")) { // Directory resource packs temporarily unsupported
                    switch (fileExt) {
                        case "zip":
                        case "mcpack":
                            loadedResourcePacks.add(new ZippedResourcePack(pack));
                            break;
                        default:
                            Server.getInstance().getLogger().warning(Server.getInstance().getLanguage().translateString("nukkit.resources.unknown-format", pack.getName()));
                    }
                }
            } catch (IllegalArgumentException e) {
                Server.getInstance().getLogger().warning(Server.getInstance().getLanguage().translateString("nukkit.resources.fail", pack.getName(), e.getMessage()), e);
            }
        }
        return loadedResourcePacks;
    }
}
