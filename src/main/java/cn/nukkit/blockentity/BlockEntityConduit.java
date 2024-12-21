package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityConduit extends BlockEntitySpawnable {

    @Getter
    private boolean active;
    @Setter
    @Getter
    private long target = -1;
    private int frameBlocks;

    public BlockEntityConduit(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == Block.CONDUIT;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.CONDUIT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("Active", this.active)
                .putLong("Target", this.target);
    }

    @Override
    public void setDirty() {
        super.setDirty();
        this.spawnToAll();
    }

    // Adapted from https://github.com/PowerNukkit/PowerNukkit/blob/master/src/main/java/cn/nukkit/blockentity/BlockEntityConduit.java

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        if (level.getCurrentTick() % 40 != 0) {
            return true;
        }

        boolean activeBefore = this.active;
        this.active = checkStructure();

        if (activeBefore != this.active) {
            this.spawnToAll();

            if (activeBefore && !this.active) {
                this.target = -1;

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CONDUIT_DEACTIVATE);
            } else if (!activeBefore && this.active) {
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CONDUIT_ACTIVATE);
            }
        }

        if (this.active) {
            attackMob();
            addEffectToPlayers();
        }

        return true;
    }

    private void addEffectToPlayers() {
        int radius = getEffectRadius();
        if (radius <= 0) {
            return;
        }

        Vector2 conduit = new Vector2(x, z);
        int radiusSquared = radius * radius;

        level.getPlayers().values().stream()
                .filter(this::canAffect)
                .filter(p -> conduit.distanceSquared(p.x, p.z) <= radiusSquared)
                .forEach(p -> p.addEffect(Effect.getEffect(Effect.CONDUIT_POWER).setDuration(260).setAmbient(true), EntityPotionEffectEvent.Cause.CONDUIT));
    }

    private void attackMob() {
        int radius = getAttackRadius();
        if (radius <= 0) {
            return;
        }

        boolean updated = false;

        Entity targetEntity = level.getEntity(this.target);
        if (targetEntity != null && !canAttack(targetEntity)) {
            targetEntity = null;
            this.target = -1;
            updated = true;
        }

        if (targetEntity == null) {
            Entity[] mobs = Arrays.stream(level.getCollidingEntities(new SimpleAxisAlignedBB(x - radius, y - radius, z - radius, x + 1 + radius, y + 1 + radius, z + 1 + radius)))
                    .filter(this::canAttack)
                    .toArray(Entity[]::new);

            if (mobs.length == 0) {
                if (updated) {
                    this.spawnToAll();
                }
                return;
            }

            targetEntity = mobs[ThreadLocalRandom.current().nextInt(mobs.length)];
            this.target = targetEntity.getId();
            updated = true;
        }

        if (!targetEntity.attack(new EntityDamageByBlockEvent(this.getLevelBlock(), targetEntity, EntityDamageEvent.DamageCause.MAGIC, 4))) {
            this.target = -1;
            updated = true;
        }

        if (updated) {
            this.spawnToAll();
        }
    }

    private boolean canAttack(Entity target) {
        return target instanceof EntityMob && canAffect(target);
    }

    private boolean canAffect(Entity target) {
        return target.isInsideOfWater() || (level.isRaining() && target.canSeeSky());
    }

    private boolean checkWater() {
        int tileX = (int) this.x;
        int tileY = (int) this.y;
        int tileZ = (int) this.z;

        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++) {
                    Block block = level.getBlock(this.chunk, tileX + xx, tileY + yy, tileZ + zz, BlockLayer.NORMAL, false);
                    if (!Block.isWater(block.getId())) {
                        block = level.getBlock(this.chunk, tileX + xx, tileY + yy, tileZ + zz, BlockLayer.WATERLOGGED, false);
                        if (!Block.isWater(block.getId())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private int checkFrame() {
        int tileX = (int) this.x;
        int tileY = (int) this.y;
        int tileZ = (int) this.z;
        int frameBlocks = 0;

        for (int yy = -2; yy <= 2; yy++) {
            if (yy == 0) {
                for (int xx = -2; xx <= 2; xx++) {
                    for (int zz = -2; zz <= 2; zz++) {
                        if (Math.abs(zz) != 2 && Math.abs(xx) != 2) {
                            continue;
                        }

                        if (getBlockIdIfLoaded(tileX + xx, tileY, tileZ + zz) == BlockID.PRISMARINE) {
                            frameBlocks++;
                        }
                    }
                }
            } else {
                int absIY = Math.abs(yy);

                for (int xx = -2; xx <= 2; xx++) {
                    if (absIY != 2 && xx == 0) {
                        continue;
                    }

                    if (absIY == 2 || Math.abs(xx) == 2) {
                        if (getBlockIdIfLoaded(tileX + xx, tileY + yy, tileZ) == BlockID.PRISMARINE) {
                            frameBlocks++;
                        }
                    }
                }

                for (int zz = -2; zz <= 2; zz++) {
                    if (absIY != 2 && zz == 0) {
                        continue;
                    }

                    if (absIY == 2 && zz != 0 || Math.abs(zz) == 2) {
                        if (getBlockIdIfLoaded(tileX, tileY + yy, tileZ + zz) == BlockID.PRISMARINE) {
                            frameBlocks++;
                        }
                    }
                }
            }
        }

        return frameBlocks;
    }

    private boolean checkStructure() {
        if (!checkWater()) {
            this.frameBlocks = 0;
            return false;
        }

        int frameBlocks = checkFrame();
        if (frameBlocks < 16) {
            this.frameBlocks = 0;
            return false;
        }

        this.frameBlocks = frameBlocks;
        return true;
    }

    public int getEffectRadius() {
        return (this.frameBlocks / 7) * 16;
    }

    public int getAttackRadius() {
        return this.frameBlocks >= 42 ? 8 : 0;
    }
}
