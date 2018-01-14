package cn.nukkit.api.entity.item;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;

/**
 * @author CreeperFace
 */
public interface Item extends Entity {

    /**
     * Gets the item stack associated with this item drop.
     *
     * @return An item stack.
     */
    Item getItemStack();

    /**
     * Sets the item associated with this item drop.
     *
     * @param item An item.
     */
    void setItemStack(Item item);

    /**
     * Gets the delay before this Item is available to be picked up by players
     *
     * @return Remaining delay
     */
    int getPickupDelay();

    /**
     * Sets the delay before this Item is available to be picked up by players
     *
     * @param delay New delay
     */
    void setPickupDelay(int delay);

    /**
     * Gets the thrower of this item
     *
     * @return Player or null
     */
    Player getOwner();
}
