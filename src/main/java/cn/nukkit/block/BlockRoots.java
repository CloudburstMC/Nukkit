package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public abstract class BlockRoots extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected BlockRoots() {
        super(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !isSupportValid()) {
            level.useBreakOn(this);
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        return isSupportValid() && super.place(item, block, target, face, fx, fy, fz, player);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean isSupportValid() {
        switch (down().getId()) {
            case WARPED_NYLIUM:
            case CRIMSON_NYLIUM:
            case GRASS:
            case PODZOL:
            case DIRT:
            case SOUL_SOIL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getBurnChance() {
        return 5;
    }
}
