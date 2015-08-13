package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Tool extends Item {
    public static final int TIER_WOODEN = 1;
    public static final int TIER_GOLD = 2;
    public static final int TIER_STONE = 3;
    public static final int TIER_IRON = 4;
    public static final int TIER_DIAMOND = 5;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_SWORD = 1;
    public static final int TYPE_SHOVEL = 2;
    public static final int TYPE_PICKAXE = 3;
    public static final int TYPE_AXE = 4;
    public static final int TYPE_SHEARS = 5;

    public static final int DURABILITY_WOODEN = 60;
    public static final int DURABILITY_GOLD = 33;
    public static final int DURABILITY_STONE = 132;
    public static final int DURABILITY_IRON = 251;
    public static final int DURABILITY_DIAMOND = 1562;
    public static final int DURABILITY_FLINT_STEEL = 65;
    public static final int DURABILITY_SHEARS = 239;
    public static final int DURABILITY_BOW = 385;

    public Tool(int id) {
        this(id, 0, 1, "Unknown");
    }

    public Tool(int id, int meta) {
        this(id, meta, 1, "Unknown");
    }

    public Tool(int id, int meta, int count) {
        this(id, meta, count, "Unknown");
    }

    public Tool(int id, int meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean useOn(Object object) {
        if (this.isHoe()) {
            if ((object instanceof Block) && (((Block) object).getId() == GRASS || ((Block) object).getId() == DIRT)) {
                this.meta++;
            }
        } else if ((object instanceof Entity) && !this.isSword()) {
            this.meta += 2;
        } else {
            this.meta++;
        }
        return true;
    }

    @Override
    public boolean isPickaxe() {
        return false;
    }

    @Override
    public boolean isAxe() {
        return false;
    }

    @Override
    public boolean isSword() {
        return false;
    }

    @Override
    public boolean isShovel() {
        return false;
    }

    @Override
    public boolean isHoe() {
        return false;
    }

    @Override
    public boolean isShears() {
        return (this.id == SHEARS);
    }

    @Override
    public boolean isTool() {
        return (this.id == FLINT_STEEL || this.id == SHEARS || this.id == BOW || this.isPickaxe() || this.isAxe() || this.isShovel() || this.isSword());
    }
}
