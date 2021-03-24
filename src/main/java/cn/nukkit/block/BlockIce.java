package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockIce extends BlockTransparent {

    public BlockIce() {
    }

    @Override
    public int getId() {
        return ICE;
    }

    @Override
    public String getName() {
        return "Ice";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will not create water when it is above air")
    @Override
    public boolean onBreak(Item item) {
        if (level.getDimension() == Level.DIMENSION_NETHER 
                || item.getEnchantmentLevel(Enchantment.ID_SILK_TOUCH) > 0 
                || down().getId() == BlockID.AIR) {
            return super.onBreak(item);
        }
        
        return level.setBlock(this, Block.get(BlockID.WATER), true);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (level.getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 12) {
                BlockFadeEvent event = new BlockFadeEvent(this, level.getDimension() == Level.DIMENSION_NETHER ? get(AIR) : get(WATER));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getLightFilter() {
        return 2;
    }
}
