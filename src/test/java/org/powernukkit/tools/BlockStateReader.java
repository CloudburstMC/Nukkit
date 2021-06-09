/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.powernukkit.tools;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockStateReader {
    public static void main(String[] args) {
        Map<CompoundTag, List<CompoundTag>> metaOverrides = new LinkedHashMap<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }
        
            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }
        
            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("LegacyStates")) {
                    metaOverrides.put(override.getCompound("block").remove("version"), override.getList("LegacyStates", CompoundTag.class).getAll());
                }
            }
        
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        
        ListTag<CompoundTag> tags;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }
            
            //noinspection unchecked
            tags = (ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.BIG_ENDIAN, false);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        Set<String> inspect = Stream.of(
                "minecraft:light_block", "minecraft:wood"
        ).collect(Collectors.toSet());
        
        TreeMap<String, String> blockStates = new TreeMap<>();
        for (CompoundTag state : tags.getAll()) {
            StringBuilder data = new StringBuilder();
            //data.append(state.copy().remove("LegacyStates").toString()).append('\n');
            CompoundTag block = state.getCompound("block");
            if (!inspect.contains(block.getString("name"))) {
                continue;
            }
            StringBuilder stateName = new StringBuilder(block.getString("name"));
            for (Tag tag : block.getCompound("states").getAllTags()) {
                stateName.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            data.append(stateName.toString()).append('\n');
            List<String> metas = state.getList("LegacyStates", CompoundTag.class).getAll().stream()
                    .map(t -> t.getInt("id") + ":" + t.getShort("val"))
                    .collect(Collectors.toList());
            data.append(metas.toString()).append('\n');
            List<CompoundTag> overrides = metaOverrides.get(block.copy().remove("version"));
            if (overrides != null) {
                List<String> overrideList = overrides.stream()
                        .map(t -> t.getInt("id") + ":" + t.getShort("val"))
                        .collect(Collectors.toList());
                data.append(overrideList.toString()).append('\n');
            }
            data.append("\n");
            blockStates.put(stateName.toString(), data.toString());
        }
        
        blockStates.values().forEach(System.out::print);
    }
}
