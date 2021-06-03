package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemPickaxeNetherite extends ItemTool {

    @Since("1.4.0.0-PN")
    public ItemPickaxeNetherite() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemPickaxeNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemPickaxeNetherite(Integer meta, int count) {
        super(NETHERITE_PICKAXE, meta, count, "Netherite Pickaxe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isPickaxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 6;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
