package cn.nukkit.item;

public class ItemLodestoneCompass extends Item {

    public ItemLodestoneCompass() {
        this(0, 1);
    }

    public ItemLodestoneCompass(Integer meta) {
        this(meta, 1);
    }

    public ItemLodestoneCompass(Integer meta, int count) {
        super(LODESTONE_COMPASS, meta, count, "Lodestone Compass");
    }
}
