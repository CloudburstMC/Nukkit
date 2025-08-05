package cn.nukkit.item;

public class ItemFlowArmorTrimSmithingTemplate extends Item implements ItemTrimPattern {

    public ItemFlowArmorTrimSmithingTemplate() {
        this(0, 1);
    }

    public ItemFlowArmorTrimSmithingTemplate(Integer meta) {
        this(meta, 1);
    }

    public ItemFlowArmorTrimSmithingTemplate(Integer meta, int count) {
        super(FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Flow Armor Trim");
    }

    @Override
    public Type getPattern() {
        return Type.FLOW;
    }
}
