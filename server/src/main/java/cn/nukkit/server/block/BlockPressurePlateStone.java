package cn.nukkit.server.block;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.entity.EntityLiving;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockPressurePlateStone extends BlockPressurePlateBase {

    public BlockPressurePlateStone(int meta) {
        super(meta);
        this.onPitch = 0.6f;
        this.offPitch = 0.5f;
    }

    public BlockPressurePlateStone() {
        this(0);
    }

    @Override
    public String getName() {
        return "Stone Pressure Plate";
    }

    @Override
    public int getId() {
        return STONE_PRESSURE_PLATE;
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 2.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity instanceof EntityLiving && entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}
