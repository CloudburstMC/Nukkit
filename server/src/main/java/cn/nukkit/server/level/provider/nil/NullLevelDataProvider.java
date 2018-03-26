package cn.nukkit.server.level.provider.nil;

import cn.nukkit.server.level.NukkitLevelData;
import cn.nukkit.server.level.provider.LevelDataProvider;

import java.nio.file.Path;

public class NullLevelDataProvider extends NukkitLevelData implements LevelDataProvider {

    public NullLevelDataProvider() {

    }

    @Override
    public void save() {
        // Nothing
    }

    @Override
    public void save(Path path) {
        // Nothing
    }

    @Override
    public Path getPath() {
        return null;
    }
}
