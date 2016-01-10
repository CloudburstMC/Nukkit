package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class SnowLayer extends Flowable {

    public SnowLayer() {
        this(0);
    }

    public SnowLayer(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Snow Layer";
    }

    @Override
    public int getId() {
        return SNOW_LAYER;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.5;
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
        Block down = this.getSide(0);
        if (down.isSolid()) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(Vector3.SIDE_DOWN).isTransparent()) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isShovel()) {
            return new int[][]{
                    {Item.SNOWBALL, 0, 1}
            };
        }
        return new int[][]{};
    }

    @Override
    public Color getMapColor() {
        return Color.snowColor;
    }
}


