package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockAncientDebris extends BlockSolid {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockAncientDebris() {
        // Does nothing
    }

    @Override
    public int getId() {
        return ANCIENT_DERBRIS;
    }

    @Override
    public String getName() {
        return "Ancient Derbris";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item != null && item.isPickaxe() && item.getTier() >= ItemTool.TIER_DIAMOND) {
            return new Item[] {
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 30;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
