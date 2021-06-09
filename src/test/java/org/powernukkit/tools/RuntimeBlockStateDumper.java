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
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.HumanStringComparator;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

public class RuntimeBlockStateDumper {
    public static void main(String[] args) throws IOException {
        //<editor-fold desc="Loading block_ids.csv" defaultstate="collapsed">
        Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
        Map<String, Integer> persistenceNameToBlockId = new LinkedHashMap<>();
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
                        int id = Integer.parseInt(parts[0]);
                        blockIdToPersistenceName.put(id, parts[1]);
                        persistenceNameToBlockId.put(parts[1], id);
                    }
                }
            } catch (Exception e) {
                throw new IOException("Error reading the line "+count+" of the block_ids.csv", e);
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>

        //<editor-fold desc="Loading canonical_block_states.nbt" defaultstate="collapsed">
        List<CompoundTag> tags = new ArrayList<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("canonical_block_states.nbt")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                int runtimeId = 0;
                while (bis.available() > 0) {
                    CompoundTag tag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
                    tag.putInt("runtimeId", runtimeId++);
                    tag.putInt("blockId", persistenceNameToBlockId.getOrDefault(tag.getString("name").toLowerCase(), -1));
                    tags.add(tag);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>

        //<editor-fold desc="Dumping runtime_block_states.dat.dump.txt" defaultstate="collapsed">
        TreeMap<String, CompoundTag> states = new TreeMap<>(new HumanStringComparator());
        
        for (CompoundTag block : tags) {
            StringBuilder builder = new StringBuilder(block.getString("name"));
            for (Tag tag : block.getCompound("states").getAllTags()) {
                builder.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            states.put(builder.toString(), block);
        }

        try (FileWriter fos = new FileWriter("runtime_block_states.dat.dump.txt"); BufferedWriter bos = new BufferedWriter(fos)) {
            bos.write("# WARNING! Don't edit this file! It's automatically regenerated!");
            bos.newLine(); bos.newLine();
            for (Map.Entry<String, CompoundTag> entry : states.entrySet()) {
                CompoundTag block = entry.getValue();

                bos.write(entry.getKey());
                bos.newLine();

                bos.write("blockId=");
                bos.write(Integer.toString(block.getInt("blockId")));
                bos.newLine();
                bos.write("runtimeId=");
                bos.write(block.get("runtimeId").parseValue().toString());
                bos.newLine();

                bos.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //</editor-fold>

        //<editor-fold desc="Dumping block-states.ini and simple-blocks-nukkit.txt" defaultstate="collapsed">
        HumanStringComparator humanStringComparator = new HumanStringComparator();
        SortedMap<String, SortedMap<String, SortedSet<String>>> statesIni = new TreeMap<>(humanStringComparator);

        for (CompoundTag block : tags) {
            String name = block.getString("name");
            CompoundTag statesCompound = block.getCompound("states");
            if (statesCompound.isEmpty()) {
                statesIni.computeIfAbsent(name, k-> new TreeMap<>(humanStringComparator));
            } else {
                SortedMap<String, SortedSet<String>> registeredProperties = statesIni.computeIfAbsent(name, k-> new TreeMap<>(humanStringComparator));
                for (Tag tag : statesCompound.getAllTags()) {
                    SortedSet<String> registeredValues = registeredProperties.computeIfAbsent(tag.getName(), k -> new TreeSet<>(humanStringComparator));
                    registeredValues.add(tag.parseValue().toString());
                }
            }
        }

        SortedSet<String> properties = new TreeSet<>(humanStringComparator);

        try(FileWriter iniFW = new FileWriter("block-states.ini"); BufferedWriter iniBuff = new BufferedWriter(iniFW);
            FileWriter txtFW = new FileWriter("simple-blocks-nukkit.txt"); BufferedWriter txtBuff = new BufferedWriter(txtFW)) {
            iniBuff.write("# WARNING! Don't edit this file! It's automatically regenerated!");
            iniBuff.newLine(); iniBuff.newLine();
            txtBuff.write("# WARNING! Don't edit this file! It's automatically regenerated!");
            txtBuff.newLine(); txtBuff.newLine();
            for (Map.Entry<String, SortedMap<String, SortedSet<String>>> topLevelEntry : statesIni.entrySet()) {
                iniBuff.write("["+topLevelEntry.getKey()+"]");
                txtBuff.write(topLevelEntry.getKey());
                txtBuff.newLine();
                iniBuff.newLine();
                for (Map.Entry<String, SortedSet<String>> propertyEntry : topLevelEntry.getValue().entrySet()) {
                    String propertyLine = propertyEntry.getKey() + "=" + String.join(",", propertyEntry.getValue());
                    properties.add(propertyLine);
                    iniBuff.write(propertyLine);
                    iniBuff.newLine();
                }
                iniBuff.newLine();
            }
        }

        try(FileWriter iniFW = new FileWriter("block-properties.ini"); BufferedWriter iniBuff = new BufferedWriter(iniFW)) {
            iniBuff.write("# WARNING! Don't edit this file! It's automatically regenerated!");
            iniBuff.newLine(); iniBuff.newLine();
            iniBuff.write("[properties]");
            iniBuff.newLine();
            for (String property : properties) {
                iniBuff.write(property);
                iniBuff.newLine();
            }
        }
        //</editor-fold>
    }
}
