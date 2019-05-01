package com.nukkitx.server.inventory.transaction;


import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.protocol.bedrock.data.ContainerId;
import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.protocol.bedrock.data.InventorySource;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.server.inventory.NukkitPlayerInventory;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
public class ItemReleaseTransaction extends InventoryTransaction {
    private final Action action;
    private final int selectedSlot;
    private final ItemData selectedItem;
    private final Vector3f headPosition;

    ItemReleaseTransaction(Collection<InventoryAction> actions, Action action, int selectedSlot, ItemData selectedItem, Vector3f headPosition) {
        super(InventoryTransactionType.ITEM_RELEASE, actions);
        this.action = action;
        this.selectedSlot = selectedSlot;
        this.selectedItem = selectedItem;
        this.headPosition = headPosition;
    }

    @Override
    public InventoryTransactionResult handle(PlayerSession player, boolean ignoreChecks) {
        InventoryTransactionResult result = InventoryTransactionResult.FAILED_VERIFYING;
        if (player.isAlive()) {
            NukkitPlayerInventory inventory = player.getInventory();
            ItemStack selectedItem = inventory.getSelectedItem();
            ItemStack transactionSelectedItem = ItemUtils.fromNetwork(this.selectedItem);

            if (!selectedItem.equals(transactionSelectedItem, true, true, true) ||
                    inventory.getSelectedSlot() != selectedSlot || !ignoreChecks) {
                return result;
            }

            List<InventoryAction> actions = getSources(InventorySource.fromContainerWindowId(ContainerId.INVENTORY));

        }
        return result;
    }

    public enum Action {
        RELEASE,
        CONSUME
    }
}
