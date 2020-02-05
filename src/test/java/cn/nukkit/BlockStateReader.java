package cn.nukkit;

import java.io.*;
import java.nio.ByteOrder;

import com.google.common.primitives.Ints;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

public class BlockStateReader {
    public static void main(String[] args) {
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

        try (FileWriter fos = new FileWriter("runtime_block_states.dat.dump.txt"); BufferedWriter bos = new BufferedWriter(fos)) {
            for (CompoundTag state : tags.getAll()) {
                CompoundTag block = state.getCompound("block");
                StringBuilder builder = new StringBuilder(block.getString("name"));
                for (Tag tag : block.getCompound("states").getAllTags()) {
                    builder.append(';').append(tag.getName()).append('=').append(tag.parseValue());
                }
                bos.write(builder.toString());
                bos.newLine();

                bos.write(Ints.asList(state.getIntArray("meta")).toString());
                bos.newLine();

                bos.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
