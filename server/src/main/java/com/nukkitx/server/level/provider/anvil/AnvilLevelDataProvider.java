package com.nukkitx.server.level.provider.anvil;

import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.server.level.NukkitLevelData;
import com.nukkitx.server.level.provider.LevelDataProvider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AnvilLevelDataProvider extends NukkitLevelData implements LevelDataProvider {
    private final Path levelDatPath;

    private AnvilLevelDataProvider(Path levelDatPath) {
        super();
        this.levelDatPath = levelDatPath;
    }

    public static AnvilLevelDataProvider load(@Nonnull Path levelDatPath) throws IOException {
        CompoundTag tag;
        try (NBTInputStream reader = NbtUtils.createGZIPReader(Files.newInputStream(levelDatPath))) {
            tag = (CompoundTag) reader.readTag();
        }

        CompoundTag dataTag = tag.getAs("Data", CompoundTag.class);
        if (dataTag == null) {
            return new AnvilLevelDataProvider(levelDatPath);
        }


        return new AnvilLevelDataProvider(levelDatPath);
    }

    public void save() {
        save(levelDatPath);
    }

    public void save(Path path) {
    }

    @Override
    public Path getPath() {
        return levelDatPath;
    }
}
