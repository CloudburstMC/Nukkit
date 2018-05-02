package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.component.Sittable;

public class SittableComponent implements Sittable {
    private boolean sitting;

    @Override
    public boolean isSitting() {
        return sitting;
    }

    @Override
    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }
}
