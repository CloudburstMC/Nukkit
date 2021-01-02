package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.generic.BaseFullChunk;

import java.util.ArrayList;
import java.util.List;

public class ListChunkManager implements ChunkManager {

    private final ChunkManager parent;
    private final List<Block> blocks;

    public ListChunkManager(ChunkManager parent) {
        this.parent = parent;
        this.blocks = new ArrayList<>();
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        for (Block block : this.blocks) {
            if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z)
                return block.getId();
        }
        return this.parent.getBlockIdAt(x, y, z);
    }

    @Override
    public void setBlockFullIdAt(int x, int y, int z, int fullId) {
        Block newBlock = Block.get(fullId, null, x, y, z);
        for (int i = 0; i < this.blocks.size(); i++) {
            Block block = this.blocks.get(i);
            if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z) {
                this.blocks.set(i, newBlock);
                return;
            }
        }
        this.blocks.add(newBlock);
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        Block newBlock = Block.get(id, this.getBlockDataAt(x, y, z), new Position(x, y, z));
        for (int i = 0; i < this.blocks.size(); i++) {
            Block block = this.blocks.get(i);
            if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z) {
                this.blocks.set(i, newBlock);
                return;
            }
        }
        this.blocks.add(newBlock);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int id, int data) {
        Block newBlock = Block.get(id, data, new Position(x, y, z));
        for (int i = 0; i < this.blocks.size(); i++) {
            Block block = this.blocks.get(i);
            if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z) {
                this.blocks.set(i, newBlock);
                return;
            }
        }
        this.blocks.add(newBlock);
    }

    @Override
    public int getBlockDataAt(int x, int y, int z) {
        for (Block block : this.blocks) {
            if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z)
                return block.getDamage();
        }
        return this.parent.getBlockDataAt(x, y, z);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        Block newBlock = Block.get(this.getBlockIdAt(x, y, z), data, new Position(x, y, z));
        for (int i = 0; i < this.blocks.size(); i++) {
            Block block = this.blocks.get(i);
            if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z) {
                this.blocks.set(i, newBlock);
                return;
            }
        }
        this.blocks.add(newBlock);
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
