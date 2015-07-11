package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Level implements Metadatable {

    private BlockMetadataStore blockMetadata;
    private Server server;

    public Level(Server server, String name, String path, LevelProvider provider) {
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
    }

    public String getName() {
        return "TODO！！！！！！！！";
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
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
