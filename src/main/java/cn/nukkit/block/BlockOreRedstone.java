package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockOreRedstone extends BlockOre {

    @Override
    public int getId() {
        return REDSTONE_ORE;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= this.getToolTier()) {
            if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
                return new Item[]{this.toItem()};
            }

            int count = ThreadLocalRandom.current().nextInt(2) + 4;

            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                count += ThreadLocalRandom.current().nextInt(fortune.getLevel() + 1);
            }

            return new Item[]{
                    Item.get(Item.REDSTONE_DUST, 0, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.REDSTONE_DUST;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) {
            this.getLevel().setBlock(this, Block.get(GLOWING_REDSTONE_ORE), false, false);
            this.getLevel().scheduleUpdate(this, 600);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public int getDropExp() {
        return Utils.rand(1, 5);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}
