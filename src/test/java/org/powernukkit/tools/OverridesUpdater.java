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
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.NonNull;
import org.powernukkit.HumanStringComparator;

import java.io.*;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OverridesUpdater {
    public static void main(String[] args) throws IOException {
        Map<CompoundTag, CompoundTag> originalTags = new LinkedHashMap<>();
        //<editor-fold desc="runtime_block_states.dat" defaultstate="collapsed">
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            //noinspection unchecked
            ListTag<CompoundTag> tags = (ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.LITTLE_ENDIAN, false);
            for (CompoundTag tag : tags.getAll()) {
                originalTags.put(tag.getCompound("block").remove("version"), tag);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>

        Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
        Map<String, Integer> persistenceNameToBlockId = new LinkedHashMap<>();
        //<editor-fold desc="Loading block_ids.csv" defaultstate="collapsed">
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_ids.csv")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block_ids.csv");
            }

            int count = 0;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    count++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    Preconditions.checkArgument(parts.length == 2 || parts[0].matches("^[0-9]+$"));
                    if (parts.length > 1 && parts[1].startsWith("minecraft:")) {
                        blockIdToPersistenceName.put(Integer.parseInt(parts[0]), parts[1]);
                        persistenceNameToBlockId.put(parts[1], Integer.parseInt(parts[0]));
                    }
                }
            } catch (Exception e) {
                throw new IOException("Error reading the line "+count+" of the block_ids.csv", e);
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>
        
        Map<CompoundTag, BlockInfo> infoList = new LinkedHashMap<>();
        //<editor-fold desc="Loading runtime_block_states_overrides.dat" defaultstate="collapsed">
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
                    CompoundTag key = override.getCompound("block").remove("version");
                    CompoundTag original = originalTags.get(key);
                    if (original == null) {
                        continue;
                    }
                    BlockInfo data = new BlockInfo(key, original,
                            original.getList("LegacyStates", CompoundTag.class),
                            override.getList("LegacyStates", CompoundTag.class));
                    BlockInfo removed = infoList.put(key, data);
                    if (removed != null) {
                        throw new IllegalStateException(removed.toString()+"\n"+data.toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>

        ListTag<CompoundTag> newOverrides = new ListTag<>("Overrides");

        for (BlockInfo info : infoList.values()) {
            String stateName = info.getStateName();
            
            if (stateName.contains("torch")) {
                continue;
            }

            CompoundTag override = new CompoundTag();
            override.putCompound("block", info.getKey().copy());
            override.putList((ListTag<? extends Tag>) info.getOverride().copy());
            
            newOverrides.add(override);
        }
      
        SortedMap<String, CompoundTag> sorted = new TreeMap<>(new HumanStringComparator());
        for (CompoundTag tag : originalTags.values()) {
            sorted.put(new BlockInfo(tag.getCompound("block"), tag, new ListTag<>(), new ListTag<>()).getStateName(), tag);
        }

        for (CompoundTag tag : sorted.values()) {
            String name = tag.getCompound("block").getString("name");
            
            if (!name.startsWith("minecraft:frame")) {
                continue;
            }
            
            CompoundTag override = new CompoundTag();
            override.putCompound("block", tag.getCompound("block").remove("version"));
            override.putList(new ListTag<>("LegacyStates")/*.add(new CompoundTag().putInt("id", blockId).putInt("val", 0))*/);
            newOverrides.add(override);
        }

        byte[] bytes = NBTIO.write(new CompoundTag().putList(newOverrides));
        try(FileOutputStream fos = new FileOutputStream("runtime_block_states_overrides.dat")) {
            fos.write(bytes);
        }
    }

    @Data
    static class BlockInfo {
        @NonNull
        private CompoundTag key;
        @NonNull
        private CompoundTag fullData;
        @NonNull
        private ListTag<CompoundTag> original;
        @NonNull
        private ListTag<CompoundTag> override;

        public String getStateName() {
            StringBuilder stateName = new StringBuilder(key.getString("name"));
            for (Tag tag : key.getCompound("states").getAllTags()) {
                stateName.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            return stateName.toString();
        }
        
        public String getBlockName() {
            return key.getString("name");
        }
    }
}
