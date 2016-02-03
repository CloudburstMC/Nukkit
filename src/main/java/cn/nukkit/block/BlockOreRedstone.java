package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockOreRedstone extends BlockSolid {

    public BlockOreRedstone() {
        this(0);
    }

    public BlockOreRedstone(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return REDSTONE_ORE;
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
    public String getName() {
        return "Redstone Ore";
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
        if (type == Level.BLOCK_UPDATE_TOUCH) { //type == Level.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, new BlockOreRedstone(this.meta), false, false);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextRange(1, 5);
    }
}
