package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemovePlayerPacket;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Utils;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Human extends Creature implements InventoryHolder {

    public static final int DATA_PLAYER_FLAG_SLEEP = 1;
    public static final int DATA_PLAYER_FLAG_DEAD = 2;

    public static final int DATA_PLAYER_FLAGS = 16;
    public static final int DATA_PLAYER_BED_POSITION = 17;

    protected PlayerInventory inventory;

    protected UUID uuid;
    protected UUID rawUUID;

    public float width = 0.6f;
    public float length = 0.6f;
    public float height = 0.6f;
    public float eyeHeight = 1.62f;

    protected byte[] skin;
    protected boolean isSlim = false;

    @Override
    public int getNetworkId() {
        return -1;
    }

    public Human(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public byte[] getSkinData() {
        return skin;
    }

    public boolean isSkinSlim() {
        return isSlim;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public UUID getRawUniqueId() {
        return rawUUID;
    }

    public void setSkin(byte[] skinData) {
        this.setSkin(skinData, false);
    }

    public void setSkin(byte[] skinData, boolean isSlim) {
        this.skin = skinData;
        this.isSlim = isSlim;
    }

    @Override
    public PlayerInventory getInventory() {
        return inventory;
    }

    @Override
    protected void initEntity() {
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);

        this.setDataProperty(DATA_PLAYER_BED_POSITION, DATA_TYPE_POS, new int[]{0, 0, 0});

        this.inventory = new PlayerInventory(this);
        if (this instanceof Player) {
            ((Player) this).addWindow(this.inventory, 0);
        }

        if (!(this instanceof Player)) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"));
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                this.setSkin(this.namedTag.getCompound("Skin").getByteArray("Data"), this.namedTag.getCompound("Skin").getBoolean("Slim"));
            }

            this.uuid = Utils.dataToUUID(String.valueOf(this.getId()), Binary.bytesToHexString(this.getSkinData()), this.getNameTag());
        }

        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = (ListTag<CompoundTag>) this.namedTag.getList("Inventory");
            for (CompoundTag item : inventoryList.list) {
                if (item.getShort("Slot") >= 0 && item.getShort("Slot") < 9) {
                    this.inventory.setHotbarSlotIndex(item.getShort("Slot"), item.contains("TrueSlot") ? item.getShort("TrueSlot") : -1);
                } else if (item.getShort("Slot") >= 100 && item.getShort("Slot") < 104) {
                    this.inventory.setItem(this.inventory.getSize() + item.getShort("Slot") - 100, NBTIO.getItemHelper(item));
                } else {
                    this.inventory.setItem(item.getShort("Slot") - 9, NBTIO.getItemHelper(item));
                }
            }
        }

        super.initEntity();
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public Item[] getDrops() {
        if (this.inventory != null) {
            return this.inventory.getContents().values().stream().toArray(Item[]::new);
        }
        return new Item[0];
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList(new ListTag<CompoundTag>("Inventory"));
        if (this.inventory != null) {
            for (byte slot = 0; slot < 9; ++slot) {
                int hotbarSlot = this.inventory.getHotbarSlotIndex(slot);
                if (hotbarSlot != -1) {
                    Item item = this.inventory.getItem(hotbarSlot);
                    if (item.getId() != 0 && item.getCount() > 0) {
                        ((ListTag<CompoundTag>) this.namedTag.getList("Inventory")).list.add(slot, NBTIO.putItemHelper(item, (int) slot).putByte("TrueSlot", (byte) hotbarSlot));
                        continue;
                    }
                }
                ((ListTag<CompoundTag>) this.namedTag.getList("Inventory")).list.add(slot, new CompoundTag()
                                .putByte("Count", (byte) 0)
                                .putShort("Damage", 0)
                                .putByte("Slot", slot)
                                .putByte("TrueSlot", (byte) -1)
                                .putShort("id", 0)
                );
            }

            int slotCount = Player.SURVIVAL_SLOTS + 9;
            for (byte slot = 9; slot < slotCount; ++slot) {
                Item item = this.inventory.getItem(slot - 9);
                ((ListTag<CompoundTag>) this.namedTag.getList("Inventory")).list.add(slot, NBTIO.putItemHelper(item, (int) slot));
            }

            for (byte slot = 100; slot < 104; ++slot) {
                Item item = this.inventory.getItem(this.inventory.getSize() + slot - 100);
                if (item != null && item.getId() != Item.AIR) {
                    ((ListTag<CompoundTag>) this.namedTag.getList("Inventory")).list.add(slot, NBTIO.putItemHelper(item, (int) slot));
                }
            }
        }

        if (this.getSkinData().length > 0) {
            this.namedTag.putCompound("Skin", new CompoundTag()
                            .putByteArray("Data", this.getSkinData())
                            .putBoolean("Slim", this.isSkinSlim())
            );
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (!this.equals(player) && !this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);

            if (this.skin.length < 64 * 32 * 4) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (!(this instanceof Player)) {
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.isSlim, this.skin, new Player[]{player});
            }

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.eid = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.getInventory().getItemInHand();
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.inventory.sendArmorContents(player);

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {

            RemovePlayerPacket pk = new RemovePlayerPacket();
            pk.eid = this.getId();
            pk.uuid = this.getUniqueId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (!(this instanceof Player) || ((Player) this).loggedIn) {
                for (Player viewer : this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory);
                }
            }

            super.close();
        }
    }

}
