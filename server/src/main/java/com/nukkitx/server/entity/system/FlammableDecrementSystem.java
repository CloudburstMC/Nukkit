package com.nukkitx.server.entity.system;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.Flammable;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;

public class FlammableDecrementSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .runner(new FlammableDecrementSystem())
            .expectComponent(Flammable.class)
            .build();


    @Override
    public void run(Entity entity) {
        Flammable flammable = entity.ensureAndGet(Flammable.class);
        if (flammable.isIgnited()) {
            flammable.setIgnitedDuration(flammable.getIgnitedDuration() - 1);
        }
    }
}
