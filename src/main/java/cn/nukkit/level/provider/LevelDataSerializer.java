package cn.nukkit.level.provider;

import cn.nukkit.level.LevelData;
import cn.nukkit.utils.LoadState;

import java.io.IOException;
import java.nio.file.Path;

public interface LevelDataSerializer {

    LoadState load(LevelData data, Path levelPath, String levelId) throws IOException;

    void save(LevelData data, Path levelPath, String levelId) throws IOException;
}
