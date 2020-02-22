package cn.nukkit.level.manager;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.chunk.LockableChunk;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of {@link ChunkManager} used during chunk population.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PopulationChunkManager {
    public static ChunkManager create(Chunk chunk, Collection<LockableChunk> allChunks) {
        //porktodo: optimized implementation for simple 3x3 square of chunks
        return new ComplexShape(chunk, allChunks);
    }

    private static final class ComplexShape implements ChunkManager {
        private final Long2ObjectMap<LockableChunk> chunks = new Long2ObjectOpenHashMap<>();
        private final Level level;

        public ComplexShape(Chunk chunk, Collection<LockableChunk> allChunks) {
            this.level = chunk.getLevel();

            for (LockableChunk populationChunk : allChunks) {
                this.chunks.put(populationChunk.key(), populationChunk);
            }
        }

        @Override
        public Identifier getBlockIdAt(int x, int y, int z, int layer) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .getBlockId(x & 0xF, y, z & 0xF, layer);
        }

        @Override
        public void setBlockIdAt(int x, int y, int z, int layer, Identifier id) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .setBlockId(x & 0xF, y, z & 0xF, layer, id);
        }

        @Override
        public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .getBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, layer);
        }

        @Override
        public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .setBlockRuntimeIdUnsafe(x & 0xF, y, z & 0xF, layer, runtimeId);
        }

        @Override
        public int getBlockDataAt(int x, int y, int z, int layer) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .getBlockData(x & 0xF, y, z & 0xF, layer);
        }

        @Override
        public void setBlockDataAt(int x, int y, int z, int layer, int data) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .setBlockData(x & 0xF, y, z & 0xF, layer, data);
        }

        @Override
        public Block getBlockAt(int x, int y, int z, int layer) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            return Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .getBlock(x & 0xF, y, z & 0xF, layer);
        }

        @Override
        public void setBlockAt(int x, int y, int z, int layer, Block block) {
            LockableChunk chunk = this.chunks.get(Chunk.key(x >> 4, z >> 4));
            Preconditions.checkNotNull(chunk, "Block position (%s,%s) out of population bounds", x, z)
                    .setBlock(x & 0xF, y, z & 0xF, layer, block);
        }

        @Override
        public IChunk getChunk(int chunkX, int chunkZ) {
            return Preconditions.checkNotNull(this.chunks.get(Chunk.key(chunkX, chunkZ)), "Chunk position (%s,%s) out of population bounds", chunkX, chunkZ);
        }

        @Override
        public long getSeed() {
            return this.level.getSeed();
        }
    }
}
