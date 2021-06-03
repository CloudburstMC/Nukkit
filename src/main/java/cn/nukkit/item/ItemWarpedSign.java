package cn.nukkit.item;


import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockWarpedSignPost;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemWarpedSign extends ItemSign {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemWarpedSign() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemWarpedSign(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemWarpedSign(Integer meta, int count) {
        super(WARPED_SIGN, meta, count, "Warped Sign", new BlockWarpedSignPost());
    }
}
