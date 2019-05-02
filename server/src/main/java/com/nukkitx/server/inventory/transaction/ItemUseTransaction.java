package com.nukkitx.server.inventory.transaction;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;
import com.nukkitx.server.block.behavior.BlockBehavior;
import com.nukkitx.server.block.behavior.BlockBehaviors;
import com.nukkitx.server.item.ItemUtils;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
@ParametersAreNonnullByDefault
public class ItemUseTransaction extends InventoryTransaction {
    private Action action;
    private Vector3i blockPosition;
    private BlockFace face;
    private int selectedSlot;
    private ItemData selectedItem;
    private Vector3f playerPosition;
    private Vector3f clickPosition;

    ItemUseTransaction(Collection<InventoryAction> actions, Action action, Vector3i blockPosition, BlockFace face,
                       int selectedSlot, ItemData selectedItem, Vector3f playerPosition, Vector3f clickPosition) {
        super(InventoryTransactionType.ITEM_USE, actions);
        this.action = action;
        this.blockPosition = blockPosition;
        this.face = face;
        this.selectedSlot = selectedSlot;
        this.selectedItem = selectedItem;
        this.playerPosition = playerPosition;
        this.clickPosition = clickPosition;
    }

    @Override
    public void onTransactionError(PlayerSession player, InventoryTransactionResult result) {
        Optional<Block> block = player.getLevel().getBlockIfChunkLoaded(blockPosition.add(face.getOffset()));
        //noinspection OptionalIsPresent
        if (block.isPresent()) {
            resendBlockAroundArea(player, block.get());
        }
        super.onTransactionError(player, result);
    }

    private void resendBlockAroundArea(PlayerSession player, Block block) {
        List<Block> blocks = block.getNeighboringBlocksIfLoaded();
        blocks.add(block);

        for (Block toResend : blocks) {
            UpdateBlockPacket updateBlock = new UpdateBlockPacket();
            updateBlock.setRuntimeId(NukkitLevel.getPaletteManager().getOrCreateRuntimeId(toResend.getBlockState()));
            updateBlock.setDataLayer(0);
            updateBlock.getFlags().addAll(UpdateBlockPacket.FLAG_ALL);
            updateBlock.setBlockPosition(toResend.getBlockPosition());

            player.sendNetworkPacket(updateBlock);
        }
    }

    @Override
    public InventoryTransactionResult handle(PlayerSession player, boolean ignoreChecks) {
        if (!player.isAlive()) {
            return InventoryTransactionResult.NOT_ALIVE;
        }

        int serverSelectedSlot = player.getInventory().getSelectedSlot();
        int clientSelectedSlot = this.selectedSlot;
        // Check if the same slot is selected.
        if (serverSelectedSlot != clientSelectedSlot && !ignoreChecks) {
            return InventoryTransactionResult.HOTBAR_MISMATCH;
        }
        ItemStack serverSelectedItem = player.getInventory().getSelectedItem();
        ItemStack clientSelectedItem = ItemUtils.fromNetwork(this.selectedItem);

        // Check if items selected are the same.
        if (!serverSelectedItem.equals(clientSelectedItem) && !ignoreChecks) {
            return InventoryTransactionResult.HOTBAR_MISMATCH;
        }

        if (action != Action.USE && !ignoreChecks) {
            // Check if player is too far away to interact.
            float distance = player.getPosition().distance(blockPosition.toFloat());
            if (distance > 12.5f) {
                return InventoryTransactionResult.INVALID_POSITION;
            }
        }

        handleAction(player);
        return InventoryTransactionResult.SUCCESS;
    }

    private void handleAction(PlayerSession player) {
        ItemStack selectedItem = player.getInventory().getSelectedItem();
        switch (action) {
            case PLACE:
                Optional<Block> block = player.getLevel().getBlockIfChunkLoaded(blockPosition);
                if (!block.isPresent()) {
                    // Unloaded block
                    return;
                }
                boolean validInteraction = false;
                BlockBehavior behavior = BlockBehaviors.getBlockBehavior(block.get().getBlockState().getBlockType());
                if (!player.isSneaking()) {
                    validInteraction = behavior.onUse(block.get(), player);
                }
                if (!validInteraction || player.isSneaking()) {
                    Vector3f position = player.getPosition();
                    // TODO: 17/02/2019 placing 
                }

                if (validInteraction) {
                    player.getInventory().setSelectedItem(selectedItem, player);
                } else {
                    resendBlockAroundArea(player, block.get());
                }
                break;
            case USE:
                break;
            case BREAK:
                break;

        }
    }

    public enum Action {
        PLACE,
        USE,
        BREAK
    }
}
