package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RedstoneOre extends Solid {

    protected int id = REDSTONE_ORE;

    public RedstoneOre() {
        super(REDSTONE_ORE);
    }

    public RedstoneOre(int meta) {
        super(REDSTONE_ORE, 0);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= 2) {
            return new int[][]{new int[]{Item.REDSTONE_DUST, 0, new Random().nextInt(1) + 4}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_TOUCH) {
            this.getLevel().setBlock(this, Block.get(Item.GLOWING_REDSTONE_ORE, this.meta), false, true);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }
}
