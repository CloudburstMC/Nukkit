package cn.nukkit.server.metadata;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.level.NukkitLevel;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockMetadataStore extends MetadataStore {
    private final NukkitLevel owningLevel;

    public BlockMetadataStore(NukkitLevel owningLevel) {
        this.owningLevel = owningLevel;
    }

    @Override
    protected String disambiguate(Metadatable block, String metadataKey) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Argument must be a BlockType instance");
        }
        return ((Block) block).x + ":" + ((Block) block).y + ":" + ((Block) block).z + ":" + metadataKey;
    }

    @Override
    public List<MetadataValue> getMetadata(Object block, String metadataKey) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a BlockType");
        }
        if (((Block) block).getLevel() == this.owningLevel) {
            return super.getMetadata(block, metadataKey);
        } else {
            throw new IllegalStateException("BlockType does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public boolean hasMetadata(Object block, String metadataKey) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a BlockType");
        }
        if (((Block) block).getLevel() == this.owningLevel) {
            return super.hasMetadata(block, metadataKey);
        } else {
            throw new IllegalStateException("BlockType does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void removeMetadata(Object block, String metadataKey, Plugin owningPlugin) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a BlockType");
        }
        if (((Block) block).getLevel() == this.owningLevel) {
            super.removeMetadata(block, metadataKey, owningPlugin);
        } else {
            throw new IllegalStateException("BlockType does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void setMetadata(Object block, String metadataKey, MetadataValue newMetadataValue) {
        if (!(block instanceof Block)) {
            throw new IllegalArgumentException("Object must be a BlockType");
        }
        if (((Block) block).getLevel() == this.owningLevel) {
            super.setMetadata(block, metadataKey, newMetadataValue);
        } else {
            throw new IllegalStateException("BlockType does not belong to world " + this.owningLevel.getName());
        }
    }
}
