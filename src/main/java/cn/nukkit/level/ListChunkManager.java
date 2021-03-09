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

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        Optional<Block> optionalBlock = this.blocks.stream().filter(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z).findAny();
        return optionalBlock.map(Block::getId).orElseGet(() -> this.parent.getBlockIdAt(x, y, z));
    }

    @Override
    public void setBlockFullIdAt(int x, int y, int z, int fullId) {
        this.blocks.removeIf(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z);
        this.blocks.add(Block.get(fullId, null, x, y, z));
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        Optional<Block> optionalBlock = this.blocks.stream().filter(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z).findAny();
        Block block = optionalBlock.orElse(Block.get(this.getBlockIdAt(x, y, z), this.getBlockDataAt(x, y, z), new Position(x, y, z)));
        this.blocks.remove(block);
        this.blocks.add(Block.get(this.getBlockIdAt(x, y, z), this.getBlockDataAt(x, y, z), new Position(x, y, z)));
    }

    @Override
    public void setBlockAt(int x, int y, int z, int id, int data) {
        this.blocks.removeIf(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z);
        this.blocks.add(Block.get(id, data, new Position(x, y, z)));
    }

    @Override
    public int getBlockDataAt(int x, int y, int z) {
        Optional<Block> optionalBlock = this.blocks.stream().filter(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z).findAny();
        return optionalBlock.map(Block::getDamage).orElseGet(() -> this.parent.getBlockDataAt(x, y, z));
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        Optional<Block> optionalBlock = this.blocks.stream().filter(block -> block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z).findAny();
        Block block = optionalBlock.orElse(Block.get(this.getBlockIdAt(x, y, z), this.getBlockDataAt(x, y, z), new Position(x, y, z)));
        this.blocks.remove(block);
        block.setDamage(data);
        this.blocks.add(block);
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
