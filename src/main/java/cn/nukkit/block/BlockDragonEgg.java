package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.level.particle.PortalParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import java.util.concurrent.ThreadLocalRandom;

public class BlockDragonEgg extends BlockFallable {

    public BlockDragonEgg() {
    }

    @Override
    public String getName() {
        return "Dragon Egg";
    }

    @Override
    public int getId() {
        return DRAGON_EGG;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 45;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.OBSIDIAN_BLOCK_COLOR;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) {
            this.teleport();
        }
        return super.onUpdate(type);
    }

    public void teleport() {
        for (int i = 0; i < 1000; ++i) {
            Block t = this.getLevel().getBlock(this.add(ThreadLocalRandom.current().nextInt(-16, 16), ThreadLocalRandom.current().nextInt(-16, 16), ThreadLocalRandom.current().nextInt(-16, 16)));
            if (t.getId() == AIR) {
                for (int j = 0; j < 128; ++j) {
                    double f = ThreadLocalRandom.current().nextDouble();
                    double x = t.getX() + (this.getX() - t.getX()) * f + ThreadLocalRandom.current().nextDouble();
                    double y = t.getY() + (this.getY() - t.getY()) * f + ThreadLocalRandom.current().nextDouble() - 0.5;
                    double z = t.getZ() + (this.getZ() - t.getZ()) * f + ThreadLocalRandom.current().nextDouble();
                    this.getLevel().addParticle(new PortalParticle(new Vector3(x, y, z)));
                }
                this.getLevel().setBlock(this, get(AIR), true);
                this.getLevel().setBlock(t, this, true);
                return;
            }
        }
    }
}
