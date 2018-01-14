package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Flammable;

public class FlammableComponent implements Flammable {
    private int ticks;

    @Override
    public int getIgnitedDuration() {
        return ticks;
    }

    @Override
    public void setIgnitedDuration(int ticks) {
        this.ticks = ticks;
    }
}
