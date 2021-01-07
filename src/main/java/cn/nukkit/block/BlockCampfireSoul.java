package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockCampfireSoul extends BlockCampfire {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCampfireSoul() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCampfireSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_CAMPFIRE_BLOCK;
    }

    @Override
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    public int getLightLevel() {
        return isExtinguished()? 0 : 10;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.SOUL_CAMPFIRE);
    }
}
