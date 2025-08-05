package cn.nukkit.item;

public class ItemRaiserArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemRaiserArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemRaiserArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemRaiserArmorTrimSmithingTemplate(Integer meta, int count) {
        super(RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Raiser Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.RAISER;
    }
}
