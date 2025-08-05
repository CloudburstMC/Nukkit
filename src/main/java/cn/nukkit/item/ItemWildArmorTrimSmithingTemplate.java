package cn.nukkit.item;

public class ItemWildArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemWildArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWildArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWildArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WILD_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wild Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.WILD;
    }
}
