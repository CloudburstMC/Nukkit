package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockCampfireSoul;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemCampfireSoul extends Item {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCampfireSoul() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCampfireSoul(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemCampfireSoul(Integer meta, int count) {
        super(SOUL_CAMPFIRE, meta, count, "Soul Campfire");
        this.block = new BlockCampfireSoul();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
