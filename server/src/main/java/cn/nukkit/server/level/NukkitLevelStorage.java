package cn.nukkit.server.level;

import cn.nukkit.api.level.LevelCreator;
import cn.nukkit.server.level.provider.ChunkProvider;
import cn.nukkit.server.level.provider.LevelDataProvider;
import cn.nukkit.server.level.provider.anvil.AnvilChunkProvider;
import cn.nukkit.server.level.provider.anvil.AnvilLevelDataProvider;
import cn.nukkit.server.level.provider.leveldb.LevelDBChunkProvider;
import cn.nukkit.server.level.provider.leveldb.LevelDBLevelDataProvider;
import cn.nukkit.server.level.provider.nil.NullChunkProvider;
import cn.nukkit.server.level.provider.nil.NullLevelDataProvider;

import java.io.IOException;
import java.nio.file.Path;

public enum NukkitLevelStorage {
    ANVIL {
        @Override
        public ChunkProvider createChunkProvider(Path levelPath) {
            return new AnvilChunkProvider(levelPath);
        }

        @Override
        public LevelDataProvider createLevelDataProvider(Path levelDatPath) throws IOException {
            return AnvilLevelDataProvider.load(levelDatPath);
        }
    },
    LEVELDB {
        @Override
        public ChunkProvider createChunkProvider(Path levelPath) {
            return new LevelDBChunkProvider(levelPath);
        }

        @Override
        public LevelDataProvider createLevelDataProvider(Path levelDatPath) throws IOException {
            return LevelDBLevelDataProvider.load(levelDatPath);
        }
    },
    NULL {
        @Override
        public ChunkProvider createChunkProvider(Path levelPath) {
            return new NullChunkProvider();
        }

        @Override
        public LevelDataProvider createLevelDataProvider(Path levelDatPath) throws IOException {
            return new NullLevelDataProvider();
        }
    };

    public abstract ChunkProvider createChunkProvider(Path levelPath);

    public abstract LevelDataProvider createLevelDataProvider(Path levelDatPath) throws IOException;

    public static NukkitLevelStorage fromApi(LevelCreator.LevelStorage storage) {
        return NukkitLevelStorage.values()[storage.ordinal()];
    }
}
