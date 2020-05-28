package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@Since("1.2.1.0-PN")
@PowerNukkitOnly
public class ItemBannerPattern extends Item {
    public static final int PATTERN_CREEPER_CHARGE = 0;
    public static final int PATTERN_SKULL_CHARGE = 1;
    public static final int PATTERN_FLOWER_CHARGE = 2;
    public static final int PATTERN_THING = 3;
    public static final int PATTERN_FIELD_MASONED = 4;
    public static final int PATTERN_BORDURE_INDENTED = 5;

    public ItemBannerPattern() {
        this(0, 1);
    }

    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, count, "Bone");
        updateName();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setDamage(Integer meta) {
        super.setDamage(meta);
        updateName();
    }

    protected void updateName() {
        switch (super.meta % 6) {
            case PATTERN_CREEPER_CHARGE:
                name = "Creeper Charge Banner Pattern";
                return;
            case PATTERN_SKULL_CHARGE:
                name = "Skull Charge Banner Pattern";
                return;
            case PATTERN_FLOWER_CHARGE:
                name = "Flower Charge Banner Pattern";
                return;
            case PATTERN_THING:
                name = "Thing Banner Pattern";
                return;
            case PATTERN_FIELD_MASONED:
                name = "Field Banner Pattern";
                return;
            case PATTERN_BORDURE_INDENTED:
                name = "Bordure Idented Banner Pattern";
                return;
            default:
                name = "Banner Pattern";
        }
    }
}
