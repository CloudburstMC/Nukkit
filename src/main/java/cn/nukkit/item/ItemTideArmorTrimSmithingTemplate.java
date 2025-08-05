package cn.nukkit.item;

public class ItemTideArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemTideArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemTideArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemTideArmorTrimSmithingTemplate(Integer meta, int count) {
        super(TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Tide Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.TIDE;
    }
}
