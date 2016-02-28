package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author Box.
 */
public class EntityCreeper extends Entity {
    public static final int NETWORK_ID = 33;

    public static final int POWERED = 19;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityCreeper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    public boolean isPowered() {
        return getDataPropertyBoolean(POWERED);
    }

    public void setPowered(boolean powered) {
        //TODO:call event
        if(powered){
            setDataProperty(new ByteEntityData(POWERED,1));
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (namedTag.contains("powered")) {
            setPowered(namedTag.getBoolean("powered"));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if(isPowered()){
            this.namedTag.putBoolean("powered",true);
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        //TODO:or deleted
        return true;
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = NETWORK_ID;
        pk.eid = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}
