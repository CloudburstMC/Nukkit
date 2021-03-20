package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemCrossbow extends ItemTool {

    @Since("1.4.0.0-PN")
    public ItemCrossbow() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }
}
