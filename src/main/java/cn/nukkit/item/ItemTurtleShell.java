package cn.nukkit.item;

/**
 * @author PetteriM1
 */
public class ItemTurtleShell extends ItemArmor {

    public ItemTurtleShell() {
        this(0, 1);
    }

    public ItemTurtleShell(Integer meta) {
        this(meta, 1);
    }

    public ItemTurtleShell(Integer meta, int count) {
        super(TURTLE_SHELL, meta, count, "Turtle Shell");
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_OTHER;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 276;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}
