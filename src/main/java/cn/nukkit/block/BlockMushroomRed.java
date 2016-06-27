package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 03.01.2015.
 */
public class BlockMushroomRed extends BlockFlowable {

    public BlockMushroomRed() {
        this(0);
    }

    public BlockMushroomRed(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    @Override
    public int getId() {
        return RED_MUSHROOM;
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
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
