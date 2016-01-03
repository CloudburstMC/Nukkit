package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;

/**
 * @author Nukkit Project Team
 */
public class BrownMushroom extends Flowable {

    public BrownMushroom() {
        this(0);
    }

    public BrownMushroom(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Brown Mushroom";
    }

    @Override
    public int getId() {
        return Block.BROWN_MUSHROOM;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (getSide(0).isTransparent()) {
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = getSide(0);
        if (!down.isTransparent()) {
            getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

}
