package cn.nukkit.item;

public class ItemShield extends ItemTool {

    public ItemShield() {
        this(0, 1);
    }

    public ItemShield(Integer meta) {
        this(meta, 1);
    }

    public ItemShield(Integer meta, int count) {
        super(SHIELD, meta, count, "Shield");
    }

    @Override
    public int getMaxDurability() {
        return 337;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean allowOffhand() {
        return true;
    }
}
