package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.PickupDelay;

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
