package cn.nukkit.server.entity.system;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.PortalCooldown;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;

public class PortalCooldownDecrementSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .runner(new PortalCooldownDecrementSystem())
            .expectComponent(PortalCooldown.class)
            .build();

    @Override
    public void run(Entity entity) {
        PortalCooldown cooldown = entity.ensureAndGet(PortalCooldown.class);
        if (!cooldown.canUsePortal()) {
            cooldown.setPortalCooldown(cooldown.getPortalCooldown() - 1);
        }
    }
}
