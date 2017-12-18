package cn.nukkit.api.item.component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CreeperFace
 */

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemBreakable implements ItemComponent {

    int maxDurability;

    public static ItemBreakable of(int maxDurability) {
        return new ItemBreakable(maxDurability);
    }
}
