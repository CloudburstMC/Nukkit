package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NonImplementedTodoInventoryActionFunction implements InventoryActionFunction {
    public static final NonImplementedTodoInventoryActionFunction INSTANCE = new NonImplementedTodoInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, PlayerSession player, boolean ignoreChecks) {
        return InventoryTransactionResult.SUCCESS;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, PlayerSession player) {
        return InventoryTransactionResult.SUCCESS;
    }
}
