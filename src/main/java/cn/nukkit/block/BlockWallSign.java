package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockWallSign extends BlockSignPost {

    public BlockWallSign(Identifier id, Identifier signStandingId, Identifier signItemId) {
        super(id, signStandingId, id, signItemId);
    }

    @Override
    public int onUpdate(int type) {
        int[] faces = {
                3,
                2,
                5,
                4,
        };
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getDamage() >= 2 && this.getDamage() <= 5) {
                if (this.getSide(BlockFace.fromIndex(faces[this.getDamage() - 2])).getId() == AIR) {
                    this.getLevel().useBreakOn(this);
                }
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    public static BlockFactory factory(Identifier signStandingId, Identifier signItemId) {
        return signWallId -> new BlockWallSign(signWallId, signStandingId, signItemId);
    }
}
