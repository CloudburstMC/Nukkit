package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

public class SnowBlock extends Solid {

    public SnowBlock() {
        this(0);
    }

    public SnowBlock(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Snow Block";
    }

    @Override
    public int getId() {
        return SNOW_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    //TODO:雪片叠垒乐

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    /*
        @Override
        public int onUpdate(int type) {

            if (type == Level.BLOCK_UPDATE_NORMAL) {
                if (this.getSide(0).getId() == AIR) {
                    this.getLevel().useBreakOn(this);

                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }

            return 0;
        }
    */
    @Override
    public int[][] getDrops(Item item) {
        if (item.isShovel()) {
            return new int[][]{
                    {Item.SNOWBALL, 0, 8}
            };
        }
        return new int[][]{};
    }
}
