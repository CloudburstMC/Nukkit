package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

public abstract class BlockRoots extends BlockFlowable {
    protected BlockRoots() {
        super(0);
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
