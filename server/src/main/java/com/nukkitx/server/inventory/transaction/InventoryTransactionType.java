package com.nukkitx.server.inventory.transaction;

import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InventoryTransactionType {
    NORMAL(InventoryTransactionPacket.Type.NORMAL),
    MISMATCH(InventoryTransactionPacket.Type.INVENTORY_MISMATCH),
    ITEM_USE(InventoryTransactionPacket.Type.ITEM_USE),
    ITEM_USE_ON_ENTITY(InventoryTransactionPacket.Type.ITEM_USE_ON_ENTITY),
    ITEM_RELEASE(InventoryTransactionPacket.Type.ITEM_RELEASE);

    private final InventoryTransactionPacket.Type network;

    public InventoryTransactionPacket.Type toNetwork() {
        return network;
    }
}
