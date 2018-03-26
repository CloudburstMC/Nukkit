package cn.nukkit.server.level.provider;

import cn.nukkit.api.level.LevelData;

import java.nio.file.Path;

public interface LevelDataProvider extends LevelData {

    void save();

    void save(Path path);

    Path getPath();
}
