package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstone;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;

import java.util.Random;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockOreRedstone extends BlockSolid {

    public BlockOreRedstone() {
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
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int count = new Random().nextInt(2) + 4;

            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                count += new Random().nextInt(fortune.getLevel() + 1);
            }

            return new Item[]{
                    new ItemRedstone(0, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) { //type == Level.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, Block.get(BlockID.GLOWING_REDSTONE_ORE), false, true);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextRange(1, 5);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
