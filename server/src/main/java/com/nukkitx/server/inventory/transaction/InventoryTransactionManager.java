package com.nukkitx.server.inventory.transaction;

import com.nukkitx.api.item.ItemStack;
import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.protocol.bedrock.data.InventorySource;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

@RequiredArgsConstructor
public class InventoryTransactionManager {
    private final PlayerSession player;
    private final Deque<InventoryAction> expectedActions = new ArrayDeque<>();
    @Getter
    private InventoryTransaction currentTransaction = null;

    public void createServerSideAction(ItemStack fromItem, ItemStack toItem) {
        InventoryAction action = new InventoryAction(
                InventorySource.fromCreativeInventory(),
                0,
                ItemData.AIR,
                ItemUtils.toNetwork(fromItem)
        );
        this.addAction(action);

        action = new InventoryAction(
                InventorySource.fromCreativeInventory(),
                1,
                ItemUtils.toNetwork(toItem),
                ItemData.AIR
        );
        this.addAction(action);
    }

    public synchronized int getSourceCount() {
        return currentTransaction == null ? 0 : currentTransaction.getSourceCount();
    }

    public synchronized void addAction(InventoryAction action) {
        if (!checkActionExpected(action)) {
            if (!action.getFromItem().equals(action.getToItem())) {
                InventoryTransaction transaction = getOrCreateTransaction();
                transaction.addAction(action);
                if (transaction.verifyBalance()) {
                    player.getDispatcher().sendInventoryTransaction(transaction);
                    currentTransaction = null;
                }
            }
        }
    }

    public synchronized boolean checkActionExpected(InventoryAction action) {
        return expectedActions.remove(action);
    }

    public synchronized void addExpectedAction(InventoryAction action) {
        if (!expectedActions.contains(action)) {
            expectedActions.add(action);
        }
    }

    private synchronized InventoryTransaction getOrCreateTransaction() {
        if (currentTransaction == null) {
            currentTransaction = new InventoryTransaction.SimpleInventoryTransaction(InventoryTransactionType.NORMAL, Collections.emptyList());
        }
        return currentTransaction;
    }

    public synchronized void reset() {
        currentTransaction = null;
    }

    public synchronized void resetExpectedActions() {
        expectedActions.clear();
    }
}
