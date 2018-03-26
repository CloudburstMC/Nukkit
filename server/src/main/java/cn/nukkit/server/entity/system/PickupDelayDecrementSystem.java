package cn.nukkit.server.entity.system;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.PickupDelay;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;

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
