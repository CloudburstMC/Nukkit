package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockDoublePlant extends BlockFlowable {

    public BlockDoublePlant() {
        this(0);
    }

    public BlockDoublePlant(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_PLANT;
    }

    @Override
    public boolean canBeReplaced() {
    	return this.meta == 2 || this.meta == 3;
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
            if ((this.meta & 0x08) == 8) {
                //top
                if (!(this.getSide(0).getId() == DOUBLE_PLANT)) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                //botom
                if (this.getSide(0).isTransparent() || !(this.getSide(1).getId() == DOUBLE_PLANT)) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public int[][] getDrops(Item item) {
        //todo

        return new int[0][];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
