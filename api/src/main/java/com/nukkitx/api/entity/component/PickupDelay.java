package com.nukkitx.api.entity.component;

import javax.annotation.Nonnegative;

public interface PickupDelay extends EntityComponent {

    default boolean canPickup() {
        return getDelayPickupTicks() <= 0;
    }

    /**
     * Gets the period of time (in ticks) before this item stack can be picked up.
     *
     * @return item stack delay
     */
    @Nonnegative
    int getDelayPickupTicks();

    /**
     * Sets the period of time (in ticks) before this item stack can be picked up.
     *
     * @param ticks item stack delay ticks
     */
    void setDelayPickupTicks(@Nonnegative int ticks);
}
