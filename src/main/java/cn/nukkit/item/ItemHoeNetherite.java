package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemHoeNetherite extends ItemTool {

    @Since("1.4.0.0-PN")
    public ItemHoeNetherite() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemHoeNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemHoeNetherite(Integer meta, int count) {
        super(NETHERITE_HOE, meta, count, "Netherite Hoe");
    }

    @Override
    public boolean isHoe() {
        return true;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }
    
    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
