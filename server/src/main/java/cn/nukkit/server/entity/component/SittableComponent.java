package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Sittable;

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
