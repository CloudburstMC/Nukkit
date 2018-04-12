/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

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
