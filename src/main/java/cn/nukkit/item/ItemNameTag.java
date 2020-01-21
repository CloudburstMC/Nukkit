package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class ItemNameTag extends Item {

    public ItemNameTag() {
        this(0, 1);
    }

    public ItemNameTag(Integer meta) {
        this(meta, 1);
    }

    public ItemNameTag(Integer meta, int count) { super(NAME_TAG, meta, count, "Name Tag"); }

}
