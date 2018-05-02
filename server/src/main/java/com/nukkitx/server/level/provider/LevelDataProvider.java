package com.nukkitx.server.level.provider;

import com.nukkitx.api.level.LevelData;

import java.nio.file.Path;

public interface LevelDataProvider extends LevelData {

    void save();

    void save(Path path);

    Path getPath();
}
