package com.nukkitx.server.block.behavior;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.TierType;
import com.nukkitx.api.item.ToolType;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.item.behavior.ItemBehavior;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public interface BlockBehavior extends ItemBehavior {

    Collection<ItemStack> getDrops(Player player, Block block, @Nullable ItemStack item);

    default float getBreakTime(Player player, Block block, @Nullable ItemStack item) {
        float breakTime = block.getBlockState().getBlockType().hardness();

        if (isCorrectTool(item)) {
            breakTime *= 1.5;
        } else {
            breakTime *= 5;
        }

        breakTime /= getMiningEfficiency(item);

        return breakTime;
    }

    boolean isCorrectTool(@Nullable ItemStack item);

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

    Result onBreak(NukkitPlayerSession session, Block block, ItemStack withItem);

    boolean onPlace(NukkitPlayerSession session, Block against, ItemStack withItem);

    boolean onUse(Block block, NukkitPlayerSession player);

    default Optional<BlockState> overridePlacement(Vector3i against, BlockFace face, ItemStack withItem) {
        return Optional.empty();
    }

    default float getMiningEfficiency(@Nullable ItemStack item) {
        if (item == null) {
            return 1f;
        }
        ItemType itemType = item.getItemType();

        float efficiency = itemType.getTierType().map(TierType::getMiningEfficiency).orElse(1f);

        efficiency *= itemType.getToolType().map(ToolType::getEfficiencyMultiplier).orElse(1f);

        return efficiency;
    }

    default boolean mayPlaceOn(Block block, Block toPlace) {
        // TODO
        return false;
    }

    enum Result {
        NOTHING,
        REMOVE_ONE_ITEM,
        PLACE_BLOCK_AND_REMOVE_ONE_ITEM,
        REDUCE_DURABILITY,
        BREAK_BLOCK,
    }
}
