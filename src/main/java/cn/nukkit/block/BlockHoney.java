package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;

import java.util.Random;

public class BlockHoney extends BlockSolid {
    private static final Random RANDOM = new Random();

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
        if (!entity.onGround && entity.motionY <= 0.08 &&
                (!(entity instanceof Player)
                        || !((Player) entity).getAdventureSettings().get(AdventureSettings.Type.FLYING))) {
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

                if (RANDOM.nextInt(10) == 0) {
                    level.addSound(entity, Sound.LAND_SLIME);
                }
            }
        }
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x+1, y+1, z+1);
    }

    @Override
    public double getMinX() {
        return x + 0.1;
    }

    @Override
    public double getMaxX() {
        return x + 0.9;
    }

    @Override
    public double getMinZ() {
        return z + 0.1;
    }

    @Override
    public double getMaxZ() {
        return z + 0.9;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getLightFilter() {
        return 1;
    }
}
