package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class PlayerInventory extends BaseInventory {

    protected int itemInHandIndex = 0;
    private int[] hotbar;

    public PlayerInventory(Human player) {
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
            Player player = (Player) this.getHolder();
            PlayerItemHeldEvent ev = new PlayerItemHeldEvent(player, this.getItem(slot), slot);
            this.getHolder().getLevel().getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                this.sendContents(this.getViewers());
                return false;
            }

            if (player.fishing != null) {
                if (!(this.getItem(slot).equals(player.fishing.getRod()))) {
                    player.stopFishing(false);
                }
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

            this.sendHeldItem(this.getHolder().getViewers());
        }
    }

    public Item getItemInHand() {
        Item item = this.getItem(this.getHeldItemIndex());
        if (item != null) {
            return item;
        } else {
            return Item.get(AIR, 0, 0);
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

        MobEquipmentPacket packet = new MobEquipmentPacket();
        packet.setItem(item.toNetwork());
        packet.setInventorySlot(this.getHeldItemIndex());
        packet.setHotbarSlot(this.getHeldItemIndex());

        for (Player player : players) {
            packet.setRuntimeEntityId(this.getHolder().getRuntimeId());
            if (player.equals(this.getHolder())) {
                packet.setRuntimeEntityId(player.getRuntimeId());
                this.sendSlot(this.getHeldItemIndex(), player);
            }

            player.sendPacket(packet);
        }
    }

    public void sendHeldItem(Collection<Player> players) {
        this.sendHeldItem(players.toArray(new Player[0]));
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        Human holder = this.getHolder();
        if (holder instanceof Player && !((Player) holder).spawned) {
            return;
        }

        if (index >= this.getSize()) {
            this.sendArmorSlot(index, this.getViewers());
            this.sendArmorSlot(index, this.getHolder().getViewers());
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
        } else if (item.getId() == AIR || item.getCount() <= 0) {
            return this.clear(index);
        }

        //Armor change
        if (!ignoreArmorEvents && index >= this.getSize()) {
            EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() && this.getHolder() != null) {
                this.sendArmorSlot(index, this.getViewers());
                return false;
            }
            item = ev.getNewItem();
        } else {
            EntityInventoryChangeEvent ev = new EntityInventoryChangeEvent(this.getHolder(), this.getItem(index), item, index);
            Server.getInstance().getPluginManager().callEvent(ev);
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
            Item item = Item.get(AIR, 0, 0);
            Item old = this.slots.get(index);
            if (index >= this.getSize() && index < this.size) {
                EntityArmorChangeEvent ev = new EntityArmorChangeEvent(this.getHolder(), old, item, index);
                Server.getInstance().getPluginManager().callEvent(ev);
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
                Server.getInstance().getPluginManager().callEvent(ev);
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

            if (item.getId() != AIR) {
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

        MobArmorEquipmentPacket packet = new MobArmorEquipmentPacket();
        packet.setRuntimeEntityId(this.getHolder().getRuntimeId());
        packet.setHelmet(armor[0].toNetwork());
        packet.setChestplate(armor[1].toNetwork());
        packet.setLeggings(armor[2].toNetwork());
        packet.setBoots(armor[3].toNetwork());

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                InventoryContentPacket packet2 = new InventoryContentPacket();
                packet2.setContainerId(ContainerIds.SPECIAL_ARMOR);
                packet2.setContents(Item.toNetwork(armor));
                player.sendPacket(packet2);
            } else {
                player.sendPacket(packet);
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
                items[i] = Item.get(AIR, 0, 0);
            }

            if (items[i].getId() == AIR) {
                this.clear(this.getSize() + i);
            } else {
                this.setItem(this.getSize() + i, items[i]);
            }
        }
    }

    public void sendArmorContents(Collection<Player> players) {
        this.sendArmorContents(players.toArray(new Player[0]));
    }

    public void sendArmorSlot(int index, Player player) {
        this.sendArmorSlot(index, new Player[]{player});
    }

    public void sendArmorSlot(int index, Player[] players) {
        Item[] armor = this.getArmorContents();

        MobArmorEquipmentPacket packet = new MobArmorEquipmentPacket();
        packet.setRuntimeEntityId(this.getHolder().getRuntimeId());
        packet.setHelmet(armor[0].toNetwork());
        packet.setChestplate(armor[1].toNetwork());
        packet.setLeggings(armor[2].toNetwork());
        packet.setBoots(armor[3].toNetwork());

        for (Player player : players) {
            if (player.equals(this.getHolder())) {
                InventorySlotPacket packet2 = new InventorySlotPacket();
                packet2.setContainerId(ContainerIds.SPECIAL_ARMOR);
                packet2.setSlot(index - this.getSize());
                packet2.setItem(this.getItem(index).toNetwork());
                player.sendPacket(packet2);
            } else {
                player.sendPacket(packet);
            }
        }
    }

    public void sendArmorSlot(int index, Collection<Player> players) {
        this.sendArmorSlot(index, players.toArray(new Player[0]));
    }

    @Override
    public void sendContents(Player player) {
        this.sendContents(new Player[]{player});
    }

    @Override
    public void sendContents(Collection<Player> players) {
        this.sendContents(players.toArray(new Player[0]));
    }

    @Override
    public void sendContents(Player[] players) {
        ItemData[] itemData = new ItemData[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            itemData[i] = this.getItem(i).toNetwork();
        }

        for (Player player : players) {
            int id = player.getWindowId(this);
            if (id == -1 || !player.spawned) {
                if (this.getHolder() != player) this.close(player);
                continue;
            }

            InventoryContentPacket pk = new InventoryContentPacket();
            pk.setContents(new ItemData[itemData.length]);
            for (int i = 0; i < this.getSize(); ++i) {
                pk.getContents()[i] = itemData[i];
            }
            pk.setContainerId(id);

            player.sendPacket(pk);
        }
    }

    @Override
    public void sendSlot(int index, Player player) {
        this.sendSlot(index, new Player[]{player});
    }

    @Override
    public void sendSlot(int index, Collection<Player> players) {
        this.sendSlot(index, players.toArray(new Player[0]));
    }

    @Override
    public void sendSlot(int index, Player... players) {
        ItemData itemData = this.getItem(index).toNetwork();

        for (Player player : players) {
            InventorySlotPacket packet = new InventorySlotPacket();
            packet.setSlot(index);
            packet.setItem(itemData);

            if (player.equals(this.getHolder())) {
                packet.setContainerId(ContainerIds.INVENTORY);
                player.sendPacket(packet);
            } else {
                int id = player.getWindowId(this);
                if (id == -1) {
                    this.close(player);
                    continue;
                }
                packet.setContainerId(id);
                player.sendPacket(packet);
            }
        }
    }

    public void sendCreativeContents() {
        if (!(this.getHolder() instanceof Player)) {
            return;
        }
        Player p = (Player) this.getHolder();

        InventoryContentPacket pk = new InventoryContentPacket();
        pk.setContainerId(ContainerIds.CREATIVE);

        if (!p.isSpectator()) { //fill it for all gamemodes except spectator
            pk.setContents(Item.toNetwork(Item.getCreativeItems().toArray(new Item[0])));
        }

        p.sendPacket(pk);
    }

    @Override
    public Human getHolder() {
        return (Human) super.getHolder();
    }
}
