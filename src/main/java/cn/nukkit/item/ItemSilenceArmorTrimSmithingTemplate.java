package cn.nukkit.item;

public class ItemSilenceArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemSilenceArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSilenceArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSilenceArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Silence Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.SILENCE;
    }
}
