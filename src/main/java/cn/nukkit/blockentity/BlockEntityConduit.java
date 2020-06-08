package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.block.ConduitActivateEvent;
import cn.nukkit.event.block.ConduitDeactivateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.type.SnowyBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityConduit extends BlockEntitySpawnable {
    public static IntSet VALID_STRUCTURE_BLOCKS = new IntOpenHashSet(new int[]{
            BlockID.PRISMARINE,
            BlockID.SEA_LANTERN
    });

    private Entity targetEntity;
    private long target;
    private boolean active;
    private int validBlocks;

    public BlockEntityConduit(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        validBlocks = -1;
        if (!namedTag.contains("Target")) {
            namedTag.putLong("Target", -1);
            target = -1;
            targetEntity = null;
        } else {
            target = namedTag.getLong("Target");
        }

        active = namedTag.getBoolean("Active");

        super.initBlockEntity();
        this.scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        Entity targetEntity = this.targetEntity;
        namedTag.putLong("Target", targetEntity != null? targetEntity.getId() : -1);
        namedTag.putBoolean("Active", active);
        super.saveNBT();
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Override
    public boolean onUpdate() {
        if (closed) {
            return false;
        }

        boolean activeBeforeUpdate = active;
        Entity targetBeforeUpdate = targetEntity;
        if (validBlocks == -1) {
            active = scanStructure();
        }

        if (level.getCurrentTick() % 20 == 0) {
            active = scanStructure();
        }

        if (target != -1) {
            targetEntity = getLevel().getEntity(target);
            target = -1;
        }

        if (activeBeforeUpdate != active || targetBeforeUpdate != targetEntity) {
            this.spawnToAll();
            if (activeBeforeUpdate && !active) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_DEACTIVATE);
                level.getServer().getPluginManager().callEvent(new ConduitDeactivateEvent(getBlock()));
            } else if (!activeBeforeUpdate && active) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_ACTIVATE);
                level.getServer().getPluginManager().callEvent(new ConduitActivateEvent(getBlock()));
            }
        }

        if (!active) {
            targetEntity = null;
            target = -1;
        } else if (level.getCurrentTick() % 40 == 0) {
            attackMob();
            addEffectToPlayers();
        }

        return true;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.CONDUIT;
    }

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    // Client validates the structure, so if we set it to an invalid state it would cause a visual desync
    /*public void setActive(boolean active) {
        this.active = active;
    }*/

    public boolean isActive() {
        return active;
    }

    public void addEffectToPlayers() {
        int radius = getPlayerRadius();
        if (radius <= 0) {
            return;
        }
        final int radiusSquared = radius * radius;

        Vector2 conduitPos = new Vector2(x, z);

        this.getLevel().getPlayers().values().stream()
                .filter(this::canAffect)
                .filter(p -> conduitPos.distanceSquared(p.x, p.z) <= radiusSquared)
                .forEach(p -> p.addEffect(Effect.getEffect(Effect.CONDUIT_POWER)
                                .setDuration(260)
                                .setVisible(true)
                                .setAmplifier(0)
                                .setAmbient(true)
                    )
                );
    }

    public void attackMob() {
        int radius = getAttackRadius();
        if (radius <= 0) {
            return;
        }

        boolean updated = false;

        Entity target = this.targetEntity;
        if (target != null && !canAttack(target)) {
            target = null;
            updated = true;
            this.targetEntity = null;
            this.target = -1;
        }

        if (target == null) {
            Entity[] mobs = Arrays.stream(level.getCollidingEntities(new SimpleAxisAlignedBB(x - radius, y - radius, z - radius, x + 1 + radius, y + 1 + radius, z + 1 + radius)))
                    .filter(this::canAttack)
                    .toArray(Entity[]::new);
            if (mobs.length == 0) {
                if (updated) {
                    spawnToAll();
                }
                return;
            }
            target = mobs[ThreadLocalRandom.current().nextInt(mobs.length)];
            this.targetEntity = target;
            updated = true;
        }

        if (!target.attack(new EntityDamageByBlockEvent(getBlock(), target, EntityDamageEvent.DamageCause.MAGIC, 4))) {
            this.targetEntity = null;
            updated = true;
        }

        if (updated) {
            spawnToAll();
        }
    }

    public boolean canAttack(Entity target) {
        return target instanceof EntityMob && canAffect(target);
    }

    public boolean canAffect(Entity target) {
        return target.isTouchingWater()
                || target.level.isRaining() && target.level.canBlockSeeSky(target)
                        && !(Biome.getBiome(target.level.getBiomeId(target.getFloorX(), target.getFloorZ())) instanceof SnowyBiome);
    }

    private boolean scanWater() {
        int x = getFloorX();
        int y = getFloorY();
        int z = getFloorZ();
        for (int ix = -1; ix <= 1; ix++) {
            for (int iz = -1; iz <= 1; iz++) {
                for (int iy = -1; iy <= 1; iy++) {
                    int blockId = this.getLevel().getBlockIdAt(x + ix, y + iy, z + iz, 0);
                    if (blockId != Block.WATER && blockId != Block.STILL_WATER) {
                        blockId = this.getLevel().getBlockIdAt(x + ix, y + iy, z + iz, 1);
                        if (blockId != Block.WATER && blockId != Block.STILL_WATER) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private int scanFrame() {
        int validBlocks = 0;
        int x = getFloorX();
        int y = getFloorY();
        int z = getFloorZ();
        for (int iy = -2; iy <= 2; iy++) {
            if (iy == 0) {
                for (int ix = -2; ix <= 2; ix++) {
                    for (int iz = -2; iz <= 2; iz++) {
                        if (Math.abs(iz) != 2 && Math.abs(ix) != 2) {
                            continue;
                        }

                        int blockId = level.getBlockIdAt(x + ix, y, z + iz);
                        //validBlocks++;
                        //level.setBlock(x + ix, y, z + iz, new BlockPlanks(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
                        }
                    }
                }
            } else {
                int absIY = Math.abs(iy);
                for (int ix = -2; ix <= 2; ix++) {
                    if (absIY != 2 && ix == 0) {
                        continue;
                    }

                    if (absIY == 2 || Math.abs(ix) == 2) {
                        int blockId = level.getBlockIdAt(x + ix, y + iy, z);
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z, new BlockWood(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
                        }
                    }
                }

                for (int iz = -2; iz <= 2; iz++) {
                    if (absIY != 2 && iz == 0) {
                        continue;
                    }

                    if (absIY == 2 && iz != 0 || Math.abs(iz) == 2) {
                        int blockId = level.getBlockIdAt(x, y + iy, z + iz);
                        //validBlocks++;
                        //level.setBlock(x, y + iy, z + iz, new BlockWood(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
                        }
                    }
                }
            }
        }

        return validBlocks;
    }

    public List<Block> scanEdgeBlock() {
        List<Block> validBlocks = new ArrayList<>();
        int x = getFloorX();
        int y = getFloorY();
        int z = getFloorZ();
        for (int iy = -2; iy <= 2; iy++) {
            if (iy != 0) {
                for (int ix = -2; ix <= 2; ix++) {
                    for (int iz = -2; iz <= 2; iz++) {
                        if (Math.abs(iy) != 2 && Math.abs(iz) < 2 && Math.abs(ix) < 2) {
                            continue;
                        }

                        if (ix == 0 || iz == 0) {
                            continue;
                        }

                        Block block = level.getBlock(x + ix, y + iy, z + iz);
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z + iz, new BlockDiamond(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(block.getId())) {
                            validBlocks.add(block);
                        }
                    }
                }
            }
        }

        return validBlocks;
    }

    public boolean scanStructure() {
        if(!scanWater()) {
            this.validBlocks = 0;
            return false;
        }

        int validBlocks = scanFrame();
        if (validBlocks < 16) {
            this.validBlocks = 0;
            return false;
        }

        this.validBlocks = validBlocks;

        return true;
    }

    public int getValidBlocks() {
        return validBlocks;
    }

    public int getPlayerRadius() {
        int radius = validBlocks / 7;
        return radius * 16;
    }

    public int getAttackRadius() {
        if (validBlocks >= 42) {
            return 8;
        } else {
            return 0;
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.CONDUIT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("Active", this.active)
                .putBoolean("isMovable", isMovable());
        Entity targetEntity = this.targetEntity;
        tag.putLong("Target", targetEntity != null? targetEntity.getId() : -1);
        return tag;
    }
}
