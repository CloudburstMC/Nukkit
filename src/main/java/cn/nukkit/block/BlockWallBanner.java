package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * Created by PetteriM1
 */
public class BlockWallBanner extends BlockBanner {

    public BlockWallBanner(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Wall Banner";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getDamage() >= BlockFace.NORTH.getIndex() && this.getDamage() <= BlockFace.EAST.getIndex()) {
                if (this.getSide(BlockFace.fromIndex(this.getDamage()).getOpposite()).getId() == AIR) {
                    this.getLevel().useBreakOn(this);
                }
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }
}
