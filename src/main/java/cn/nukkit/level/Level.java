package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.tile.Tile;
import cn.nukkit.utils.LevelException;

import java.util.List;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Level implements Metadatable {

    private TreeMap<Integer, Tile> tiles = new TreeMap<>();

    public TreeMap<Integer, Tile> updateTiles = new TreeMap<>();

    private BlockMetadataStore blockMetadata;
    private Server server;

    public Level(Server server, String name, String path, LevelProvider provider) {
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
    }

    public String getName() {

        //todo !!!
        return "TODO！！！！！！！！";
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    public Server getServer() {
        return server;
    }

    public void clearChunkCache(int chunkX, int chunkZ) {
        //todo !!!!
    }

    public Block getBlock(Vector3 pos) {
        return this.getBlock(pos, true);
    }

    public Block getBlock(Vector3 pos, boolean cached) {
        //todo !!!!
        return null;
    }

    public void addTile(Tile tile) throws LevelException {
        if (tile.getLevel() != this) {
            throw new LevelException("Invalid Tile level");
        }
        tiles.put(tile.getId(), tile);
        this.clearChunkCache((int) tile.getX() >> 4, (int) tile.getZ() >> 4);
    }

    public void removeTile(Tile tile) throws LevelException {
        if (tile.getLevel() != this) {
            throw new LevelException("Invalid Tile level");
        }
        tiles.remove(tile.getId());
        updateTiles.remove(tile.getId());
        this.clearChunkCache((int) tile.getX() >> 4, (int) tile.getZ() >> 4);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        this.server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        return this.server.getLevelMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.server.getLevelMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        this.server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }


}
