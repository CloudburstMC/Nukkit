package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;

import java.util.HashSet;
import java.util.Set;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class EntityArmorInventory extends BaseInventory {

    private final Entity entity;

    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int SLOT_HEAD = 0;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int SLOT_CHEST = 1;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int SLOT_LEGS = 2;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int SLOT_FEET = 3;

    /**
     * @param entity an Entity which implements {@link InventoryHolder}.
     * @throws ClassCastException if the entity does not implements {@link InventoryHolder}
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EntityArmorInventory(Entity entity) {
        super((InventoryHolder) entity, InventoryType.ENTITY_ARMOR);
        this.entity = entity;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Entity getEntity() {
        return entity;
    }

    @Override
    public InventoryHolder getHolder() {
        return this.holder;
    }

    @Override
    public String getName() {
        return "Entity Armor";
    }

    @Override
    public int getSize() {
        return 4;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getHelmet(){
        return this.getItem(SLOT_HEAD);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getChestplate(){
        return this.getItem(SLOT_CHEST);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getLeggings(){
        return this.getItem(SLOT_LEGS);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getBoots(){
        return this.getItem(SLOT_FEET);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setHelmet(Item item) {
        this.setItem(SLOT_CHEST, item);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setChestplate(Item item) {
        this.setItem(SLOT_CHEST, item);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setLeggings(Item item) {
        this.setItem(SLOT_LEGS, item);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setBoots(Item item) {
        this.setItem(SLOT_FEET, item);
    }

    @Override
    public void sendSlot(int index, Player... players) {
        for (Player player : players) {
            this.sendSlot(index, player);
        }
    }

    @Override
    public void sendSlot(int index, Player player) {
        MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.eid = this.entity.getId();
        mobArmorEquipmentPacket.slots = new Item[]{this.getHelmet(), this.getChestplate(), this.getLeggings(), this.getBoots()};

        if (player == this.holder) {
            InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
            inventorySlotPacket.inventoryId = player.getWindowId(this);
            inventorySlotPacket.slot = index;
            inventorySlotPacket.item = this.getItem(index);
            player.dataPacket(inventorySlotPacket);
        } else {
            player.dataPacket(mobArmorEquipmentPacket);
        }
    }

    @Override
    public void sendContents(Player... players) {
        for (Player player : players) {
            this.sendContents(player);
        }
    }

    @Override
    public void sendContents(Player player) {
        MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.eid = this.entity.getId();
        mobArmorEquipmentPacket.slots = new Item[]{this.getHelmet(), this.getChestplate(), this.getLeggings(), this.getBoots()};

        if (player == this.holder) {
            InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = player.getWindowId(this);
            inventoryContentPacket.slots = new Item[]{this.getHelmet(), this.getChestplate(), this.getLeggings(), this.getBoots()};
            player.dataPacket(inventoryContentPacket);
        } else {
            player.dataPacket(mobArmorEquipmentPacket);
        }
    }

    @Override
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>(this.viewers);
        viewers.addAll(entity.getViewers().values());
        return viewers;
    }
}
