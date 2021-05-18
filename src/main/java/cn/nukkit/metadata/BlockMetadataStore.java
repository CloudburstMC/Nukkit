package cn.nukkit.metadata;

import cn.nukkit.block.Block;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.world.World;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockMetadataStore extends MetadataStore {
    private final World owningLevel;

    public BlockMetadataStore(World owningLevel) {
        this.owningLevel = owningLevel;
    }

    @Override
    protected String disambiguate(Metadatable block, String metadataKey) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Argument must be a Block instance");
        }
        return ((Block) block).x + ":" + ((Block) block).y + ":" + ((Block) block).z + ":" + metadataKey;
    }

    @Override
    public List<MetadataValue> getMetadata(Object block, String metadataKey) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (((Block) block).getWorld() == this.owningLevel) {
            return super.getMetadata(block, metadataKey);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public boolean hasMetadata(Object block, String metadataKey) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (((Block) block).getWorld() == this.owningLevel) {
            return super.hasMetadata(block, metadataKey);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void removeMetadata(Object block, String metadataKey, Plugin owningPlugin) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (((Block) block).getWorld() == this.owningLevel) {
            super.removeMetadata(block, metadataKey, owningPlugin);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void setMetadata(Object block, String metadataKey, MetadataValue newMetadataValue) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (((Block) block).getWorld() == this.owningLevel) {
            super.setMetadata(block, metadataKey, newMetadataValue);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }
}
