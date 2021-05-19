package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitDifference;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends ItemTool instead of Item only in PowerNukkit")
public class ItemShield extends ItemTool {

    public ItemShield() {
        this(0, 1);
    }

    public ItemShield(Integer meta) {
        this(meta, 1);
    }

    public ItemShield(Integer meta, int count) {
        super(SHIELD, meta, count, "Shield");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 336;
    }
}
