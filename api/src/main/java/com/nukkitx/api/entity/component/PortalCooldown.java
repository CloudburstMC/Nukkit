package com.nukkitx.api.entity.component;

import javax.annotation.Nonnegative;

public interface PortalCooldown extends EntityComponent {

    default boolean canUsePortal() {
        return getPortalCooldown() <= 0;
    }

    /**
     * Gets the period of time (in ticks) before this entity can use a portal.
     *
     * @return portal cooldown ticks
     */
    @Nonnegative
    int getPortalCooldown();

    /**
     * Sets the period of time (in ticks) before this entity can use a portal.
     *
     * @param ticks portal cooldown ticks
     */
    void setPortalCooldown(@Nonnegative int ticks);
}
