package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author CreeperFace
 * @since 27. 11. 2016
 */
public class BlockButtonStone extends BlockButton {

    public BlockButtonStone() {
        this(0);
    }

    public BlockButtonStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_BUTTON;
    }

    @Override
    public String getName() {
        return "Stone Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return false")
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Requires wooden pickaxe to drop item")
    @Override
    public Item[] getDrops(Item item) {
        return super.getDrops(item);
    }
}
