package cn.nukkit.item;

public class ItemVexArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemVexArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemVexArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemVexArmorTrimSmithingTemplate(Integer meta, int count) {
        super(VEX_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Vex Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.VEX;
    }
}
