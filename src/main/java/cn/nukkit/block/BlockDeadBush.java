package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDeadBush extends BlockFlowable {
    public BlockDeadBush() {
        this(0);
    }

    public BlockDeadBush(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dead Bush";
    }

    @Override
    public int getId() {
        return DEAD_BUSH;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if (down.getId() == SAND || down.getId() == HARDENED_CLAY || down.getId() == STAINED_CLAY || down.getId() == PODZOL) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }


    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(0).isTransparent()) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isShears()) {
            return new int[][]{
                    {Item.DEAD_BUSH, 0, 1}
            };
        } else {
            return new int[][]{
                    {Item.STICK, 0, new Random().nextInt(3)}
            };
        }
    }

    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
