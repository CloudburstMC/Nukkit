package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockDoorWarped extends BlockDoorWood {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoorWarped() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoorWarped(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Door Block";
    }

    @Override
    public int getId() {
        return WARPED_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.WARPED_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
