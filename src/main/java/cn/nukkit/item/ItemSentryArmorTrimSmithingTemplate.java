package cn.nukkit.item;

public class ItemSentryArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemSentryArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemSentryArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemSentryArmorTrimSmithingTemplate(Integer meta, int count) {
        super(SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Sentry Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.SENTRY;
    }
}
