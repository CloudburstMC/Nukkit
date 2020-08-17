package cn.nukkit.item;

public class ItemSwordNetherite extends ItemTool {
    public ItemSwordNetherite() {
        this(0, 1);
    }

    public ItemSwordNetherite(Integer meta) {
        this(meta, 1);
    }

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
