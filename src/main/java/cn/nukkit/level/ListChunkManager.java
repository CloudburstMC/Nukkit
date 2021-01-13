package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.generic.BaseFullChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListChunkManager implements ChunkManager {

    private ChunkManager parent;
    private List<Block> blocks;

    public ListChunkManager(ChunkManager parent) {
        this.parent = parent;
        this.blocks = new ArrayList<>();
    }

    private Optional<Block> findBlock(int x, int y, int z) {
        return this.blocks.stream().filter(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z).findAny();
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        return this.findBlock(x, y, z).map(Block::getId).orElseGet(() -> this.parent.getBlockIdAt(x, y, z));
    }

    @Override
    public void setBlockFullIdAt(int x, int y, int z, int fullId) {
        this.findBlock(x, y, z).ifPresent(this.blocks::remove);
        this.blocks.add(Block.get(fullId, null, x, y, z));
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        int blockData = this.getBlockDataAt(x, y, z);
        this.findBlock(x, y, z).ifPresent(this.blocks::remove);
        this.blocks.add(Block.get(id, blockData, new Position(x, y, z)));
    }

    @Override
    public void setBlockAt(int x, int y, int z, int id, int data) {
        this.findBlock(x, y, z).ifPresent(this.blocks::remove);
        this.blocks.add(Block.get(id, data, new Position(x, y, z)));
    }

    @Override
    public int getBlockDataAt(int x, int y, int z) {
        return this.findBlock(x, y, z).map(Block::getDamage).orElseGet(() -> this.parent.getBlockDataAt(x, y, z));
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        int blockId = this.getBlockIdAt(x, y, z);
        this.findBlock(x, y, z).ifPresent(this.blocks::remove);
        this.blocks.add(Block.get(blockId, data, new Position(x, y, z)));
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.parent.getChunk(chunkX, chunkZ);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.parent.setChunk(chunkX, chunkZ);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk) {
        this.parent.setChunk(chunkX, chunkZ, chunk);
    }

    @Override
    public long getSeed() {
        return this.parent.getSeed();
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

}
