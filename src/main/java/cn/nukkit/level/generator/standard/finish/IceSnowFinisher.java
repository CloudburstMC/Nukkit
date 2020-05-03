package cn.nukkit.level.generator.standard.finish;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class IceSnowFinisher implements Finisher {
    public static final Identifier ID = Identifier.fromString("nukkitx:ice_snow");

    @JsonProperty
    protected IntRange height;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.height, "height must be set!");
    }

    @Override
    public void finish(PRandom random, ChunkManager level, int blockX, int blockZ) {
        Biome biome = BiomeRegistry.get().getBiome(level.getChunk(blockX >> 4, blockZ >> 4).getBiome(blockX & 0xF, blockZ & 0xF));
        int y = level.getChunk(blockX >> 4, blockZ >> 4).getHighestBlock(blockX & 0xF, blockZ & 0xF);
        if (this.height.contains(y) && biome.canSnowAt(level, blockX, y + 1, blockZ)) {
            Block block = BlockRegistry.get().getBlock(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ, 0));
            if (block.getId() == BlockIds.WATER) {
                level.setBlockId(blockX, y, blockZ, 0, BlockIds.ICE);
            } else if (y < 255 && block.isSolid()) {
                level.setBlockId(blockX, y + 1, blockZ, 0, BlockIds.SNOW_LAYER);
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
