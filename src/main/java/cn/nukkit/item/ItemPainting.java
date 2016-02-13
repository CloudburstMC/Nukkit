package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPainting extends Item {

    public ItemPainting() {
        this(0, 1);
    }

    public ItemPainting(Integer meta) {
        this(meta, 1);
    }

    public ItemPainting(Integer meta, int count) {
        super(PAINTING, 0, count, "Painting");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        FullChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        if (!target.isTransparent() && face > 1 && !block.isSolid()) {
            int[] faces = {2, 0, 1, 3};

            CompoundTag nbt = new CompoundTag()
                    .putByte("Direction", faces[face - 2])
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("0", target.x))
                            .add(new DoubleTag("1", target.y))
                            .add(new DoubleTag("2", target.z)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("0", 0))
                            .add(new DoubleTag("1", 0))
                            .add(new DoubleTag("2", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("0", faces[face - 2] * 90))
                            .add(new FloatTag("1", 0)));

            EntityPainting entity = new EntityPainting(chunk, nbt);

            if (player.isSurvival()) {
                --this.count;
            }
            entity.spawnToAll();

            return true;
        }

        return false;
    }

}
