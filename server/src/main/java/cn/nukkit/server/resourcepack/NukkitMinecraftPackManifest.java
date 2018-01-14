package cn.nukkit.server.resourcepack;

import cn.nukkit.api.resourcepack.MinecraftPackManifest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public class NukkitMinecraftPackManifest extends MinecraftPackManifest {
    private Path path;

    public boolean verify(Path path) {
        this.path = path;

        if (dependencies == null) {
            dependencies = new ArrayList<>();
        }
        return getFormatVersion() != null && getHeader() != null && getModules() != null && getHeader().getDescription() != null &&
                getHeader().getName() != null && getHeader().getUuid() != null && getHeader().getVersion() != null;
    }

    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }
}