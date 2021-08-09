package cn.nukkit.item;

public class ItemBannerPattern extends Item {

    public ItemBannerPattern() {
        this(0, 1);
    }

    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, count, "Banner Pattern");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
