package cn.nukkit.server.block.behavior;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.item.behavior.ItemBehavior;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import com.flowpowered.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.Collection;

public interface BlockBehavior extends ItemBehavior {

    Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item);

    float getBreakTime(Player player, Block block, @Nullable ItemInstance item);

    boolean isToolCompatible(Block block, @Nullable ItemInstance item);

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

    enum Result {
        NOTHING,
        REMOVE_ONE_ITEM,
        PLACE_BLOCK_AND_REMOVE_ONE_ITEM,
        REDUCE_DURABILITY,
        BREAK_BLOCK,
    }
}
