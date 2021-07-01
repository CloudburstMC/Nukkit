package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemChorusFruitPopped extends Item {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemChorusFruitPopped() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemChorusFruitPopped(Integer meta) {
        this(meta, 1);
    }

    public ItemChorusFruitPopped(Integer meta, int count) {
        super(POPPED_CHORUS_FRUIT, meta, count, "Popped Chorus Fruit");
    }
}
