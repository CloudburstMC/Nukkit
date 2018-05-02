package com.nukkitx.server.entity.system;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.PortalCooldown;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;

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
