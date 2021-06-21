package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemRabbitHide extends Item {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemRabbitHide() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemRabbitHide(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemRabbitHide(Integer meta, int count) {
        super(RABBIT_HIDE, meta, count, "Rabbit Hide");
    }
}
