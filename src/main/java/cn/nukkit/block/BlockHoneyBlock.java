package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

public class BlockHoneyBlock extends BlockSolid {

    @Override
    public String getName() {
        return "Honey Block";
    }

    @Override
    public int getId() {
        return HONEY_BLOCK;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.onGround && entity.motionY <= 0.08 && !(entity instanceof Player)) {
            double ex = Math.abs(x + 0.5D - entity.x);
            double ez = Math.abs(z + 0.5D - entity.z);
            double width = 0.4375D + (double)(entity.getWidth() / 2.0F);
            if (ex + 1.0E-3D > width || ez + 1.0E-3D > width) {
                Vector3 motion = entity.getMotion();
                motion.y = -0.05;
                if (entity.motionY < -0.13) {
                    double m = -0.05 / entity.motionY;
                    motion.x *= m;
                    motion.z *= m;
                }

                if (!entity.getMotion().equals(motion)) {
                    entity.setMotion(motion);
                }
                entity.resetFallDistance();
            }
        }
    }
}
