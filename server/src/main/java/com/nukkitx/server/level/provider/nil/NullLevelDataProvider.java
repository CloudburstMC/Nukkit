package com.nukkitx.server.level.provider.nil;

import com.nukkitx.server.level.NukkitLevelData;
import com.nukkitx.server.level.provider.LevelDataProvider;

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
