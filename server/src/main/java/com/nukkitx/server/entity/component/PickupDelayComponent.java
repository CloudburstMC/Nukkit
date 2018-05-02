package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.component.PickupDelay;

public class PickupDelayComponent implements PickupDelay {
    private int delay;

    public PickupDelayComponent(int delayTicks) {
        this.delay = delayTicks;
    }

    @Override
    public int getDelayPickupTicks() {
        return delay;
    }

    @Override
    public void setDelayPickupTicks(int ticks) {
        this.delay = ticks;
    }
}
