package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.ListTag;

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
        }

        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = (ListTag<CompoundTag>) this.namedTag.getList("Inventory");
            for (CompoundTag item : inventoryList.list) {
                if (item.getShort("Slot") >= 0 && item.getShort("Slot") < 9) {
                    this.inventory.setHotbarSlotIndex(item.getShort("Slot"), item.contains("TrueSlot") ? item.getShort("TrueSlot") : -1);
                } else if (item.getShort("Slot") >= 100 && item.getShort("Slot") < 104) {
                    this.inventory.setItem(this.inventory.getSize() + item.getShort("Slot") - 100, cn.nukkit.item.Item.get(item.getShort("id"), (int) item.getShort("Damage"), item.getByte("Count")));
                } else {
                    this.inventory.setItem(item.getShort("Slot") - 9, cn.nukkit.item.Item.get(item.getShort("id"), (int) item.getShort("Damage"), item.getByte("Count")));
                }
            }
        }

        super.initEntity();
    }
    //todo alot alot alot !!! :\
}
