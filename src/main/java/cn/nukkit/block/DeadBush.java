package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Color;

import java.util.Random;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DeadBush extends Flowable {
    public DeadBush() {
        this(0);
    }

    public DeadBush(int meta) {
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
            return new int[][]{{Item.DEAD_BUSH, 0, 1}};
        } else {
            return new int[][]{{Item.STICK, 0, new Random().nextInt(1) + 2}};
        }
    }

    public Color getColor() {
        return Color.foliageColor;
    }
}
