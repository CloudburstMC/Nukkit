package cn.nukkit.block;

import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;


/**
 * author: rcsuperman
 * Nukkit Project
 */
public abstract class BlockFallable extends BlockSolid {

    protected BlockFallable() {};

    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            if (down.getId() == AIR || down instanceof BlockLiquid) {
                this.level.setBlock(this, Block.get(Block.AIR), true, true);
                CompoundTag nbt = new CompoundTag()
                        .putList(new ListTag<DoubleTag>("Pos")
                                .add(new DoubleTag("", this.x + 0.5))
                                .add(new DoubleTag("", this.y))
                                .add(new DoubleTag("", this.z + 0.5)))
                        .putList(new ListTag<DoubleTag>("Motion")
                                .add(new DoubleTag("", 0))
                                .add(new DoubleTag("", 0))
                                .add(new DoubleTag("", 0)))

                        .putList(new ListTag<FloatTag>("Rotation")
                                .add(new FloatTag("", 0))
                                .add(new FloatTag("", 0)))
                        .putInt("TileID", this.getId())
                        .putByte("Data", this.getDamage());

                EntityFallingBlock fall = new EntityFallingBlock(this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);

                fall.spawnToAll();
            }
        }
        return type;
    }
}
