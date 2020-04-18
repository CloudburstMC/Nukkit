package cn.nukkit.level.biome;

import cn.nukkit.Nukkit;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.utils.Identifier;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
public class Biome {
    public static final CompoundTag BIOME_DEFINITIONS;
    public static final NoiseSource TEMPERATURE_NOISE = new PerlinNoiseEngine(new FastPRandom(123456789L));

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
    protected final double temperature;
    protected final double downfall;

    protected Biome(@NonNull Identifier id, Set<Identifier> tags, float temperature, float downfall) {
        this.id = id;
        this.tags = tags == null || tags.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(tags);
        this.temperature = temperature;
        this.downfall = downfall;
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean hasTag(Identifier tag) {
        return this.tags.contains(tag);
    }

    public Set<Identifier> getTags() {
        return this.tags;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public double getTemperature(int x, int y, int z)  {
        double temperature = this.temperature;
        if (y > 64) {
            double noise = TEMPERATURE_NOISE.get(x * 0.125d, z * 0.125d) * 4.0d;
            temperature -= (noise + y - 64.0d) * 0.001666666666d;
        }
        return temperature;
    }

    public boolean canSnowAt(ChunkManager level, int x, int y, int z)  {
        //TODO: light level must be less than 10
        return y >= 0 && this.getTemperature(x, y, z) < 0.15d && (y >= 256 || level.getBlockRuntimeIdUnsafe(x, y, z, 0) == 0);
    }

    public double getDownfall() {
        return this.downfall;
    }
}
