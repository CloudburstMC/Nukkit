package cn.nukkit.entity.passive;

import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityBee extends EntityAnimal {

    public static final int NETWORK_ID = 122;
    private boolean carryingNectar = false;
    private boolean stung = false;
    private String hurtBy;
    private Position hiveLocation;
    private Position flowerLocation;
    private int anger = 0, cropsGrown = 0, hiveTicks = 0, pollinationTicks = 0;

    public EntityBee(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean hasNectar() {
        return this.carryingNectar;
    }

    public void setNectar(boolean hasNectar) {
        this.carryingNectar = hasNectar;
    }

    public boolean hasStung() {
        return stung;
    }

    public void setStung(boolean stung) {
        this.stung = stung;
    }

    public Position getHiveLocation() {
        return hiveLocation;
    }

    public void setHiveLocation(Position hiveLocation) {
        this.hiveLocation = hiveLocation;
    }

    public Position getFlowerLocation() {
        return flowerLocation;
    }

    public void setFlowerLocation(Position flowerLocation) {
        this.flowerLocation = flowerLocation;
    }

    public int getCropsGrown() {
        return this.cropsGrown;
    }

    public void incrementCropsGrown() {
        this.cropsGrown++;
    }

    public String getHurtBy() {
        return this.hurtBy;
    }

    public void setHurtBy(String uuid) {
        this.hurtBy = uuid;
    }

    public boolean isAngry() {
        return this.anger > 0;
    }

    public boolean canEnterHive() {
        return this.hiveTicks <= 0;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public String getName() {
        return "Bee";
    }

    @Override
    public void saveNBT() {
        namedTag.putBoolean("HasNectar", this.carryingNectar);
        namedTag.putBoolean("HasStung", this.stung);
        if(this.hiveLocation != null) {
            namedTag.putCompound("HivePos", new CompoundTag()
                    .putDouble("X",this.hiveLocation.getY())
                    .putDouble("Y", this.hiveLocation.getY())
                    .putDouble("Z", this.hiveLocation.getZ()));
        }
        if(this.flowerLocation != null) {
            namedTag.putCompound("FlowerPos", new CompoundTag()
                    .putDouble("X",this.flowerLocation.getY())
                    .putDouble("Y", this.flowerLocation.getY())
                    .putDouble("Z", this.flowerLocation.getZ()));
        }
        namedTag.putInt("TicksSincePollination", this.pollinationTicks);
        namedTag.putInt("CannotEnterHiveTicks", this.hiveTicks);
        namedTag.putInt("CropsGrownSincePollination", this.pollinationTicks);
        namedTag.putInt("Anger", this.anger);
        if(hurtBy != null) {
            namedTag.putString("HurtBy", this.hurtBy);
        } else {
            namedTag.putString("HurtBy", "");
        }
        super.saveNBT();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        int tickDiff = currentTick - this.lastUpdate;

        if( tickDiff <= 0) { return super.onUpdate(currentTick); }
        if(this.anger > 0 ) {
            if (this.anger >= tickDiff) {
                this.anger = 0;
            } else {
                this.anger -= tickDiff;
            }
        }
        this.pollinationTicks += tickDiff;
        if( this.hiveTicks > 0 ) {
            if(this.hiveTicks >= tickDiff) {
                this.hiveTicks = 0;
            } else {
                this.hiveTicks -= tickDiff;
            }
        }
        return super.onUpdate(currentTick);

    }
}
