package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * @author Nukkit Project Team
 */
public class BlockPressurePlateWood extends BlockPressurePlateBase {

    public BlockPressurePlateWood(Identifier id) {
        super(id);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity.canTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}