package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;

import java.util.Random;

//和pm源码有点出入，这里参考了wiki

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class GlowingRedstoneOre extends Solid {

    public GlowingRedstoneOre() {
        this(0);
    }

    public GlowingRedstoneOre(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Glowing Redstone Ore";
    }

    @Override
    public int getId() {
        return GLOWING_REDSTONE_ORE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_IRON) {
            return new int[][]{new int[]{Item.REDSTONE_DUST, 0, new Random().nextInt(1) + 4}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            this.getLevel().setBlock(this, Block.get(Item.REDSTONE_ORE, this.meta), false, false);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }
}
