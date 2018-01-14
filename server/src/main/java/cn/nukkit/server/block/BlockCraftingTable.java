package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.inventory.BigCraftingGrid;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2015/12/5 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockCraftingTable extends BlockSolid {
    public BlockCraftingTable() {
        this(0);
    }

    public BlockCraftingTable(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crafting Table";
    }

    @Override
    public int getId() {
        return WORKBENCH;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.setCraftingGrid(new BigCraftingGrid(player));
            player.craftingType = Player.CRAFTING_BIG;
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}
