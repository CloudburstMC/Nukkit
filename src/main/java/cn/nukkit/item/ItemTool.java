package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Identifier;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.Random;

import static cn.nukkit.block.BlockIds.DIRT;
import static cn.nukkit.block.BlockIds.GRASS;
import static cn.nukkit.item.ItemIds.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemTool extends Item implements ItemDurable {
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
    /**
     * Same breaking speed independent of the tool.
     */
    public static final int TYPE_HANDS_ONLY = 6;

    public static final int DURABILITY_WOODEN = 60;
    public static final int DURABILITY_GOLD = 33;
    public static final int DURABILITY_STONE = 132;
    public static final int DURABILITY_IRON = 251;
    public static final int DURABILITY_DIAMOND = 1562;
    public static final int DURABILITY_FLINT_STEEL = 65;
    public static final int DURABILITY_SHEARS = 239;
    public static final int DURABILITY_BOW = 385;
    public static final int DURABILITY_TRIDENT = 251;
    public static final int DURABILITY_FISHING_ROD = 65;

    private boolean unbreakable;

    public ItemTool(Identifier id) {
        super(id);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForBoolean("Unbreakable", this::setUnbreakable);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        if (this.unbreakable) tag.booleanTag("Unbreakable", true);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean useOn(Block block) {
        if (this.isUnbreakable() || isDurable()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears()
                ) {
            this.setMeta(getMeta() + 1);
        } else if (!this.isShears() && block.getBreakTime(this) > 0) {
            this.setMeta(getMeta() + 2);
        } else if (this.isHoe()) {
            if (block.getId() == GRASS || block.getId() == DIRT) {
                this.setMeta(getMeta() + 1);
            }
        } else {
            this.setMeta(getMeta() + 1);
        }
        return true;
    }

    @Override
    public boolean useOn(Entity entity) {
        if (this.isUnbreakable() || isDurable()) {
            return true;
        }

        if ((entity != null) && !this.isSword()) {
            this.setMeta(getMeta() + 2);
        } else {
            this.setMeta(getMeta() + 1);
        }

        return true;
    }

    private boolean isDurable() {
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment durability = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100);
    }

    @Override
    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
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
        return (this.getId() == SHEARS);
    }

    @Override
    public boolean isTool() {
        Identifier id = getId();
        return id == FLINT_AND_STEEL || id == SHEARS || id == BOW ||
                this.isPickaxe() || this.isAxe() || this.isShovel() || this.isSword() || this.isHoe();
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
        }

        return 0;
    }
}
