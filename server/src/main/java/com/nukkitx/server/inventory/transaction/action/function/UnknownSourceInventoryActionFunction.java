package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UnknownSourceInventoryActionFunction implements InventoryActionFunction {
    public static final UnknownSourceInventoryActionFunction INSTANCE = new UnknownSourceInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, PlayerSession player, boolean ignoreChecks) {
        return InventoryTransactionResult.FAILED_VERIFYING;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, PlayerSession player) {
        return InventoryTransactionResult.FAILED_EXECUTING;
    }

}
