package cn.nukkit;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import test.HumanStringComparator;

import java.io.*;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RuntimeBlockStateDumper {
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
            tags = (ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.LITTLE_ENDIAN, false);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        TreeMap<String, CompoundTag> states = new TreeMap<>(new HumanStringComparator());
        
        for (CompoundTag state : tags.getAll()) {
            CompoundTag block = state.getCompound("block");
            StringBuilder builder = new StringBuilder(block.getString("name"));
            for (Tag tag : block.getCompound("states").getAllTags()) {
                builder.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            states.put(builder.toString(), state);
        }

        try (FileWriter fos = new FileWriter("runtime_block_states.dat.dump.txt"); BufferedWriter bos = new BufferedWriter(fos)) {
            for (Map.Entry<String, CompoundTag> entry : states.entrySet()) {
                CompoundTag state = entry.getValue();
                CompoundTag block = state.getCompound("block");
                
                bos.write(entry.getKey());
                bos.newLine();

                List<String> metas = state.getList("LegacyStates", CompoundTag.class).getAll().stream()
                        .map(t -> t.getInt("id") + ":" + t.getInt("val"))
                        .collect(Collectors.toList());
                bos.write(metas.toString());
                bos.newLine();

                List<CompoundTag> overrides = metaOverrides.get(block.copy().remove("version"));
                if (overrides != null) {
                    List<String> overrideList = overrides.stream()
                            .map(t -> t.getInt("id") + ":" + t.getInt("val"))
                            .collect(Collectors.toList());
                    bos.write(overrideList.toString());
                    bos.newLine();
                }
                
                bos.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
