package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DiamondLeggings extends Armor {

    public DiamondLeggings() {
        this(0, 1);
    }

    public DiamondLeggings(Integer meta) {
        this(meta, 1);
    }

    public DiamondLeggings(Integer meta, int count) {
        super(DIAMOND_LEGGINGS, meta, count, "Diamond Leggings");
    }

    @Override
    public boolean isLeggings(){
        return true;
    }

    @Override
    public int getTier() { return Armor.TIER_DIAMOND; }
}
