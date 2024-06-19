package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public abstract class BlockRoots extends BlockFlowable {
    
    protected BlockRoots() {
        super(0);
    }

    protected BlockRoots(int meta) {
        super(meta);
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        return this.isSupportValid() && super.place(item, block, target, face, fx, fy, fz, player);
    }
    
    protected boolean isSupportValid() {
        switch (this.down().getId()) {
            case BlockID.GRASS:
            case BlockID.DIRT:
            case BlockID.PODZOL:
            case BlockID.FARMLAND:
            case BlockID.CRIMSON_NYLIUM:
            case BlockID.WARPED_NYLIUM:
            case BlockID.MYCELIUM:
            case BlockID.SOUL_SOIL:
            case BlockID.ROOTED_DIRT:
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