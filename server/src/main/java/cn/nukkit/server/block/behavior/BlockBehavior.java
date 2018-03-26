package cn.nukkit.server.block.behavior;

import cn.nukkit.api.Player;
import cn.nukkit.api.Server;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.server.item.behavior.ItemBehavior;
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

    default boolean onTick(Server server, Block block) {
        return true;
    }

    @Override
    default float getMiningEfficiency(Block block, @Nullable ItemInstance item) {
        return 1f;
    }
}
