package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityBee;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;

//Can use the same block entity object for beehives and bee nests as they have the same data
public class BlockEntityBeehive extends BlockEntity {


    private Position flowerPos;
    private ListTag<CompoundTag> bees;
    private static final int MAX_BEES = 3;

    public BlockEntityBeehive(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.bees = new ListTag<>("Bees");
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = this.getBlock().getId();
        return blockID == BlockID.BEE_NEST || blockID == BlockID.BEEHIVE;
    }

    public boolean addBee(EntityBee bee) {
        if(bees.size() >= MAX_BEES) return false;
        this.bees.add(bee.namedTag);
        // despawn here, or let method caller despawn on true return?
        return true;
    }

    public void releaseBee(int index) {
        if( index < 0 || index >= bees.size()) { return; }
        CompoundTag bee = bees.get(index);
        spawnBee(bee);
        this.bees.remove(index);
    }

    public Position getFlowerPos() {
        return flowerPos;
    }

    public void setFlowerPos(Position flowerPos) {
        this.flowerPos = flowerPos;
    }

    // Use this to clear all bees from hive
    public void releaseAllBees(boolean angry) {
        for(CompoundTag bee : this.bees.getAll()) {
            if(angry) {
                bee.putInt("Anger", 300);
            }
            spawnBee(bee);
        }
    }

    private void spawnBee(CompoundTag beeData) {
        beeData.putInt("CannotEnterHiveTicks", 300);
        if(this.flowerPos != null ) {
            beeData.putCompound("FlowerPos", new CompoundTag()
                    .putInt("X", flowerPos.getFloorX())
                    .putInt("Y", flowerPos.getFloorY())
                    .putInt("Z", flowerPos.getFloorZ())
            );
        }
        NukkitRandom random = new NukkitRandom();
        Block block = this.getLevelBlock();
        beeData.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", block.getX() + random.nextSignedDouble()))
                .add(new DoubleTag("", block.getY() + random.nextSignedDouble()))
                .add(new DoubleTag("", block.getZ() + random.nextSignedDouble())));
        EntityBee bee = (EntityBee) Entity.createEntity(EntityBee.NETWORK_ID,block, beeData);
        bee.spawnToAll();
    }

    @Override
    public boolean onUpdate() {
        long diff = this.level.getCurrentTick() - this.lastUpdate;
        for (CompoundTag bee : this.bees.getAll() )
        {
            int hiveTicks = bee.getInt("TicksInHive");
            hiveTicks += diff;
            if(hiveTicks >= bee.getInt("MinOccupationTicks")) {
                //TODO more checks to see if need to release (i.e. is nighttime?)
                releaseBee(bee.getCompound("EntityData"));
                bees.remove(bee);
                continue;
            }
            bee.putInt("TicksInHive", hiveTicks);
        }
        return super.onUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putCompound("FlowerPos", new CompoundTag()
                .putInt("X", this.flowerPos.getFloorX())
                .putInt("Y", this.flowerPos.getFloorY())
                .putInt("Z", this.flowerPos.getFloorZ()));
        this.namedTag.putList(this.bees);
    }

}
