package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
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

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
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
                int diffX = this.getFloorX() - t.getFloorX();
                int diffY = this.getFloorY() - t.getFloorY();
                int diffZ = this.getFloorZ() - t.getFloorZ();
                LevelEventPacket pk = new LevelEventPacket();
                pk.evid = LevelEventPacket.EVENT_PARTICLE_DRAGON_EGG_TELEPORT;
                pk.data = (((((Math.abs(diffX) << 16) | (Math.abs(diffY) << 8)) | Math.abs(diffZ)) | ((diffX < 0 ? 1 : 0) << 24)) | ((diffY < 0 ? 1 : 0) << 25)) | ((diffZ < 0 ? 1 : 0) << 26);
                pk.x = this.getFloorX();
                pk.y = this.getFloorY();
                pk.z = this.getFloorZ();
                this.getLevel().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
                this.getLevel().setBlock(this, get(AIR), true);
                this.getLevel().setBlock(t, this, true);
                return;
            }
        }
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
