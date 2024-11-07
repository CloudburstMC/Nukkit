package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFormEvent;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockSoulSand extends BlockSolid {

    @Override
    public String getName() {
        return "Soul Sand";
    }

    @Override
    public int getId() {
        return SOUL_SAND;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.875;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.motionX *= 0.4d;
        entity.motionZ *= 0.4d;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block up = this.up();
            if (up instanceof BlockWater && (up.getDamage() == 0 || up.getDamage() == 8)) {
                BlockFormEvent event = new BlockFormEvent(up, Block.get(BUBBLE_COLUMN, BlockBubbleColumn.DIRECTION_UP));
                Server.getInstance().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.getLevel().setBlock(up, event.getNewState(), false, true);
                }
            }
        }

        return 0;
    }
}
