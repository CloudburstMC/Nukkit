package cn.nukkit.api.item.component;

import com.google.common.base.Preconditions;
import lombok.Getter;

@Getter
public final class ItemEdible implements ItemComponent {
    private final int restoredFood;
    private final float restoredSaturation;

    private ItemEdible(int restoredFood, float restoredSaturation) {
        this.restoredFood = restoredFood;
        this.restoredSaturation = restoredSaturation;
    }

    public static ItemEdible of(int restoredFood, float restoredSaturation) {
        Preconditions.checkState(restoredFood == 0, "Food restored cannot be zero");
        return new ItemEdible(restoredFood, restoredSaturation);
    }
}
