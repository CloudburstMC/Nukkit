package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockOre extends BlockSolid {

    public BlockOre() {
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!this.canHarvest(item) || item.getTier() < this.getToolTier()) {
            return new Item[0];
        }

        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{this.toItem()};
        }

        int rawMaterial = this.getRawMaterial();
        if (rawMaterial == BlockID.AIR) {
            return super.getDrops(item);
        }

        float multiplier = this.getDropMultiplier();
        int amount = (int) multiplier;
        if (amount > 1) {
            amount = 1 + ThreadLocalRandom.current().nextInt(amount);
        }
        int fortuneLevel = NukkitMath.clamp(item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING), 0, 3);
        if (fortuneLevel > 0) {
            int increase = ThreadLocalRandom.current().nextInt((int)(multiplier * fortuneLevel) + 1);
            amount += increase;
        }
        return new Item[]{ Item.get(rawMaterial, this.getRawMaterialMeta(), amount) };
    }

    protected abstract int getRawMaterial();

    protected int getRawMaterialMeta() {
        return 0;
    }

    protected float getDropMultiplier() {
        return 1;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 3;
    }
}
