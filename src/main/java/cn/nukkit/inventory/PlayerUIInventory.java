package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.types.ContainerIds;

import java.util.HashMap;

public class PlayerUIInventory extends BaseInventory {
    private final Player player;

    private final PlayerCursorInventory cursorInventory;
    private final CraftingGrid craftingGrid;
    private final BigCraftingGrid bigCraftingGrid;

    public PlayerUIInventory(Player player) {
        super(player, InventoryType.UI, new HashMap<>(), 51);
        this.player = player;

        this.cursorInventory = new PlayerCursorInventory(this);
        this.craftingGrid = new CraftingGrid(this);
        this.bigCraftingGrid = new BigCraftingGrid(this);
    }

    public PlayerCursorInventory getCursorInventory() {
        return cursorInventory;
    }

    public CraftingGrid getCraftingGrid() {
        return craftingGrid;
    }

    public BigCraftingGrid getBigCraftingGrid() {
        return bigCraftingGrid;
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("UI size is immutable");
    }

    @Override
    public void sendSlot(int index, Player... target) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.slot = index;
        pk.item = this.getItem(index);

        for (Player p : target) {
            if (p == this.getHolder()) {
                pk.inventoryId = ContainerIds.UI;
            } else {
                int id;

                if ((id = p.getWindowId(this)) == ContainerIds.NONE) {
                    this.close(p);
                    continue;
                }
                pk.inventoryId = id;
            }
            p.dataPacket(pk);
        }
    }

    @Override
    public void sendContents(Player... target) {
        //doesn't work here
    }

    @Override
    public int getSize() {
        return 51;
    }

    @Override
    public Player getHolder() {
        return player;
    }
}
