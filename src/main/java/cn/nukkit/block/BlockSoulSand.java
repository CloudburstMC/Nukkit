package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFormEvent;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
public class BlockSoulSand extends BlockSolid {

    public BlockSoulSand() {
    }

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
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed MaxY BB, soul sand is a normal full cube in Bedrock Edition")
    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isSoulSpeedCompatible() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.motionX *= 0.4d;
        entity.motionZ *= 0.4d;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block up = up();
            if (up instanceof BlockWater && (up.getDamage() == 0 || up.getDamage() == 8)) {
                BlockFormEvent event = new BlockFormEvent(up, new BlockBubbleColumn(0));
                if (!event.isCancelled()) {
                    if (event.getNewState().getWaterloggingLevel() > 0) {
                        this.getLevel().setBlock(up, 1, new BlockWater(), true, false);
                    }
                    this.getLevel().setBlock(up, 0, event.getNewState(), true, true);
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }

}
