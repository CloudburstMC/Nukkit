package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
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

                CompoundTag ex = new CompoundTag()
                        .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.BLACK.getDyeData()})
                        .putByteArray("FireworkFade", new byte[]{})
                        .putBoolean("FireworkFlicker", false)
                        .putBoolean("FireworkTrail", false)
                        .putByte("FireworkType", FireworkExplosion.ExplosionType.CREEPER_SHAPED.ordinal());

                tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                        .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                        .putByte("Flight", 1)
                );

                this.setNamedTag(tag);
            }
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (block.canPassThrough()) {
            this.spawnFirework(level, block);

            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.getInventory().getChestplate() instanceof ItemElytra && player.isGliding()) {
            this.spawnFirework(player.getLevel(), player);

            if (!player.isCreative()) {
                this.count--;
            }

            player.setMotion(new Vector3(
                    -Math.sin(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch)) * 2,
                    -Math.sin(Math.toRadians(player.pitch)) * 2,
                    Math.cos(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch)) * 2));

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
    }

    public void clearExplosions() {
        this.getNamedTag().getCompound("Fireworks").putList(new ListTag<CompoundTag>("Explosions"));
    }

    private void spawnFirework(Level level, Vector3 pos) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", pos.x + 0.5))
                        .add(new DoubleTag("", pos.y + 0.5))
                        .add(new DoubleTag("", pos.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putCompound("FireworkItem", NBTIO.putItemHelper(this));

        EntityFirework entity = new EntityFirework(level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4), nbt);
        entity.spawnToAll();
    }

    public static class FireworkExplosion {

        private List<DyeColor> colors = new ArrayList<>();
        private List<DyeColor> fades = new ArrayList<>();
        private boolean flicker = false;
        private boolean trail = false;
        private ExplosionType type = ExplosionType.CREEPER_SHAPED;

        public List<DyeColor> getColors() {
            return this.colors;
        }

        public List<DyeColor> getFades() {
            return this.fades;
        }

        public boolean hasFlicker() {
            return this.flicker;
        }

        public boolean hasTrail() {
            return this.trail;
        }

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
