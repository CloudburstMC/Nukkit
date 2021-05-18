package cn.nukkit.block;

import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.network.protocol.WorldEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.world.World;

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
        if (type == World.BLOCK_UPDATE_TOUCH) {
            this.teleport();
        }
        return super.onUpdate(type);
    }

    public void teleport() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 1000; ++i) {
            Block to = this.getWorld().getBlock(this.add(random.nextInt(-16, 16), random.nextInt(-16, 16), random.nextInt(-16, 16)));
            if (to.getId() == AIR) {
                BlockFromToEvent event = new BlockFromToEvent(this, to);
                this.world.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) return;
                to = event.getTo();

                int diffX = this.getFloorX() - to.getFloorX();
                int diffY = this.getFloorY() - to.getFloorY();
                int diffZ = this.getFloorZ() - to.getFloorZ();
                WorldEventPacket pk = new WorldEventPacket();
                pk.evid = WorldEventPacket.EVENT_PARTICLE_DRAGON_EGG_TELEPORT;
                pk.data = (((((Math.abs(diffX) << 16) | (Math.abs(diffY) << 8)) | Math.abs(diffZ)) | ((diffX < 0 ? 1 : 0) << 24)) | ((diffY < 0 ? 1 : 0) << 25)) | ((diffZ < 0 ? 1 : 0) << 26);
                pk.x = this.getFloorX();
                pk.y = this.getFloorY();
                pk.z = this.getFloorZ();
                this.getWorld().addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
                this.getWorld().setBlock(this, get(AIR), true);
                this.getWorld().setBlock(to, this, true);
                return;
            }
        }
    }
}
