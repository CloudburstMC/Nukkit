package test;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.*;

public class SimpleBlocksReader {
    public static void main(String[] args) throws IOException {
        HumanStringComparator humanStringComparator = new HumanStringComparator();
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

        SortedMap<String, SortedMap<String, SortedSet<String>>> states = new TreeMap<>(humanStringComparator);

        for (CompoundTag state : tags.getAll()) {
            CompoundTag block = state.getCompound("block");
            String name = block.getString("name");
            CompoundTag statesCompound = block.getCompound("states");
            if (statesCompound.isEmpty()) {
                states.put(name, Collections.emptySortedMap());
            } else {
                SortedMap<String, SortedSet<String>> registeredProperties = states.computeIfAbsent(name, k-> new TreeMap<>(humanStringComparator));
                for (Tag tag : statesCompound.getAllTags()) {
                    SortedSet<String> registeredValues = registeredProperties.computeIfAbsent(tag.getName(), k -> new TreeSet<>(humanStringComparator));
                    registeredValues.add(tag.parseValue().toString());
                }
            }
        }

        try(FileWriter iniFW = new FileWriter("block-states.ini"); BufferedWriter iniBuff = new BufferedWriter(iniFW);
            FileWriter txtFW = new FileWriter("simple-blocks-nukkit.txt"); BufferedWriter txtBuff = new BufferedWriter(txtFW)) {
            for (Map.Entry<String, SortedMap<String, SortedSet<String>>> topLevelEntry : states.entrySet()) {
                iniBuff.write("["+topLevelEntry.getKey()+"]");
                txtBuff.write(topLevelEntry.getKey());
                txtBuff.newLine();
                iniBuff.newLine();
                for (Map.Entry<String, SortedSet<String>> propertyEntry : topLevelEntry.getValue().entrySet()) {
                    iniBuff.write(propertyEntry.getKey());
                    iniBuff.write('=');
                    iniBuff.write(String.join(",", propertyEntry.getValue()));
                    iniBuff.newLine();
                }
                iniBuff.newLine();
            }
        }
    }
}
