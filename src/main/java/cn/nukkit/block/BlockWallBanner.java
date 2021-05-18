package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import cn.nukkit.world.World;

/**
 * Created by PetteriM1
 */
public class BlockWallBanner extends BlockBanner {

    public BlockWallBanner() {
        this(0);
    }

    public BlockWallBanner(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_BANNER;
    }

    @Override
    public String getName() {
        return "Wall Banner";
    }

    @Override
    public int onUpdate(int type) {
        if (type == World.BLOCK_UPDATE_NORMAL) {
            if (this.getDamage() >= BlockFace.NORTH.getIndex() && this.getDamage() <= BlockFace.EAST.getIndex()) {
                if (this.getSide(BlockFace.fromIndex(this.getDamage()).getOpposite()).getId() == AIR) {
                    this.getWorld().useBreakOn(this);
                }
                return World.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }
}
