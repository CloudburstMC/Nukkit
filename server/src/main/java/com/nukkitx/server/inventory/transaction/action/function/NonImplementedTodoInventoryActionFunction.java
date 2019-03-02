package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NonImplementedTodoInventoryActionFunction implements InventoryActionFunction {
    public static final NonImplementedTodoInventoryActionFunction INSTANCE = new NonImplementedTodoInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, NukkitPlayerSession player, boolean ignoreChecks) {
        return InventoryTransactionResult.SUCCESS;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, NukkitPlayerSession player) {
        return InventoryTransactionResult.SUCCESS;
    }
}
