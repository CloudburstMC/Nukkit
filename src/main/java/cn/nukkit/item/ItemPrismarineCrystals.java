package cn.nukkit.item;

public class ItemPrismarineCrystals extends Item {

    public ItemPrismarineCrystals() {
        this(0, 1);
    }

    public ItemPrismarineCrystals(Integer meta) {
        this(meta, 1);
    }

    public ItemPrismarineCrystals(Integer meta, int count) {
        super(PRISMARINE_CRYSTALS, 0, count, "Prismarine Crystals");
    }
}
