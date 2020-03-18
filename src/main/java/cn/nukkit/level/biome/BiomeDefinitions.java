package cn.nukkit.level.biome;

import cn.nukkit.Nukkit;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class BiomeDefinitions {
    public final CompoundTag BIOME_DEFINITIONS;

    static {
        InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream("biome_definitions.dat");
        if (inputStream == null) {
            throw new AssertionError("Could not find biome_definitions.dat");
        }
        try (NBTInputStream stream = NbtUtils.createNetworkReader(inputStream)) {
            BIOME_DEFINITIONS = (CompoundTag) stream.readTag();
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading biome_definitions.dat", e);
        }
    }
}
