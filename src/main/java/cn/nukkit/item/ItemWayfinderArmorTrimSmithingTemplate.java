package cn.nukkit.item;

public class ItemWayfinderArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemWayfinderArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWayfinderArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWayfinderArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wayfinder Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.WAYFINDER;
    }
}
