package com.nukkitx.server.level.provider.anvil;

import com.nukkitx.nbt.NBTIO;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.Tag;
import com.nukkitx.server.level.NukkitLevelData;
import com.nukkitx.server.level.provider.LevelDataProvider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class AnvilLevelDataProvider extends NukkitLevelData implements LevelDataProvider {
    private final Path levelDatPath;

    private AnvilLevelDataProvider(Path levelDatPath) {
        super();
        this.levelDatPath = levelDatPath;
    }

    public static AnvilLevelDataProvider load(@Nonnull Path levelDatPath) throws IOException {
        CompoundTag tag;
        try (NBTInputStream reader = NBTIO.createGZIPReader(Files.newInputStream(levelDatPath))) {
            tag = (CompoundTag) reader.readTag();
        }

        Optional<CompoundTag> optionalTag = tag.getAsCompound("Data");
        if (!optionalTag.isPresent()) {
            return new AnvilLevelDataProvider(levelDatPath);
        }
        Map<String, Tag<?>> data = optionalTag.get().getValue();


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
