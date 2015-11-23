package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DoublePlant extends Flowable {

    public DoublePlant() {
        this(0);
    }

    public DoublePlant(int meta) {
        super(DOUBLE_PLANT, meta);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Sunflower",
                "Lilac",
                "Double Tallgrass",
                "Large Fern",
                "Rose Bush",
                "Peony"
        };
        return names[this.meta & 0x07];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(0).isTransparent()) { //Replace with common break method
                this.getLevel().setBlock(this, new Air(), false, false);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int[][] getDrops(Item item) {
        //todo

        return new int[0][];
    }
}
