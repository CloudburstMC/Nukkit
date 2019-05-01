package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

public interface InventoryActionFunction {

    InventoryTransactionResult verify(InventoryAction action, PlayerSession player, boolean ignoreChecks);

    InventoryTransactionResult execute(InventoryAction action, PlayerSession player);
}
