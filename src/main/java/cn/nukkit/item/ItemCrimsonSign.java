package cn.nukkit.item;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockCrimsonSignPost;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemCrimsonSign extends ItemSign {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCrimsonSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCrimsonSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCrimsonSign(Integer meta, int count) {
        super(CRIMSON_SIGN, meta, count, "Crimson Sign", new BlockCrimsonSignPost());
    }
}
