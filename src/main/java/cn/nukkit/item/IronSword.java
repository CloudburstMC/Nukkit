package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronSword extends Tool {

    public IronSword() {
        this(0, 1);
    }

    public IronSword(Integer meta) {
        this(meta, 1);
    }

    public IronSword(Integer meta, int count) {
        super(IRON_SWORD, meta, count, "Iron Sword");
    }

    @Override
    public int getMaxDurability() {
        return Tool.DURABILITY_IRON;
    }

    @Override
    public boolean isSword() {
        return true;
    }

    @Override
    public int getTier() {
        return Tool.TIER_IRON;
    }
}
