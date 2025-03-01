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
        if (item.isPickaxe() && item.getTier() >= this.getToolTier()) {
            if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
                return new Item[]{this.toItem()};
            }

            int rawMaterial = this.getRawMaterial();
            if (rawMaterial == BlockID.AIR) {
                return super.getDrops(item);
            }

            int amount = 1;
            int fortuneLevel = NukkitMath.clamp(item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING), 0, 3);
            if (fortuneLevel > 0) {
                amount += ThreadLocalRandom.current().nextInt(fortuneLevel + 1);
            }

            return new Item[]{Item.get(rawMaterial, this.getRawMaterialMeta(), amount)};
        } else {
            return new Item[0];
        }
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
