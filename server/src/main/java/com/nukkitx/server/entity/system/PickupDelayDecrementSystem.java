package com.nukkitx.server.entity.system;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.PickupDelay;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;

public class PickupDelayDecrementSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .runner(new PickupDelayDecrementSystem())
            .expectComponent(PickupDelay.class)
            .build();

    @Override
    public void run(Entity entity) {
        PickupDelay delay = entity.ensureAndGet(PickupDelay.class);
        if (!delay.canPickup()) {
            delay.setDelayPickupTicks(delay.getDelayPickupTicks() - 1);
        }
    }
}
