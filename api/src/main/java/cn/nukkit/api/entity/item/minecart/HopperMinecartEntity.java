package cn.nukkit.api.entity.item.minecart;

/**
 * @author CreeperFace
 */
public interface HopperMinecartEntity extends StorageMinecartEntity {

    /**
     * Checks whether or not this Minecart will pick up
     * items into its inventory.
     *
     * @return true if the Minecart will pick up items
     */
    boolean isEnabled();

    /**
     * Sets whether this Minecart will pick up items.
     *
     * @param enabled new enabled state
     */
    void setEnabled(boolean enabled);
}
