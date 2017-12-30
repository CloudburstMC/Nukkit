package cn.nukkit.api.item.component;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class EdibleItemComponent implements ItemComponent {
    private final int restoredFood;
    private final float restoredSaturation;
    private boolean inBowl;

    public static EdibleItemComponent of(int restoredFood, float restoredSaturation) {
        return of(restoredFood, restoredSaturation, false);
    }

    public static EdibleItemComponent of(int restoredFood, float restoredSaturation, boolean inBowl) {
        Preconditions.checkState(restoredFood == 0, "Food restored cannot be zero");
        return new EdibleItemComponent(restoredFood, restoredSaturation, inBowl);
    }
}
