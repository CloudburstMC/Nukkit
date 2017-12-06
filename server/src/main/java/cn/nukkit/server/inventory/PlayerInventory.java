package cn.nukkit.server.inventory;

import cn.nukkit.api.event.entity.EntityArmorChangeEvent;
import cn.nukkit.api.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.api.event.player.PlayerItemHeldEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.block.BlockAir;
import cn.nukkit.server.entity.EntityHuman;
import cn.nukkit.server.entity.EntityHumanType;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.network.protocol.InventoryContentPacket;
import cn.nukkit.server.network.protocol.InventorySlotPacket;
import cn.nukkit.server.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.server.network.protocol.MobEquipmentPacket;
import cn.nukkit.server.network.protocol.types.ContainerIds;

import java.util.Collection;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInventory extends BaseInventory {

    protected int itemInHandIndex = 0;
    private int[] hotbar;

    public PlayerInventory(EntityHumanType player) {
        super(player, InventoryType.PLAYER);
        this.hotbar = new int[this.getHotbarSize()];

        for (int i = 0; i < this.hotbar.length; i++) {
            this.hotbar[i] = i;
        }

    }

    @Override
    public int getSize() {
        return super.getSize() - 4;
    }

    @Override
    public void setSize(int size) {
        super.setSize(size + 4);
        this.sendContents(this.getViewers());
    }

    /**
     * Called when a client equips a hotbar inventorySlot. This method should not be used by plugins.
     * This method will call PlayerItemHeldEvent.
     *
     * @param slot hotbar slot Number of the hotbar slot to equip.
     * @return boolean if the equipment change was successful, false if not.
     */
    public boolean equipItem(int slot) {
        if (!isHotbarSlot(slot)) {
            this.sendContents((Player) this.getHolder());
            return false;
        }

        if (this.getHolder() instanceof Player) {
            PlayerItemHeldEvent ev = new PlayerItemHeldEvent((Player) this.getHolder(), this.getItem(slot), slot);
            this.getHolder().getLevel().getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                this.sendContents(this.getViewers());
                return false;
            }
        }

        this.setHeldItemIndex(slot, false);
        return true;
    }

    private boolean isHotbarSlot(int slot) {
        return slot >= 0 && slot <= this.getHotbarSize();
    }

    @Deprecated
    public int getHotbarSlotIndex(int index) {
        return index;
    }

    @Deprecated
    public void setHotbarSlotIndex(int index, int slot) {

    }

    public int getHeldItemIndex() {
        return this.itemInHandIndex;
    }

    public void setHeldItemIndex(int index) {
        setHeldItemIndex(index, true);
    }

    public void setHeldItemIndex(int index, boolean send) {
        if (index >= 0 && index < this.getHotbarSize()) {
            this.itemInHandIndex = index;

            if (this.getHolder() instanceof Player && send) {
                this.sendHeldItem((Player) this.getHolder());
            }

            this.sendHeldItem(this.getHolder().getViewers().values());
        }
    }

    public Item getItemInHand() {
        Item item = this.getItem(this.getHeldItemIndex());
        if (item != null) {
            return item;
        } else {
            return new ItemBlock(new BlockAir(), 0, 0);
        }
    }

    public boolean setItemInHand(Item item) {
        return this.setItem(this.getHeldItemIndex(), item);
    }

    @Deprecated
    public int getHeldItemSlot() {
        return this.itemInHandIndex;
    }

    public void setHeldItemSlot(int slot) {
        if (!isHotbarSlot(slot)) {
            return;
        }

        this.itemInHandIndex = slot;

        if (this.getHolder() instanceof Player) {
            this.sendHeldItem((Player) this.getHolder());
        }

        this.sendHeldItem(this.getViewers());
    }

    public void sendHeldItem(Player... players) {
        Item item = this.getItemInHand();

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.item = item;
        pk.inventorySlot = pk.hotbarSlot = this.getHeldItemIndex();

        for (Player player : players) {
            pk.eid = this.getHolder().getId();
            if (player.equals(this.getHolder())) {
                pk.eid = player.getId();
                this.sendSlot(this.getHeldItemIndex(), player);
            }

            player.dataPacket(pk);
        }
    }

    public void sendHeldItem(Collection<Player> players) {
        this.sendHeldItem(players.stream().toArray(Player[]::new));
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        EntityHuman holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        if (index >= this.getSize()) {
            this.sendArmorSlot(index, this.getViewers());
            this.sendArmorSlot(index, this.getHolder().getViewers().values());
        } else {
            super.onSlotChange(index, before, send);
        }
    }

    public int getHotbarSize() {
        return 9;
    }

    public Item getArmorItem(int index) {
        return this.getItem(this.getSize() + index);
    }

    public boolean setArmorItem(int index, Item item) {
        return this.setArmorItem(index, item, false);
    }

    public boolean setArmorItem(int index, Item item, boolean ignoreArmorEvents) {
        return this.setItem(this.getSize() + index, item, ignoreArmorEvents);
    }

    public Item getHelmet() {
        return this.getItem(this.getSize());
    }

    public Item getChestplate() {
        return this.getItem(this.getSize() + 1);
    }

    public Item getLeggings() {
        return this.getItem(this.getSize() + 2);
    }

    public Item getBoots() {
        return this.getItem(this.getSize() + 3);
    }

    public boolean setHelmet(Item helmet) {
        return this.setItem(this.getSize(), helmet);
    }

    public boolean setChestplate(Item chestplate) {
        return this.setItem(this.getSize() + 1, chestplate);
    }

    public boolean setLeggings(Item leggings) {
        return this.setItem(this.getSize() + 2, leggings);
    }

    public boolean setBoots(Item boots) {
        return this.setItem(this.getSize() + 3, boots);
    }

    @Override
    public boolean setItem(int index, Item item) {
        return setItem(index, item, true, false);
    }

    private boolean setItem(int index, Item item, boolean send, boolean ignoreArmorEvents) {
        if (index < 0 || index >= this.size) {
            return false;
        } else if (item.getId() == 0 || item.getCount() <= 0) {
            return this.clear(index);
        }

        //Armor change
        if (!ignoreArmorEvents && index >= this.getSize()) {
            EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), this.getItem(index), item, index);
            NukkitServer.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() && this.getHolder() != null) {
                this.sendArmorSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        } else {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), this.getItem(index), item, index);
            NukkitServer.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.sendSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        }
        Item old = this.getItem(index);
        this.slots.put(index, item.clone());
        this.onSlotChange(index, old, send);
        return true;
    }

    @Override
    public boolean clear(int index, boolean send) {
        if (this.slots.containsKey(index)) {
            Item item = new ItemBlock(new BlockAir(), null, 0);
            Item old = this.slots.get(index);
            if (index >= this.getSize() && index < this.size) {
                EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), old, item, index);
                NukkitServer.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    if (index >= this.size) {
                        this.sendArmorSlot(index, this.getViewers());
                    } else {
                        this.sendSlot(index, this.getViewers());
                    }
                    return false;
                }
                item = ev.getNewItem();
            } else {
                EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), old, item, index);
                NukkitServer.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    if (index >= this.size) {
                        this.sendArmorSlot(index, this.getViewers());
                    } else {
                        this.sendSlot(index, this.getViewers());
                    }
                    return false;
                }
                item = ev.getNewItem();
            }

            if (item.getId() != Item.AIR) {
                this.slots.put(index, item.clone());
            } else {
                this.slots.remove(index);
            }

            this.onSlotChange(index, old, send);
        }

        return true;
    }

    public Item[] getArmorContents() {
        Item[] armor = new Item[4];
        for (int i = 0; i < 4; i++) {
            armor[i] = this.getItem(this.getSize() + i);
        }

        return armor;
    }

    @Override
    public void clearAll() {
        int limit = this.getSize() + 4;
        for (int index = 0; index < limit; ++index) {
            this.clear(index);
        }
    }

    public void sendArmorContents(Player player) {
        this.sendArmorContents(new Player[]{player});
    }

    public void sendArmorContents(Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.slots = armor;
        pk.encode();
        pk.isEncoded = true;

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.inventoryId = InventoryContentPacket.SPECIAL_ARMOR;
                pk2.slots = armor;
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    public void setArmorContents(Item[] items) {
        if (items.length < 4) {
            Item[] newItems = new Item[4];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }

        for (int i = 0; i < 4; ++i) {
            if (items[i] == null) {
                items[i] = new ItemBlock(new BlockAir(), null, 0);
            }

            if (items[i].getId() == Item.AIR) {
                this.clear(this.getSize() + i);
            } else {
                this.setItem(this.getSize() + i, items[i]);
            }
        }
    }

    public void sendArmorContents(Collection<Player> players) {
        this.sendArmorContents(players.stream().toArray(Player[]::new));
    }

    public void sendArmorSlot(int index, Player player) {
        this.sendArmorSlot(index, new Player[]{player});
    }

    public void sendArmorSlot(int index, Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket pk = new MobArmorEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.slots = armor;
        pk.encode();
        pk.isEncoded = true;

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                InventorySlotPacket pk2 = new InventorySlotPacket();
                pk2.inventoryId = InventoryContentPacket.SPECIAL_ARMOR;
                pk2.slot = index - this.getSize();
                pk2.item = this.getItem(index);
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    public void sendArmorSlot(int index, Collection<Player> players) {
        this.sendArmorSlot(index, players.stream().toArray(Player[]::new));
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.stream().toArray(Player[]::new));
    }

    @Override
    public void sendContents(Player[] players) {
        InventoryContentPacket pk = new InventoryContentPacket();
        pk.slots = new Item[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            pk.slots[i] = this.getItem(i);
        }

        /*//Because PE is stupid and shows 9 less slots than you send it, give it 9 dummy slots so it shows all the REAL slots.
        for(int i = this.getSize(); i < this.getSize() + this.getHotbarSize(); ++i){
            pk.slots[i] = new ItemBlock(new BlockAir());
        }
            pk.slots[i] = new ItemBlock(new BlockAir());
        }*/

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1 || !player.spawned) {
                this.close(player);
                continue;
            }
            pk.inventoryId = id;
            player.dataPacket(pk.clone());

        }
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.stream().toArray(Player[]::new));
    }

    @Override
    public void sendSlot(int index, Player... players) {
        InventorySlotPacket pk = new InventorySlotPacket();
        pk.slot = index;
        pk.item = this.getItem(index).clone();

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                pk.inventoryId = ContainerIds.INVENTORY;
                player.dataPacket(pk);
            } else {
                int id = player.getWindowId(this);
                if (id == -1) {
                    this.close(player);
                    continue;
                }
                pk.inventoryId = id;
                player.dataPacket(pk.clone());
            }
        }
    }

    public void sendCreativeContents() {
        if (!(this.getHolder() instanceof Player)) {
            return;
        }
        Player p = (Player) this.getHolder();

        InventoryContentPacket pk = new InventoryContentPacket();
        pk.inventoryId = ContainerIds.CREATIVE;

        if (!p.isSpectator()) { //fill it for all gamemodes except spectator
            pk.slots = Item.getCreativeItems().stream().toArray(Item[]::new);
        }

        p.dataPacket(pk);
    }

    @Override
    public EntityHuman getHolder() {
        return (EntityHuman) super.getHolder();
    }
}
