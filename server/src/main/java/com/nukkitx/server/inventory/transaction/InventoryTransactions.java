package com.nukkitx.server.inventory.transaction;

import com.google.common.base.Preconditions;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.protocol.bedrock.data.InventorySource;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.server.inventory.transaction.action.function.*;
import lombok.experimental.UtilityClass;

import static com.nukkitx.protocol.bedrock.data.ContainerId.*;

@UtilityClass
public class InventoryTransactions {

    public static InventoryTransaction fromPacket(InventoryTransactionPacket packet) {
        Preconditions.checkNotNull(packet, "packet");

        switch (packet.getTransactionType()) {
            case NORMAL:
                return new InventoryTransaction.SimpleInventoryTransaction(InventoryTransactionType.NORMAL, packet.getActions());
            case INVENTORY_MISMATCH:
                return new InventoryTransaction.SimpleInventoryTransaction(InventoryTransactionType.MISMATCH, packet.getActions());
            case ITEM_USE:
                ItemUseTransaction.Action action = ItemUseTransaction.Action.values()[packet.getActionType()];
                BlockFace face = BlockFace.getFace(packet.getFace());
                return new ItemUseTransaction(packet.getActions(), action, packet.getBlockPosition(), face,
                        packet.getHotbarSlot(), packet.getItemInHand(), packet.getPlayerPosition(), packet.getClickPosition());
            case ITEM_USE_ON_ENTITY:
            case ITEM_RELEASE:
            default:
                throw new IllegalArgumentException("Unknown transaction type " + packet.getTransactionType());
        }
    }

    public static InventoryActionFunction getFunction(InventorySource source) {
        switch (source.getType()) {
            case WORLD_INTERACTION:
                return WorldInteractionInventoryActionFunction.INSTANCE;
            case CREATIVE:
                return CreativeInventoryActionFunction.INSTANCE;
            case UNTRACKED_INTERACTION_UI:
                return UntrackedInteractionUIInventoryActionFunction.INSTANCE;
            case NON_IMPLEMENTED_TODO:
                return NonImplementedTodoInventoryActionFunction.INSTANCE;
            case CONTAINER:
                switch (source.getContainerId()) {
                    case OFFHAND:
                        break;
                    case ARMOR:
                        break;
                    case CREATIVE:
                        break;
                    case CURSOR:
                        break;
                    default:
                        break;
                }
            default:
                return UnknownSourceInventoryActionFunction.INSTANCE;
        }
    }

    public static InventoryTransactionPacket toPacket(InventoryTransaction transaction) {
        InventoryTransactionPacket packet = new InventoryTransactionPacket();
        packet.setTransactionType(transaction.getType().toNetwork());

        switch (transaction.getType()) {

        }
        return null;
    }
}
