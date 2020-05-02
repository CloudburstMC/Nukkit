package test;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;

public class OverridesUpdater {
    public static void main(String[] args) {
        Map<CompoundTag, CompoundTag> originalTags = new LinkedHashMap<>();
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
        
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }
            
            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("meta")) {
                    CompoundTag key = override.getCompound("block").remove("version");
                    CompoundTag original = originalTags.get(key);
                    int id = original.getShort("id");
                    ListTag<CompoundTag> legacyStates = new ListTag<>("LegacyStates");
                    for (int meta : override.getIntArray("meta")) {
                        CompoundTag legacyState = new CompoundTag();
                        legacyState.putInt("id", id);
                        legacyState.putShort("val", meta);
                        legacyStates.add(legacyState);
                    }
                    override.remove("meta");
                    override.putList(legacyStates);
                }
            }

            byte[] bytes = NBTIO.write(new CompoundTag().putList(states));
            try(FileOutputStream fos = new FileOutputStream("runtime_block_states_overrides.dat")) {
                fos.write(bytes);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
