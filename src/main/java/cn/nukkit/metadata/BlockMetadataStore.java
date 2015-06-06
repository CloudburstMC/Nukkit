package cn.nukkit.metadata;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.Plugin;
import com.sun.javaws.exceptions.InvalidArgumentException;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockMetadataStore extends MetadataStore {
    private Level owningLevel;

    public BlockMetadataStore(Level owningLevel) {
        this.owningLevel = owningLevel;
    }

    @Override
    protected String disambiguate(Metadatable block, String metadataKey) throws Exception {
        if (!(block instanceof Block)) {
            throw new InvalidArgumentException(new String[]{"Argument must be a Block instance"});
        }
        return ((Block) block).x + ":" + ((Block) block).y + ":" + ((Block) block).z + ":" + metadataKey;
    }

    @Override
    public List<MetadataValue> getMetadata(Object block, String metadataKey) throws Exception {
        if (!(block instanceof Block)) {
            throw new InvalidArgumentException(new String[]{"Object must be a Block"});
        }
        if (((Block) block).getLevel().equals(this.owningLevel)) {
            return super.getMetadata(block, metadataKey);
        } else {
            throw new InvalidStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public boolean hasMetadata(Object block, String metadataKey) throws Exception {
        if (!(block instanceof Block)) {
            throw new InvalidArgumentException(new String[]{"Object must be a Block"});
        }
        if (((Block) block).getLevel().equals(this.owningLevel)) {
            return super.hasMetadata(block, metadataKey);
        } else {
            throw new InvalidStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void removeMetadata(Object block, String metadataKey, Plugin owningPlugin) throws Exception {
        if (!(block instanceof Block)) {
            throw new InvalidArgumentException(new String[]{"Object must be a Block"});
        }
        if (((Block) block).getLevel().equals(this.owningLevel)) {
            super.removeMetadata(block, metadataKey, owningPlugin);
        } else {
            throw new InvalidStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void setMetadata(Object block, String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (!(block instanceof Block)) {
            throw new InvalidArgumentException(new String[]{"Object must be a Block"});
        }
        if (((Block) block).getLevel().equals(this.owningLevel)) {
            super.setMetadata(block, metadataKey, newMetadataValue);
        } else {
            throw new InvalidStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }
}
