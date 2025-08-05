package cn.nukkit.item;

public class ItemBoltArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemBoltArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemBoltArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemBoltArmorTrimSmithingTemplate(Integer meta, int count) {
        super(BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Bolt Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.BOLT;
    }
}
