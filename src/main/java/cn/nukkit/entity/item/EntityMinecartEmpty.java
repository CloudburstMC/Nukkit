package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartEmpty extends EntityVehicle {

    public static final int NETWORK_ID = 84;

    public static final int DATA_VEHICLE_DISPLAY_BLOCK = 20;
    public static final int DATA_VEHICLE_DISPLAY_DATA = 20;
    public static final int DATA_VEHICLE_DISPLAY_OFFSET = 21;
    public static final int DATA_VEHICLE_CUSTOM_DISPLAY = 22;

    // TODO: 2016/1/30 check if these numbers correct
    @Override
    public float getHeight() {
        return 0.7f;
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    protected float getDrag() {
        return 0.1f;
    }

    @Override
    protected float getGravity() {
        return 0.5f;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityMinecartEmpty(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        //?
        super.initEntity();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // TODO: 2016/1/30 run run run!
        // TODO: 2016/1/30 split to onXXXRailPass, such as, protected void onActivatorRailPass(...)
        return super.onUpdate(currentTick);
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.eid = this.getId();
        pk.type = EntityMinecartEmpty.NETWORK_ID;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.yaw = 0;
        pk.pitch = 0;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }

    @Override
    public void attack(float damage, EntityDamageEvent source) {
        super.attack(damage, source);
        if (source.isCancelled()) return;

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.id;
        pk.event = EntityEventPacket.HURT_ANIMATION;
        for (Player aPlayer : this.getLevel().getPlayers().values()) {
            aPlayer.dataPacket(pk);
        }
    }

}
