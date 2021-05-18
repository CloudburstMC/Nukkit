package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.world.World;

/**
 * author: MagicDroidX
 * Nukkit Project
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

    @Override
    public boolean onBreak(Item item) {
        if (this.getWorld().getDimension() != World.DIMENSION_NETHER) {
            return this.getWorld().setBlock(this, Block.get(BlockID.WATER), true);
        } else {
            return super.onBreak(item);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == World.BLOCK_UPDATE_RANDOM) {
            if (world.getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 12) {
                BlockFadeEvent event = new BlockFadeEvent(this, world.getDimension() == World.DIMENSION_NETHER ? get(AIR) : get(WATER));
                world.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    world.setBlock(this, event.getNewState(), true);
                }
                return World.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{this.toItem()};
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
