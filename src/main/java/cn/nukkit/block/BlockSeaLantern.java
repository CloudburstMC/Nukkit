package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
public class BlockSeaLantern extends BlockTransparent {

    @PowerNukkitOnly
    public BlockSeaLantern() {
        // Does Nothing
    }

    @Override
    public String getName() {
        return "Sea Lantern";
    }

    @Override
    public int getId() {
        return SEA_LANTERN;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item) {
        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortuneLevel = fortune != null? fortune.getLevel() : 0;
        // it drops 2–3 prismarine crystals
        // Each level of Fortune increases the maximum number of prismarine crystals dropped. 
        // The amount is capped at 5, so Fortune III simply increases the chance of getting 5 crystals.
        int count = Math.min(5, 2 + ThreadLocalRandom.current().nextInt(1 + fortuneLevel));
        
        return new Item[]{ Item.get(ItemID.PRISMARINE_CRYSTALS, 0, count) };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
