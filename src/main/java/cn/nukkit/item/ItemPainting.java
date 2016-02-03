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

import java.util.Random;

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
            int[] faces = {1, 3, 0, 2};
            Motive[] motives = new Motive[]{
                    new Motive("Kebab", 1, 1),
                    new Motive("Aztec", 1, 1),
                    new Motive("Alban", 1, 1),
                    new Motive("Aztec2", 1, 1),
                    new Motive("Bomb", 1, 1),
                    new Motive("Plant", 1, 1),
                    new Motive("Wasteland", 1, 1),
                    new Motive("Wanderer", 1, 2),
                    new Motive("Graham", 1, 2),
                    new Motive("Pool", 2, 1),
                    new Motive("Courbet", 2, 1),
                    new Motive("Sunset", 2, 1),
                    new Motive("Sea", 2, 1),
                    new Motive("Creebet", 2, 1),
                    new Motive("Match", 2, 2),
                    new Motive("Bust", 2, 2),
                    new Motive("Stage", 2, 2),
                    new Motive("Void", 2, 2),
                    new Motive("SkullAndRoses", 2, 2),
                    //new Motive("Wither", 2, 2),
                    new Motive("Fighters", 4, 2),
                    new Motive("Skeleton", 4, 3),
                    new Motive("DonkeyKong", 4, 3),
                    new Motive("Pointer", 4, 4),
                    new Motive("Pigscene", 4, 4),
                    new Motive("Flaming Skull", 4, 4)
            };

            Motive motive = motives[new Random().nextInt(motives.length - 1)];

            CompoundTag nbt = new CompoundTag()
                    .putString("Motive", motive.title)
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

    static class Motive {
        public String title;
        public int width;
        public int height;

        public Motive(String title, int width, int height) {
            this.title = title;
            this.width = width;
            this.height = height;
        }
    }
}
