package com.nukkitx.server.inventory.transaction;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.InventoryAction;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ItemUseOnEntityTransaction extends InventoryTransaction {
    private final long runtimeEntityId;
    private final Action action;
    private final Vector3f clickPosition;

    ItemUseOnEntityTransaction(Collection<InventoryAction> actions, long runtimeEntityId, Action action, Vector3f clickPosition) {
        super(InventoryTransactionType.ITEM_USE_ON_ENTITY, actions);
        this.runtimeEntityId = runtimeEntityId;
        this.action = action;
        this.clickPosition = clickPosition;
    }

    @Override
    public String toString() {
        return "ItemUseOnEntityTransaction" + super.toString() +
                ", runtimeEntityId=" + runtimeEntityId +
                ", action=" + action +
                ", clickPosition=" + clickPosition +
                ')';
    }

    public enum Action {
        INTERACT,
        ATTACK,
        ITEM_INTERACT
    }
}
