package cn.nukkit.block;

import cn.nukkit.level.Position;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.MainLogger;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Block extends Position implements Metadatable {

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        if (this.getLevel() != null) {
            try {
                this.getLevel().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
            } catch (Exception e) {
                MainLogger.getLogger().logException(e);
            }
        }
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        if (this.getLevel() != null) {
            try {
                return this.getLevel().getBlockMetadata().getMetadata(this, metadataKey);
            } catch (Exception e) {
                MainLogger.getLogger().logException(e);
            }
        }
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        if (this.getLevel() != null) {
            try {
                return this.getLevel().getBlockMetadata().hasMetadata(this, metadataKey);
            } catch (Exception e) {
                MainLogger.getLogger().logException(e);
            }
        }
        return false;
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        if (this.getLevel() != null) {
            try {
                this.getLevel().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
            } catch (Exception e) {
                MainLogger.getLogger().logException(e);
            }
        }
    }

}
