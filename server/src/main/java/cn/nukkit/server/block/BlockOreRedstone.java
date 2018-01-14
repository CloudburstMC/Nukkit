package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemRedstone;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.NukkitRandom;

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
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
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
        if (type == NukkitLevel.BLOCK_UPDATE_TOUCH) { //type == NukkitLevel.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, new BlockOreRedstone(this.meta), false, false);

            return NukkitLevel.BLOCK_UPDATE_WEAK;
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
}
