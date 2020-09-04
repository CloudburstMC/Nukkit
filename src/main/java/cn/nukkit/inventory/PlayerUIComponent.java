package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PlayerUIComponent extends BaseInventory {
    protected final PlayerUIInventory playerUI;
    private final int offset;
    private final int size;

    PlayerUIComponent(PlayerUIInventory playerUI, int offset, int size) {
        super(playerUI.holder, InventoryType.UI, Collections.emptyMap(), size);
        this.playerUI = playerUI;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setMaxStackSize(int size) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getTitle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item getItem(int index) {
        return this.playerUI.getItem(index + this.offset);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        return this.playerUI.setItem(index + this.offset, item, send);
    }

    @Override
    public boolean clear(int index, boolean send) {
        return this.playerUI.clear(index + this.offset, send);
    }

    @Override
    public Map<Integer, Item> getContents() {
        Map<Integer, Item> contents = playerUI.getContents();
        contents.keySet().removeIf(slot -> slot < offset || slot > offset + size);
        return contents;
    }


    @Override
    public void sendContents(Player... players) {
        this.playerUI.sendContents(players);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        playerUI.sendSlot(index + this.offset, players);
    }

    @Override
    public Set<Player> getViewers() {
        return playerUI.viewers;
    }

    @Override
    public InventoryType getType() {
        return playerUI.type;
    }

    @Override
    public void onOpen(Player who) {

    }

    @Override
    public boolean open(Player who) {
        return false;
    }

    @Override
    public void close(Player who) {

    }

    @Override
    public void onClose(Player who) {

    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        this.playerUI.onSlotChangeBase(index + this.offset, before, send);
    }
}
