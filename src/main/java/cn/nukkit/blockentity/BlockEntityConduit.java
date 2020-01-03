package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

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
            } else if (!activeBeforeUpdate && active) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_ACTIVATE);
            }
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
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

    private int scanMainStructure() {
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

    private int scanExtraStructure() {
        int validBlocks = 0;
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

                        int blockId = level.getBlockIdAt(x + ix, y + iy, z + iz);
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z + iz, new BlockDiamond(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++;
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

        int validBlocks = scanMainStructure();
        if (validBlocks < 16) {
            this.validBlocks = 0;
            return false;
        }

        validBlocks += scanExtraStructure();

        this.validBlocks = validBlocks;

        return true;
    }

    public int getValidBlocks() {
        return validBlocks;
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
