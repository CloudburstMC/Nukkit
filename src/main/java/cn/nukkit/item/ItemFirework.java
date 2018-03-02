package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CreeperFace
 */
public class ItemFirework extends Item {

    public ItemFirework() {
        this(0);
    }

    public ItemFirework(Integer meta) {
        this(meta, 1);
    }

    public ItemFirework(Integer meta, int count) {
        super(FIREWORKS, meta, count, "Fireworks");

        if (!hasCompoundTag() || !this.getNamedTag().contains("Fireworks")) {
            CompoundTag tag = getNamedTag();
            if (tag == null) {
                tag = new CompoundTag();
            }

            tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                    .putList(new ListTag<CompoundTag>("Explosions"))
                    .putByte("Flight", 1)
            );

            this.setNamedTag(tag);
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (block.canPassThrough()) {
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("", block.x + 0.5))
                            .add(new DoubleTag("", block.y + 0.5))
                            .add(new DoubleTag("", block.z + 0.5)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)))
                    .putCompound("FireworkItem", NBTIO.putItemHelper(this));

            EntityFirework entity = new EntityFirework(level.getChunk(block.getFloorX() >> 4,
                    block.getFloorZ() >> 4), nbt);
            entity.spawnToAll();
            return true;
        }

        return false;
    }

    public void addExplosion(FireworkExplosion explosion) {
        List<DyeColor> colors = explosion.getColors();
        List<DyeColor> fades = explosion.getFades();

        if (colors.isEmpty()) {
            return;
        }
        byte[] clrs = new byte[colors.size()];
        for (int i = 0; i < clrs.length; i++) {
            clrs[i] = (byte) colors.get(i).getDyeData();
        }

        byte[] fds = new byte[fades.size()];
        for (int i = 0; i < fds.length; i++) {
            fds[i] = (byte) fades.get(i).getDyeData();
        }


        ListTag<CompoundTag> explosions = this.getNamedTag().getCompound("Fireworks").getList("Explosions", CompoundTag.class);
        CompoundTag tag = new CompoundTag()
                .putByteArray("FireworkColor", clrs)
                .putByteArray("FireworkFade", fds)
                .putBoolean("FireworkFlicker", explosion.flicker)
                .putBoolean("FireworkTrail", explosion.trail)
                .putByte("FireworkType", explosion.type.ordinal());

        explosions.add(tag);
        encodeCompoundTag();
    }

    public void clearExplosions() {
        this.getNamedTag().getCompound("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions"));

        encodeCompoundTag();
    }

    public static class FireworkExplosion {

        private List<DyeColor> colors = new ArrayList<>();
        public List<DyeColor> getColors(){
            return this.colors;
        }

        private List<DyeColor> fades = new ArrayList<>();
        public List<DyeColor> getFades() {
            return this.fades;
        }

        private boolean flicker;
        public boolean hasFlicker() {
            return this.flicker;
        }

        private boolean trail;
        public boolean hasTrail() {
            return this.trail;
        }
        private ExplosionType type;
        public ExplosionType getType() {
            return this.type;
        }

        public FireworkExplosion setFlicker(boolean flicker) {
            this.flicker = flicker;
            return this;
        }

        public FireworkExplosion setTrail(boolean trail) {
            this.trail = trail;
            return this;
        }

        public FireworkExplosion type(ExplosionType type) {
            this.type = type;
            return this;
        }

        public FireworkExplosion addColor(DyeColor color) {
            colors.add(color);
            return this;
        }

        public FireworkExplosion addFade(DyeColor fade) {
            fades.add(fade);
            return this;
        }

        public enum ExplosionType {
            SMALL_BALL,
            LARGE_BALL,
            STAR_SHAPED,
            CREEPER_SHAPED,
            BURST
        }

    }

}