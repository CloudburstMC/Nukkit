package cn.nukkit.api.item.component;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemEdible implements ItemComponent {
    private final int restoredFood;
    private final float restoredSaturation;

    public static ItemEdible of(int restoredFood, float restoredSaturation) {
        Preconditions.checkState(restoredFood == 0, "Food restored cannot be zero");
        return new ItemEdible(restoredFood, restoredSaturation);
    }
}
