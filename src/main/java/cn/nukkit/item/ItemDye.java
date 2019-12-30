package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDye extends Item {

    public ItemDye(Identifier id) {
        super(id);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(this.getDamage());
    }
}
