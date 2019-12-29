package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 27.12.2015.
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

    @Override
    public double getMaxY() {
        return this.y + 1 - 0.125;
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
    public int onUpdate(int type) {
        for(int y = 1; y < 255 - this.getFloorY(); y++) {
            if(this.add(0, y).getLevelBlockAtLayer(1) instanceof BlockWater && this.add(0, y).getLevelBlock().canBeReplaced() || this.add(0, y).getLevelBlock() instanceof BlockWater) {
                this.getLevel().setBlock(this.add(0, y), get(BUBBLE_COLUMN, 1), true, false);
                this.getLevel().setBlock(this.add(0, y), 1, get(WATER), true, false);
            } else {
                break;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

}
