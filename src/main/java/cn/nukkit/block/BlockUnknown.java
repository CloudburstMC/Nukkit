package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.UnsignedIntBlockProperty;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockUnknown extends BlockMeta {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final UnsignedIntBlockProperty UNKNOWN = new UnsignedIntBlockProperty("unknown", true, 0xFFFFFFFF);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(UNKNOWN);

    private final int id;

    public BlockUnknown(int id) {
        this(id, 0);
    }

    public BlockUnknown(int id, Integer meta) {
        super(0);
        this.id = id;
        if (meta != null && meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
