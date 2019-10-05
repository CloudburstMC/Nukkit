package cn.nukkit.network.protocol;

import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.TransactionData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class InventoryTransactionPacket extends DataPacket {

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

    /**
     * NOTE: THIS FIELD DOES NOT EXIST IN THE PROTOCOL, it's merely used for convenience for PocketMine-MP to easily
     * determine whether we're doing a crafting transaction.
     */
    public boolean isCraftingPart = false;

    @Override
    public short pid() {
        return ProtocolInfo.INVENTORY_TRANSACTION_PACKET;
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, this.transactionType);

        Binary.writeUnsignedVarInt(buffer, this.actions.length);
        for (NetworkInventoryAction action : this.actions) {
            action.write(buffer, this);
        }

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                break;
            case TYPE_USE_ITEM:
                UseItemData useItemData = (UseItemData) this.transactionData;

                Binary.writeUnsignedVarInt(buffer, useItemData.actionType);
                Binary.writeBlockVector3(buffer, useItemData.blockPos);
                Binary.writeBlockFace(buffer, useItemData.face);
                Binary.writeVarInt(buffer, useItemData.hotbarSlot);
                Binary.writeItem(buffer, useItemData.itemInHand);
                Binary.writeVector3f(buffer, useItemData.playerPos.asVector3f());
                Binary.writeVector3f(buffer, useItemData.clickPos);
                Binary.writeUnsignedVarInt(buffer, useItemData.blockRuntimeId);
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) this.transactionData;

                Binary.writeEntityRuntimeId(buffer, useItemOnEntityData.entityRuntimeId);
                Binary.writeUnsignedVarInt(buffer, useItemOnEntityData.actionType);
                Binary.writeVarInt(buffer, useItemOnEntityData.hotbarSlot);
                Binary.writeItem(buffer, useItemOnEntityData.itemInHand);
                Binary.writeVector3f(buffer, useItemOnEntityData.playerPos.asVector3f());
                Binary.writeVector3f(buffer, useItemOnEntityData.clickPos.asVector3f());
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = (ReleaseItemData) this.transactionData;

                Binary.writeUnsignedVarInt(buffer, releaseItemData.actionType);
                Binary.writeVarInt(buffer, releaseItemData.hotbarSlot);
                Binary.writeItem(buffer, releaseItemData.itemInHand);
                Binary.writeVector3f(buffer, releaseItemData.headRot.asVector3f());
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.transactionType = (int) Binary.readUnsignedVarInt(buffer);

        this.actions = new NetworkInventoryAction[(int) Binary.readUnsignedVarInt(buffer)];
        for (int i = 0; i < this.actions.length; i++) {
            this.actions[i] = new NetworkInventoryAction().read(buffer, this);
        }

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                //Regular ComplexInventoryTransaction doesn't read any extra data
                break;
            case TYPE_USE_ITEM:
                UseItemData itemData = new UseItemData();

                itemData.actionType = (int) Binary.readUnsignedVarInt(buffer);
                itemData.blockPos = Binary.readBlockVector3(buffer);
                itemData.face = Binary.readBlockFace(buffer);
                itemData.hotbarSlot = Binary.readVarInt(buffer);
                itemData.itemInHand = Binary.readItem(buffer);
                itemData.playerPos = Binary.readVector3f(buffer).asVector3();
                itemData.clickPos = Binary.readVector3f(buffer);
                itemData.blockRuntimeId = (int) Binary.readUnsignedVarInt(buffer);

                this.transactionData = itemData;
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = new UseItemOnEntityData();

                useItemOnEntityData.entityRuntimeId = Binary.readEntityRuntimeId(buffer);
                useItemOnEntityData.actionType = (int) Binary.readUnsignedVarInt(buffer);
                useItemOnEntityData.hotbarSlot = Binary.readVarInt(buffer);
                useItemOnEntityData.itemInHand = Binary.readItem(buffer);
                useItemOnEntityData.playerPos = Binary.readVector3f(buffer).asVector3();
                useItemOnEntityData.clickPos = Binary.readVector3f(buffer).asVector3();

                this.transactionData = useItemOnEntityData;
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = new ReleaseItemData();

                releaseItemData.actionType = (int) Binary.readUnsignedVarInt(buffer);
                releaseItemData.hotbarSlot = Binary.readVarInt(buffer);
                releaseItemData.itemInHand = Binary.readItem(buffer);
                releaseItemData.headRot = Binary.readVector3f(buffer).asVector3();

                this.transactionData = releaseItemData;
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }
}
