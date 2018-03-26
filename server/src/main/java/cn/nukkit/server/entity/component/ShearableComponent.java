package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Shearable;

import java.util.OptionalInt;

public class ShearableComponent implements Shearable {
    private int ticksSinceSheared = -1;

    @Override
    public boolean isSheared() {
        return ticksSinceSheared >= 0;
    }

    @Override
    public void setSheared(boolean sheared) {
        if (sheared) {
            ticksSinceSheared = 0;
        } else {
            ticksSinceSheared = -1;
        }
    }

    @Override
    public OptionalInt ticksSinceSheared() {
        if (ticksSinceSheared == -1) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(ticksSinceSheared);
    }
}
