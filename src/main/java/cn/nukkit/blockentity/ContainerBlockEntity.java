package cn.nukkit.blockentity;

import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import java.util.stream.IntStream;

/**
 * An interface describes a container.
 */
public interface ContainerBlockEntity extends InventoryHolder {

    /**
     * Returns an array of slot indexes where the item should be pushed to
     *
     * @param direction direction from hopper to this block entity
     * @param item      target item
     * @return array of indexes or null if there's nothing to push
     */
    default int[] getHopperPushSlots(BlockFace direction, Item item) {
        return IntStream.rangeClosed(0, getInventory().getSize() - 1).toArray();
    }

    /**
     * Returns list of slot indexes where the item should be pulled from
     *
     * @return array of indexes or null if there's nothing to pull
     */
    default int[] getHopperPullSlots() {
        return IntStream.rangeClosed(0, getInventory().getSize() - 1).toArray();
    }
}
