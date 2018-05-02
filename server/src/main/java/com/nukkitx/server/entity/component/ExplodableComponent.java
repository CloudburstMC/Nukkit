package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.component.Explode;

public class ExplodableComponent implements Explode {
    private final int fuse;
    private int radius;
    private boolean incendiary;

    public ExplodableComponent(int fuse, int radius, boolean incendiary) {
        this.fuse = fuse;
        this.radius = radius;
        this.incendiary = incendiary;
    }

    public int getFuse() {
        return fuse;
    }

    @Override
    public boolean isPrimed() {
        return false; //TODO
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void setIsIncendiary(boolean incendiary) {
        this.incendiary = incendiary;
    }

    @Override
    public boolean isIncendiary() {
        return incendiary;
    }
}
