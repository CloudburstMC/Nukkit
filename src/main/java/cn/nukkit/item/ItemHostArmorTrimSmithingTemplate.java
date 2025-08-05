package cn.nukkit.item;

public class ItemHostArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemHostArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemHostArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemHostArmorTrimSmithingTemplate(Integer meta, int count) {
        super(HOST_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Host Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.HOST;
    }
}
