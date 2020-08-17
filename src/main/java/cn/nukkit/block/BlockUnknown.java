package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockUnknown extends BlockMeta {
    public static final BlockProperties PROPERTIES = CommonBlockProperties.LEGACY_BIG_PROPERTIES; 

    private final int id;

    public BlockUnknown(int id) {
        this(id, 0);
    }

    public BlockUnknown(int id, Integer meta) {
        super(meta);
        this.id = id;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.LEGACY_BIG_PROPERTIES;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Unknown";
    }
}
