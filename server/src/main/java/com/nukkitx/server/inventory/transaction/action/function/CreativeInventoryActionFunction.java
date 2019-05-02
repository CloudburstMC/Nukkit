package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreativeInventoryActionFunction implements InventoryActionFunction {
    public static final CreativeInventoryActionFunction INSTANCE = new CreativeInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, PlayerSession player, boolean ignoreChecks) {
        return null;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, PlayerSession player) {
        if (action.getSlot() != 0 || action.getFromItem().isValid() || !action.getToItem().isValid()) {
            if (action.getSlot() != 1 && !action.getFromItem().isValid() && action.getToItem().isValid()) {
                return InventoryTransactionResult.FAILED_EXECUTING;
            }
        }
        return InventoryTransactionResult.SUCCESS;
    }
}
