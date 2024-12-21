package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class ItemTool extends Item implements ItemDurable {

    public static final int TIER_WOODEN = 1;
    public static final int TIER_GOLD = 2;
    public static final int TIER_STONE = 3;
    public static final int TIER_IRON = 4;
    public static final int TIER_DIAMOND = 5;
    public static final int TIER_NETHERITE = 6;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_SWORD = 1;
    public static final int TYPE_SHOVEL = 2;
    public static final int TYPE_PICKAXE = 3;
    public static final int TYPE_AXE = 4;
    public static final int TYPE_SHEARS = 5;
    public static final int TYPE_HOE = 6;

    // Using vanilla durability + 1 here
    public static final int DURABILITY_WOODEN = 60;
    public static final int DURABILITY_GOLD = 33;
    public static final int DURABILITY_STONE = 132;
    public static final int DURABILITY_IRON = 251;
    public static final int DURABILITY_DIAMOND = 1562;
    public static final int DURABILITY_NETHERITE = 2032;
    public static final int DURABILITY_FLINT_STEEL = 65;
    public static final int DURABILITY_SHEARS = 239;
    public static final int DURABILITY_BOW = 385;
    public static final int DURABILITY_CROSSBOW = 465;
    public static final int DURABILITY_TRIDENT = 251;
    public static final int DURABILITY_FISHING_ROD = 385;
    public static final int DURABILITY_CARROT_ON_A_STICK = 26;
    public static final int DURABILITY_WARPED_FUNGUS_ON_A_STICK = 100;

    public ItemTool(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemTool(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemTool(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemTool(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean useOn(Block block) {
        if (this.noDamageOnBreak() || this.isUnbreakable() || this.isDurable()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears()
                ) {
            this.meta++;
        } else if (this.isSword() && block.getHardness() > 0) {
            this.meta += 2;
        } else if (this.isHoe()) {
            if (block.getId() == GRASS || block.getId() == DIRT) {
                this.meta++;
            }
        } else {
            this.meta++;
        }
        return true;
    }

    @Override
    public boolean useOn(Entity entity) {
        if (this.noDamageOnAttack() || this.isUnbreakable() || this.isDurable()) {
            return true;
        }

        if ((entity != null) && !this.isSword()) {
            this.meta += 2;
        } else {
            this.meta++;
        }

        return true;
    }

    private boolean isDurable() {
        Enchantment durability = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= ThreadLocalRandom.current().nextInt(100);
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getEnchantAbility() {
        switch (this.getTier()) {
            case TIER_STONE:
                return 5;
            case TIER_WOODEN:
                return 15;
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 22;
            case TIER_IRON:
                return 14;
            case TIER_NETHERITE:
                return 10; //TODO
        }

        return 0;
    }

    /**
     * No damage to item when it's used to attack entities
     * @return whether the item should take damage when used to attack entities
     */
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     * @return whether the item should take damage when used to break blocks
     */
    public boolean noDamageOnBreak() {
        return false;
    }
}
