package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.Tag;

import javax.annotation.Nonnull;
import java.util.Random;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ItemTool extends Item implements ItemDurable {
    public static final int TIER_WOODEN = 1;
    public static final int TIER_GOLD = 2;
    public static final int TIER_STONE = 3;
    public static final int TIER_IRON = 4;
    public static final int TIER_DIAMOND = 5;
    @Since("1.3.2.0-PN") public static final int TIER_NETHERITE = 6;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_SWORD = 1;
    public static final int TYPE_SHOVEL = 2;
    public static final int TYPE_PICKAXE = 3;
    public static final int TYPE_AXE = 4;
    public static final int TYPE_SHEARS = 5;
    @Since("1.3.2.0-PN") public static final int TYPE_HOE = 6;
    
    /**
     * Same breaking speed independent of the tool.
     */
    @PowerNukkitOnly
    public static final int TYPE_HANDS_ONLY = dynamic(Integer.MAX_VALUE);

    public static final int DURABILITY_WOODEN = dynamic(60);
    public static final int DURABILITY_GOLD = dynamic(33);
    public static final int DURABILITY_STONE = dynamic(132);
    public static final int DURABILITY_IRON = dynamic(251);
    public static final int DURABILITY_DIAMOND = dynamic(1562);
    @Since("1.3.2.0-PN") public static final int DURABILITY_NETHERITE = dynamic(2032);
    public static final int DURABILITY_FLINT_STEEL = dynamic(65);
    public static final int DURABILITY_SHEARS = dynamic(239);
    public static final int DURABILITY_BOW = dynamic(385);
    public static final int DURABILITY_TRIDENT = dynamic(251);
    public static final int DURABILITY_FISHING_ROD = dynamic(65);
    @Since("1.4.0.0-PN") public static final int DURABILITY_CROSSBOW = dynamic(465);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static Item getBestTool(int toolType) {
        switch (toolType) {
            case TYPE_NONE:
            case TYPE_PICKAXE:
                return Item.get(ItemID.NETHERITE_PICKAXE);
            case TYPE_AXE:
                return Item.get(ItemID.NETHERITE_AXE);
            case TYPE_SHOVEL:
                return Item.get(ItemID.NETHERITE_SHOVEL);
            case TYPE_SHEARS:
                return Item.get(ItemID.SHEARS);
            case TYPE_SWORD:
                return Item.get(ItemID.NETHERITE_SWORD);
            default:
                // Can't use the switch-case syntax because they are dynamic types
                if (toolType == TYPE_HOE) {
                    return Item.get(ItemID.NETHERITE_HOE);
                }
                if (toolType == TYPE_HANDS_ONLY) {
                    return Item.getBlock(BlockID.AIR);
                }
                return Item.get(ItemID.NETHERITE_PICKAXE);
        }
    }

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
        if (this.isUnbreakable() || isDurable() || !damageWhenBreaking()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe()
                ) {
            this.meta++;
        } else if (!this.isShears() && block.calculateBreakTime(this) > 0) {
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
        if (this.isUnbreakable() || isDurable() || !damageWhenBreaking()) {
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
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment durability = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100);
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
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
        switch (this.id) {
            case FLINT_STEEL:
            case SHEARS:
            case BOW:
            case CROSSBOW:
            case SHIELD:
                return true;
            default:
                return this.isPickaxe() || this.isAxe() || this.isShovel() || this.isSword() || this.isHoe();
        }
    }

    @Override
    public int getEnchantAbility() {
        int tier = this.getTier();
        switch (tier) {
            case TIER_STONE:
                return 5;
            case TIER_WOODEN:
            case TIER_DIAMOND:
                return 10;
            case TIER_GOLD:
                return 22;
            case TIER_IRON:
                return 14;
        }
        
        if (tier == TIER_NETHERITE) {
            return 15;
        }
        return 0;
    }

}
