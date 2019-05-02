package com.nukkitx.server.inventory.transaction;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.protocol.bedrock.data.InventorySource;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.server.inventory.transaction.action.function.InventoryActionFunction;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;


@ParametersAreNonnullByDefault
public abstract class InventoryTransaction {
    private final Map<InventorySource, List<InventoryAction>> sources = new HashMap<>();
    private final Deque<InventoryTransactionItemGroup> itemGroups = new ArrayDeque<>();
    @Getter
    private final InventoryTransactionType type;
    @Getter
    private final long creationTime = System.currentTimeMillis();
    private boolean balanceVerified = false;
    private boolean fullyVerified = false;

    InventoryTransaction(InventoryTransactionType type, Collection<InventoryAction> actions) {
        this.type = type;
        actions.forEach(this::addAction);
    }

    public void addAction(@Nonnull InventoryAction action) {
        Preconditions.checkNotNull(action, "action");

        InventorySource source = action.getSource();
        List<InventoryAction> actions = sources.get(source);
        //noinspection Java8MapApi
        if (actions == null) {
            actions = new ArrayList<>();
            sources.put(source, actions);
        }

        fullyVerified = false;

        ListIterator<InventoryAction> iterator = actions.listIterator();

        while (iterator.hasNext()) {
            InventoryAction otherAction = iterator.next();
            if (otherAction.getSlot() == action.getSlot() &&
                    action.getFromItem().equals(otherAction.getToItem(), true, true, true)) {
                iterator.set(new InventoryAction(otherAction.getSource(), otherAction.getSlot(), otherAction.getFromItem(), action.getToItem()));
                this.addActionToContent(action);
                return;
            }
        }
        actions.add(action);
        this.addActionToContent(action);
    }

    private void addActionToContent(InventoryAction action) {
        ItemData fromItem = action.getFromItem();
        ItemData toItem = action.getToItem();

        if (toItem.getCount() < 1) {
            addItemToContent(fromItem, -fromItem.getCount());
        } else {
            if (fromItem.getCount() > 0) {
                addItemToContent(fromItem, -fromItem.getCount());
            }
            addItemToContent(toItem, toItem.getCount());
        }
    }

    private void addItemToContent(ItemData item, int count) {
        for (InventoryTransactionItemGroup itemGroup : itemGroups) {
            if (itemGroup.add(item, count)) {
                return;
            }
        }
        InventoryTransactionItemGroup itemGroup = new InventoryTransactionItemGroup(item, count);
        itemGroups.add(itemGroup);
        balanceVerified = false;
    }

    public int getSourceCount() {
        return sources.size();
    }

    public List<InventoryAction> getSources(@Nonnull InventorySource source) {
        Preconditions.checkNotNull(source, "source");
        List<InventoryAction> actions = this.sources.get(source);

        if (actions == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(actions);
    }

    public boolean verifyBalance() {
        if (!balanceVerified) {
            for (InventoryTransactionItemGroup itemGroup : itemGroups) {
                if (itemGroup.getCount() != 0 || itemGroup.hasOverflow()) {
                    return false;
                }
            }
        }
        return true;
    }

    public InventoryTransactionResult verify(PlayerSession player, boolean ignoreChecks) {
        if (fullyVerified) {
            return InventoryTransactionResult.SUCCESS;
        }
        if (verifyBalance()) {
            for (Map.Entry<InventorySource, List<InventoryAction>> entry : sources.entrySet()) {
                InventoryActionFunction function = InventoryTransactions.getFunction(entry.getKey());

                for (InventoryAction action : entry.getValue()) {
                    InventoryTransactionResult result = function.verify(action, player, ignoreChecks);
                    if (result != InventoryTransactionResult.SUCCESS) {
                        return result;
                    }
                }
            }
        } else {
            return InventoryTransactionResult.FAILED_BALANCING;
        }
        fullyVerified = true;
        return InventoryTransactionResult.SUCCESS;
    }

    public InventoryTransactionResult execute(PlayerSession player, boolean ignoreChecks) {

        for (Map.Entry<InventorySource, List<InventoryAction>> entry : sources.entrySet()) {
            InventoryActionFunction function = InventoryTransactions.getFunction(entry.getKey());

            for (InventoryAction action : entry.getValue()) {
                InventoryTransactionResult result = function.verify(action, player, ignoreChecks);
                if (result != InventoryTransactionResult.SUCCESS) {
                    return result;
                }
                result = function.execute(action, player);
                if (result != InventoryTransactionResult.SUCCESS) {
                    return result;
                }
            }
        }
        return InventoryTransactionResult.SUCCESS;
    }

    public void onTransactionError(PlayerSession player, InventoryTransactionResult result) {
        player.getDispatcher().sendInventory(true);
    }

    public InventoryTransactionResult handle(PlayerSession player, boolean ignoreChecks) {
        InventoryTransactionResult result = verify(player, ignoreChecks);
        if (result == InventoryTransactionResult.SUCCESS) {
            InventoryTransactionManager manager = player.getTransactionManager();
            for (List<InventoryAction> actions : sources.values()) {
                for (InventoryAction action : actions) {
                    manager.addExpectedAction(action);
                }
            }
            result = execute(player, ignoreChecks);
            if (result == InventoryTransactionResult.SUCCESS && type == InventoryTransactionType.MISMATCH) {
                player.getDispatcher().sendInventory(true);
            }
        }
        return result;
    }

    public List<InventoryAction> getActions() {
        List<InventoryAction> actions = new ArrayList<>();
        for (List<InventoryAction> actionList : sources.values()) {
            actions.addAll(actionList);
        }
        return actions;
    }

    static class SimpleInventoryTransaction extends InventoryTransaction {

        SimpleInventoryTransaction(InventoryTransactionType type, Collection<InventoryAction> actions) {
            super(type, actions);
        }
    }
}
