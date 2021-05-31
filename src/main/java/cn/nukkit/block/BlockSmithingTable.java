package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
public class BlockSmithingTable extends BlockSolid {

    @PowerNukkitOnly
    public BlockSmithingTable() {
    }

    @Override
    public int getId() {
        return SMITHING_TABLE;
    }

    @Override
    public String getName() {
        return "Smithing Table";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (player == null) {
            return false;
        }
        
        player.addWindow(new SmithingInventory(player.getUIInventory(), this), Player.SMITHING_WINDOW_ID);
        return true;
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
}
