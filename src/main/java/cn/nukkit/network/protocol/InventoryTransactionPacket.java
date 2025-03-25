package cn.nukkit.network.protocol;

import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.TransactionData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import lombok.ToString;

@ToString
public class InventoryTransactionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INVENTORY_TRANSACTION_PACKET;

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MISMATCH = 1;
    public static final int TYPE_USE_ITEM = 2;
    public static final int TYPE_USE_ITEM_ON_ENTITY = 3;
    public static final int TYPE_RELEASE_ITEM = 4;

    public static final int USE_ITEM_ACTION_CLICK_BLOCK = 0;
    public static final int USE_ITEM_ACTION_CLICK_AIR = 1;
    public static final int USE_ITEM_ACTION_BREAK_BLOCK = 2;

    public static final int RELEASE_ITEM_ACTION_RELEASE = 0; //bow shoot
    public static final int RELEASE_ITEM_ACTION_CONSUME = 1; //eat food, drink potion

    public static final int USE_ITEM_ON_ENTITY_ACTION_INTERACT = 0;
    public static final int USE_ITEM_ON_ENTITY_ACTION_ATTACK = 1;


    public static final int ACTION_MAGIC_SLOT_DROP_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_PICKUP_ITEM = 1;

    public static final int ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM = 1;

    public int transactionType;
    public NetworkInventoryAction[] actions;
    public TransactionData transactionData;
    public boolean hasNetworkIds = false;
    public int legacyRequestId;

    /**
     * NOTE: THESE FIELDS DO NOT EXIST IN THE PROTOCOL, it's merely used for convenience for us to easily
     * determine whether we're doing a crafting or enchanting transaction.
     */
    public boolean isCraftingPart = false;
    public boolean isEnchantingPart = false;
    public boolean isRepairItemPart = false;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    @Override
    public void decode() {
        this.legacyRequestId = this.getVarInt();
        if (legacyRequestId < -1 && (legacyRequestId & 1) == 0) {
            int length = (int) this.getUnsignedVarInt();
            if (length > 4096) {
                throw new RuntimeException("Too many inventory transactions in one packet");
            }

            for (int i = 0; i < length; i++) {
                this.getByte();
                int bufLen = (int) this.getUnsignedVarInt();
                this.get(bufLen);
            }
        }

        this.transactionType = (int) this.getUnsignedVarInt();

        this.actions = new NetworkInventoryAction[Math.min((int) this.getUnsignedVarInt(), 4096)];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new NetworkInventoryAction().read(this);
        }

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                //Regular ComplexInventoryTransaction doesn't read any extra data
                break;
            case TYPE_USE_ITEM:
                UseItemData itemData = new UseItemData();

                itemData.actionType = (int) this.getUnsignedVarInt();
                itemData.triggerType = (int) this.getUnsignedVarInt();
                itemData.blockPos = this.getBlockVector3();
                itemData.face = this.getBlockFace();
                itemData.hotbarSlot = this.getVarInt();
                itemData.itemInHand = this.getSlot();
                itemData.playerPos = this.getVector3fAsVector3();
                itemData.clickPos = this.getVector3f();
                itemData.blockRuntimeId = (int) this.getUnsignedVarInt();
                itemData.clientInteractPrediction = (int) this.getUnsignedVarInt();

                this.transactionData = itemData;
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = new UseItemOnEntityData();

                useItemOnEntityData.entityRuntimeId = this.getEntityRuntimeId();
                useItemOnEntityData.actionType = (int) this.getUnsignedVarInt();
                useItemOnEntityData.hotbarSlot = this.getVarInt();
                useItemOnEntityData.itemInHand = this.getSlot();
                useItemOnEntityData.playerPos = this.getVector3fAsVector3();
                useItemOnEntityData.clickPos = this.getVector3fAsVector3();

                this.transactionData = useItemOnEntityData;
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = new ReleaseItemData();

                releaseItemData.actionType = (int) getUnsignedVarInt();
                releaseItemData.hotbarSlot = getVarInt();
                releaseItemData.itemInHand = this.getSlot();
                releaseItemData.headRot = this.getVector3fAsVector3();

                this.transactionData = releaseItemData;
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }

    private Vector3 getVector3fAsVector3() {
        return new Vector3(this.getLFloat(), this.getLFloat(), this.getLFloat());
    }
}
