package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.player.CraftingTableOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/5 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockCraftingTable extends BlockSolid {

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
            CraftingTableOpenEvent ev = new CraftingTableOpenEvent(player, this);
            player.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                if (player.craftingType == Player.CRAFTING_BIG) {
                    player.getServer().getLogger().debug(player.getName() + " tried to activate crafting table but craftingType is already CRAFTING_BIG");
                    return true;
                }
                player.craftingType = Player.CRAFTING_BIG;
                player.setCraftingGrid(player.getUIInventory().getBigCraftingGrid());

                ContainerOpenPacket pk = new ContainerOpenPacket();
                pk.windowId = -1;
                pk.type = 1;
                pk.x = (int) x;
                pk.y = (int) y;
                pk.z = (int) z;
                pk.entityId = player.getId();
                player.dataPacket(pk);
            }
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
