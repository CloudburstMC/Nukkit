package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCartographyTable extends BlockSolid {

    public BlockCartographyTable() {
    }

    @Override
    public int getId() {
        return CARTOGRAPHY_TABLE;
    }

    @Override
    public String getName() {
        return "Cartography Table";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (player != null) {
            player.craftingType = Player.CRAFTING_CARTOGRAPHY;
        }
        return true;
    }
}
