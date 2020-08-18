package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.Optional;

public class EntityBee extends EntityAnimal {

    public static final int NETWORK_ID = 122;
    
    private int beehiveTimer = 600;
    
    public EntityBee(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7F;
    }

    @Override
    public float getHeight() {
        return 0.6F;
    }

    public boolean getHasNectar() {
        return false;
    }

    public void setHasNectar(boolean hasNectar) {
    
    }
    
    public boolean isAngry() {
        return false;
    }
    
    public void setAngry(boolean angry) {
    
    }
    
    @Override
    public boolean onUpdate(int currentTick) {
        if (--beehiveTimer <= 0) {
            BlockEntityBeehive closestBeehive = null;
            double closestDistance = Double.MAX_VALUE;
            Optional<Block> flower = Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(4, 4, 4), false, true))
                    .filter(block -> block instanceof BlockFlower)
                    .findFirst();
            
            for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5))) {
                if (collisionBlock instanceof BlockBeehive) {
                    BlockEntityBeehive beehive = ((BlockBeehive) collisionBlock).getOrCreateBlockEntity();
                    double distance;
                    if(beehive.getOccupantsCount() < 4 && (distance = beehive.distanceSquared(this)) < closestDistance) {
                        closestBeehive = beehive;
                        closestDistance = distance;
                    }
                }
            }
            
            if (closestBeehive != null) {
                BlockEntityBeehive.Occupant occupant = closestBeehive.addOccupant(this);
                if (flower.isPresent()) {
                    occupant.setTicksLeftToStay(2400);
                    occupant.setHasNectar(true);
                }
            }
        }
        return true;
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }

    public void nectarDelivered(BlockEntityBeehive blockEntityBeehive) {

    }
    
    public void leftBeehive(BlockEntityBeehive blockEntityBeehive) {
    
    }
    
    public void setAngry(Player player) {
    
    }
}
