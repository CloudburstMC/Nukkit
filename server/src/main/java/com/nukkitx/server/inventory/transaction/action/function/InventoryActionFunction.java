package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

public interface InventoryActionFunction {

    InventoryTransactionResult verify(InventoryAction action, NukkitPlayerSession player, boolean ignoreChecks);

    InventoryTransactionResult execute(InventoryAction action, NukkitPlayerSession player);
}
