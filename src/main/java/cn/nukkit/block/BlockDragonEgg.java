package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.AIR;

public class BlockDragonEgg extends BlockFallable {

    public BlockDragonEgg(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
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
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 1000; ++i) {
            Block t = this.getLevel().getBlock(this.getPosition().add(random.nextInt(-16, 16),
                    random.nextInt(-16, 16), random.nextInt(-16, 16)));
            if (t.getId() == AIR) {
                int diffX = this.getX() - t.getX();
                int diffY = this.getY() - t.getY();
                int diffZ = this.getZ() - t.getZ();
                LevelEventPacket pk = new LevelEventPacket();
                pk.setType(LevelEventType.DRAGON_EGG_TELEPORT);
                pk.setData((((((Math.abs(diffX) << 16) | (Math.abs(diffY) << 8)) | Math.abs(diffZ)) |
                        ((diffX < 0 ? 1 : 0) << 24)) | ((diffY < 0 ? 1 : 0) << 25)) | ((diffZ < 0 ? 1 : 0) << 26));
                pk.setPosition(this.getPosition().toFloat().add(0.5, 0.5, 0.5));
                this.getLevel().addChunkPacket(this.getPosition(), pk);
                this.getLevel().setBlock(this.getPosition(), get(AIR), true);
                this.getLevel().setBlock(t.getPosition(), this, true);
                return;
            }
        }
    }
}
