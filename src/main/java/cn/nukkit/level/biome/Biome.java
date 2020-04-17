package cn.nukkit.level.biome;

import cn.nukkit.Nukkit;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
public class Biome {
    public static final transient CompoundTag BIOME_DEFINITIONS;

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

    protected final Identifier id;
    protected final Set<Identifier> tags;
    protected final float temperature;
    protected final float downfall;

    protected Biome(@NonNull Identifier id, Set<Identifier> tags, float temperature, float downfall)   {
        this.id = id;
        this.tags = tags == null || tags.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(tags);
        this.temperature = temperature;
        this.downfall = downfall;
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean hasTag(Identifier tag)   {
        return this.tags.contains(tag);
    }

    public Set<Identifier> getTags() {
        return this.tags;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public float getDownfall() {
        return this.downfall;
    }
}
