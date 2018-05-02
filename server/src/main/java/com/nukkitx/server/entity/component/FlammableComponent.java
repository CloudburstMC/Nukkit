package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.component.Flammable;

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
