package cn.nukkit.item;

public class ItemWardArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemWardArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemWardArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemWardArmorTrimSmithingTemplate(Integer meta, int count) {
        super(WARD_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Ward Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.WARD;
    }
}
