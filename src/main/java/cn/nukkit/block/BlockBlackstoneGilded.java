package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockBlackstoneGilded extends BlockSolid {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockBlackstoneGilded() {
        // Does nothing
    }

    @Override
    public int getId() {
        return GILDED_BLACKSTONE;
    }

    @Override
    public String getName() {
        return "Gilded Blackstone";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return new Item[0];
        }
        
        int dropOdds;
        int fortune = 0;
        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }
        
        switch (fortune) {
            case 0:
                dropOdds = 10;
                break;
            case 1:
                dropOdds = 7;
                break;
            case 2:
                dropOdds = 4;
                break;
            default:
                dropOdds = 1;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (dropOdds > 1 && random.nextInt(dropOdds) != 0) {
            return new Item[] { toItem() };
        }

        return new Item[] { Item.get(ItemID.GOLD_NUGGET, 0, random.nextInt(2, 6)) };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 6;
    }
}
