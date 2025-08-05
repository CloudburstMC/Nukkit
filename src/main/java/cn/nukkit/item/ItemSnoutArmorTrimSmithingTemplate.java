package cn.nukkit.item;

public class ItemSnoutArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemSnoutArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSnoutArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSnoutArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Snout Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.SNOUT;
    }
}
