package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.3.2.0-PN")
public class ItemSwordNetherite extends ItemTool {

    @Since("1.3.2.0-PN")
    public ItemSwordNetherite() {
        this(0, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemSwordNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.3.2.0-PN")
    public ItemSwordNetherite(Integer meta, int count) {
        super(NETHERITE_SWORD, meta, count, "Netherite Sword");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isSword() {
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
