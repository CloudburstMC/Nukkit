package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.BannerPattern;

@Since("1.2.1.0-PN")
@PowerNukkitOnly
public class ItemBannerPattern extends Item {
    @PowerNukkitOnly
    public static final int PATTERN_CREEPER_CHARGE = 0;

    @PowerNukkitOnly
    public static final int PATTERN_SKULL_CHARGE = 1;

    @PowerNukkitOnly
    public static final int PATTERN_FLOWER_CHARGE = 2;

    @PowerNukkitOnly
    public static final int PATTERN_THING = 3;

    @PowerNukkitOnly
    public static final int PATTERN_FIELD_MASONED = 4;

    @PowerNukkitOnly
    public static final int PATTERN_BORDURE_INDENTED = 5;

    @PowerNukkitOnly
    public ItemBannerPattern() {
        this(0, 1);
    }

    @PowerNukkitOnly
    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, count, "Bone");
        updateName();
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected ItemBannerPattern(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
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
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public BannerPattern.Type getPatternType() {
        if (getId() != BANNER_PATTERN) {
            return BannerPattern.Type.PATTERN_CREEPER;
        }
        switch (getDamage()) {
            default:
            case PATTERN_CREEPER_CHARGE: return BannerPattern.Type.PATTERN_CREEPER; 
            case PATTERN_SKULL_CHARGE: return BannerPattern.Type.PATTERN_SKULL; 
            case PATTERN_FLOWER_CHARGE: return BannerPattern.Type.PATTERN_FLOWER;
            case PATTERN_THING: return BannerPattern.Type.PATTERN_MOJANG;
            case PATTERN_FIELD_MASONED: return BannerPattern.Type.PATTERN_BRICK; 
            case PATTERN_BORDURE_INDENTED: return BannerPattern.Type.PATTERN_CURLY_BORDER;
        }
    }

    protected void updateName() {
        if (getId() != BANNER_PATTERN) {
            return;
        }
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
