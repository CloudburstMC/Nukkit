package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 21.02.2016.
 */
public class BlockSlime extends BlockSolid {

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public String getName() {
        return "Slime Block";
    }

    @Override
    public int getId() {
        return SLIME_BLOCK;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity.motionY < 0.0) {
            double d = entity instanceof EntityLiving ? 1.0 : 0.8;
            entity.setMotion(new Vector3(entity.motionX, -entity.motionY * d, entity.motionZ));
        }
    }

    @Override
    public double getFrictionFactor() {
        return 0.8;
    }
}
