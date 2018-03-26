package cn.nukkit.server.level.provider.anvil;

import cn.nukkit.server.level.NukkitLevelData;
import cn.nukkit.server.level.provider.LevelDataProvider;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.stream.NBTInputStream;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.Tag;

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
