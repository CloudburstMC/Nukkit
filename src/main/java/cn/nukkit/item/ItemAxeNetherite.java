package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemAxeNetherite extends ItemTool {

    @Since("1.4.0.0-PN")
    public ItemAxeNetherite() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemAxeNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemAxeNetherite(Integer meta, int count) {
        super(NETHERITE_AXE, meta, count, "Netherite Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 8;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
