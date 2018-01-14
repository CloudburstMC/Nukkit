package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockWallSign extends BlockSignPost {

    public BlockWallSign() {
        this(0);
    }

    public BlockWallSign(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_SIGN;
    }

    @Override
    public String getName() {
        return "Wall Sign";
    }

    @Override
    public int onUpdate(int type) {
        int[] faces = {
                3,
                2,
                5,
                4,
        };
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (this.meta >= 2 && this.meta <= 5) {
                if (this.getSide(BlockFace.fromIndex(faces[this.meta - 2])).getId() == Item.AIR) {
                    this.getLevel().useBreakOn(this);
                }
                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }
}
