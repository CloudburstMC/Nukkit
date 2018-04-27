package cn.nukkit.server.level.chunk;

import cn.nukkit.api.block.BlockSnapshot;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.chunk.ChunkSnapshot;
import cn.nukkit.api.level.data.Biome;
import cn.nukkit.api.metadata.blockentity.BlockEntity;
import cn.nukkit.server.block.NukkitBlock;
import cn.nukkit.server.block.NukkitBlockState;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.biome.NukkitBiome;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SectionedChunkSnapshot implements ChunkSnapshot {
    protected final ChunkSection[] sections;
    protected final int x;
    protected final int z;
    protected final byte[] biomeId;
    protected final byte[] height;
    protected final TIntObjectMap<BlockEntity> blockEntities = new TIntObjectHashMap<>();

    public SectionedChunkSnapshot(ChunkSection[] sections, int x, int z) {
        this.sections = sections;
        this.x = x;
        this.z = z;
        this.biomeId = new byte[256];
        this.height = new byte[512];
    }

    public SectionedChunkSnapshot(ChunkSection[] sections, int x, int z, byte[] biomeId, byte[] height) {
        Preconditions.checkArgument(biomeId != null && biomeId.length == 256);
        Preconditions.checkArgument(height != null && height.length == 512);
        this.sections = sections;
        this.x = x;
        this.z = z;
        this.biomeId = biomeId;
        this.height = height;
    }

    static int xyzIdx(int x, int y, int z) {
        return (x << 8) + (z << 4) + y;
    }

    static void checkPosition(int x, int y, int z) {
        checkPosition(x, z);
        Preconditions.checkArgument(y >= 0 && y < 256, "y value (%s) not in range (0 to 255)", y);
    }

    static void checkPosition(int x, int z) {
        Preconditions.checkArgument(x >= 0 && x <= 15, "x value (%s) not in range (0 to 15)", x);
        Preconditions.checkArgument(z >= 0 && z <= 15, "z value (%s) not in range (0 to 15)", z);
    }

    Vector3i getLevelLocation(int chunkX, int y, int chunkZ) {
        return new Vector3i(chunkX + (this.x << 4), y, chunkZ + (this.z << 4));
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public BlockSnapshot getBlock(int x, int y, int z) {
        return getBlock(null, null, x, y, z);
    }

    BlockSnapshot getBlock(Level level, Chunk chunk, int x, int y, int z) {
        checkPosition(x, y, z);
        ChunkSection section = sections[y >> 4];
        Vector3i full = getLevelLocation(x, y, z);

        if (section == null) {
            return new NukkitBlock(level, chunk, full, new NukkitBlockState(BlockTypes.AIR, null, null));
        }

        Optional<BlockState> blockState = NukkitLevel.getPaletteManager().getBlockState(section.getBlockId(x, y & 15, z, 0));
        BlockState state = blockState.orElse(NukkitBlockState.AIR);
        BlockEntity blockEntity = blockEntities.get(xyzIdx(x, y, z));

        if (blockEntity != null) {
            state = new NukkitBlockState(state.getBlockType(), state.getBlockData().orElse(null), blockEntity);
        }

        return new NukkitBlock(level, chunk, full, state);
    }

    @Nonnull
    @Override
    public Biome getBiome(int x, int z) {
        checkPosition(x, z);
        return NukkitBiome.byIdApi(biomeId[(z << 4) | x]);
    }

    @Override
    public int getHighestLayer(int x, int z) {
        return height[(z << 4) + x];
    }

    @Override
    public byte getSkyLight(int x, int y, int z) {
        ChunkSection section = sections[y >> 4];
        if (section == null) {
            return 15;
        }
        return section.getSkyLight(x, y & 15, z);
    }

    @Override
    public byte getBlockLight(int x, int y, int z) {
        ChunkSection section = sections[y >> 4];
        if (section == null) {
            return 15;
        }
        return section.getBlockLight(x, y & 15, z);
    }
}
