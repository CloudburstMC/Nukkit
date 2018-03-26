package cn.nukkit.server.entity.system;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Flammable;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;

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
