package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Explodable;

public class ExplodableComponent implements Explodable {
    private int radius;
    private boolean incendiary;

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
