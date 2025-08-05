package cn.nukkit.item;

public class ItemRibArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemRibArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemRibArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemRibArmorTrimSmithingTemplate(Integer meta, int count) {
        super(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Rib Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.RIB;
    }
}
