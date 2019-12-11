package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.passive.EntityBee;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

//Can use the same block entity object for beehives and bee nests as they have the same data
public class BlockEntityBeehive extends BlockEntity {


    private Position flowerPos;
    private ListTag<CompoundTag> bees;

    public BlockEntityBeehive(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = this.getBlock().getId();
        return blockID == BlockID.BEE_NEST || blockID == BlockID.BEEHIVE;
    }

    public void addBee(EntityBee bee) {
        // TODO
    }

    public void releaseBee(CompoundTag beeData) {
        // TODO
    }

    public Position getFlowerPos() {
        return flowerPos;
    }

    public void setFlowerPos(Position flowerPos) {
        this.flowerPos = flowerPos;
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
