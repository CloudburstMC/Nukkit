package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.LevelEventPacket;
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
                float distance = (float) (this.distance(t) / 1.5);
                float diffX = (this.getFloorX() - t.getFloorX()) / distance;
                float diffY = (this.getFloorY() - t.getFloorY()) / distance;
                float diffZ = (this.getFloorZ() - t.getFloorZ()) / distance;
                float x = t.getFloorX();
                float y = t.getFloorY();
                float z = t.getFloorZ();
                for (int j = 0; j < Math.ceil(distance); ++j) {
                    LevelEventPacket pk = new LevelEventPacket();
                    pk.evid = 2010;
                    pk.x = x;
                    pk.y = y;
                    pk.z = z;
                    this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
                    x += diffX;
                    y += diffY;
                    z += diffZ;
                }
                this.getLevel().setBlock(this, get(AIR), true);
                this.getLevel().setBlock(t, this, true);
                return;
            }
        }
    }
}
