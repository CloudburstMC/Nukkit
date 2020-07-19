package cn.nukkit.item;

public class ItemWarpedFungusOnAStick extends ItemTool {

    public ItemWarpedFungusOnAStick() {
        this(0, 1);
    }

    public ItemWarpedFungusOnAStick(Integer meta) {
        this(meta, 1);
    }

    public ItemWarpedFungusOnAStick(Integer meta, int count) {
        super(WARPED_FUNGUS_ON_A_STICK, meta, count, "Warped Fungus on a stick");
    }
    
    @Override
    public int getMaxDurability() {
        return 100;
    }

    @Override
    public boolean damageWhenBreaking() {
        return false;
    }
}
