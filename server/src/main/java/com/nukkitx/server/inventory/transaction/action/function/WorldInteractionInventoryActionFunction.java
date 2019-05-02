package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.protocol.bedrock.data.InventorySource;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldInteractionInventoryActionFunction implements InventoryActionFunction {
    public static final WorldInteractionInventoryActionFunction INSTANCE = new WorldInteractionInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, PlayerSession player, boolean ignoreChecks) {
        InventoryTransactionResult result;
        if (action.getSlot() > 0 || action.getFromItem().isValid() || !action.getToItem().isValid()) {
            if (action.getSlot() != 1 || !action.getFromItem().isValid() || action.getToItem().isValid()) {
                result = InventoryTransactionResult.FAILED_VERIFYING;
            } else if (ignoreChecks) {
                result = InventoryTransactionResult.SUCCESS;
            } else {
                result = InventoryTransactionResult.FAILED_VERIFYING;
            }
        } else {
            result = InventoryTransactionResult.SUCCESS;
        }
        return result;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, PlayerSession player) {
        if (action.getSlot() > 0 || action.getFromItem().isValid() || !action.getToItem().isValid()) {
            if (action.getSlot() == 1 || !action.getFromItem().isValid() || action.getToItem().isValid()) {
                return InventoryTransactionResult.SUCCESS;
            }
        } else {
            player.drop(ItemUtils.fromNetwork(action.getToItem()), action.getSource().getFlag() != InventorySource.Flag.DROP_ITEM);
            return InventoryTransactionResult.SUCCESS;
        }
        return InventoryTransactionResult.FAILED_EXECUTING;
    }
}
