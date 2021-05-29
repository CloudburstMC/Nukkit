package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemDoorCrimson extends Item {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemDoorCrimson() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemDoorCrimson(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemDoorCrimson(Integer meta, int count) {
        super(CRIMSON_DOOR, 0, count, "Crimson Door");
        this.block = Block.get(BlockID.CRIMSON_DOOR_BLOCK);
    }

}
