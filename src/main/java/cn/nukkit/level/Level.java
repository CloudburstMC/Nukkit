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

    public Level(Server server, String name, String path, LevelProvider provider) {
        this.blockMetadata = new BlockMetadataStore(this);
    }

    public String getName() {
        return "TODO！！！！！！！！";
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        //todo
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        //todo
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        //todo
        return false;
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        //todo
    }
}
