package com.nukkitx.server.inventory.transaction.action.function;

import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.Inventories;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UntrackedInteractionUIInventoryActionFunction implements InventoryActionFunction {
    public static final UntrackedInteractionUIInventoryActionFunction INSTANCE = new UntrackedInteractionUIInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, PlayerSession player, boolean ignoreChecks) {
        if (action.getSlot() < Inventories.UNTRACKED_INTERACTION_SLOT_COUNT) {
            return InventoryTransactionResult.SUCCESS;
        }
        return InventoryTransactionResult.FAILED_VERIFYING;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, PlayerSession player) {
        if (player.setUntrackedInterationItem(action.getSlot(), ItemUtils.fromNetwork(action.getToItem()))) {
            return InventoryTransactionResult.SUCCESS;
        }
        return InventoryTransactionResult.FAILED_EXECUTING;
    }
}
