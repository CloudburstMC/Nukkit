package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockDoorCrimson extends BlockDoorWood {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoorCrimson() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockDoorCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Door Block";
    }

    @Override
    public int getId() {
        return CRIMSON_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CRIMSON_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
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
