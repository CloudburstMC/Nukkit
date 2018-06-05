package com.nukkitx.server.block.behavior;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.TierType;
import com.nukkitx.api.item.ToolType;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.item.behavior.ItemBehavior;
import com.nukkitx.server.network.bedrock.session.PlayerSession;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public interface BlockBehavior extends ItemBehavior {

    Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item);

    float getBreakTime(Player player, Block block, @Nullable ItemInstance item);

    boolean isCorrectTool(@Nullable ItemInstance item);

    default BoundingBox getBoundingBox(Block block) {
        Vector3f asFloat = block.getBlockPosition().toFloat();
        return new BoundingBox(asFloat, asFloat.add(1, 1, 1));
    }

    default boolean onUpdate(Block block) {
        return true;
    }

    default boolean onEntityCollision(BaseEntity entity) {
        return true;
    }

    /**
     * @param block     block
     * @param neighbour neighboring block
     * @return whether to schedule an update
     */
    default boolean onNeighboringBlockChange(Block block, Block neighbour) {
        return false;
    }

    Result onBreak(PlayerSession session, Block block, ItemInstance withItem);

    boolean onPlace(PlayerSession session, Block against, ItemInstance withItem);

    default Optional<BlockState> overridePlacement(Vector3i against, BlockFace face, ItemInstance withItem) {
        return Optional.empty();
    }

    default float getMiningEfficiency(@Nullable ItemInstance item) {
        if (item == null) {
            return 1f;
        }
        ItemType itemType = item.getItemType();

        float efficiency = itemType.getTierType().map(TierType::getMiningEfficiency).orElse(1f);

        efficiency *= itemType.getToolType().map(ToolType::getEfficiencyMultiplier).orElse(1f);

        return efficiency;
    }

    enum Result {
        NOTHING,
        REMOVE_ONE_ITEM,
        PLACE_BLOCK_AND_REMOVE_ONE_ITEM,
        REDUCE_DURABILITY,
        BREAK_BLOCK,
    }
}
