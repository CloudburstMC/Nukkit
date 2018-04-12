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
