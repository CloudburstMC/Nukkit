package test;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import com.google.common.primitives.Ints;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

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

        //try(FileWriter fos = new FileWriter("antes.txt")) {
            for (CompoundTag state : tags.getAll()) {
                //fos.write(state.toString() + '\n');
            CompoundTag block = state.getCompound("block");
            if (block.getString("name").toLowerCase().contains("piston")) {
                StringBuilder builder = new StringBuilder(block.getString("name"));
                for (Tag tag : block.getCompound("states").getAllTags()) {
                    builder.append(';').append(tag.getName()).append('=').append(tag.parseValue());
                }
                System.out.println(builder.toString());
                System.out.println(Ints.asList(state.getIntArray("meta")));
                System.out.println();
            }
            }
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }
}
