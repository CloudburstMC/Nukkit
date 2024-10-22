package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MinecartType;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartEmpty extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 84;

    private static final Vector3f RIDER_OFFSET = new Vector3f(0f, -0.35f);
    private static final Vector3f RIDER_PLAYER_OFFSET = new Vector3f(0f, 0.525f);

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityMinecartEmpty(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setName("Minecart");
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(0);
    }
    
    @Override
    public boolean isRideable() {
        return true;
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {
        if (flag && this.getHealth() > 15
                && this.attack(new EntityDamageByBlockEvent(this.level.getBlock(x, y, z), this, EntityDamageEvent.DamageCause.CONTACT, 1))
                && !this.passengers.isEmpty()) {
            this.dismountEntity(this.getPassenger());
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (this.passengers.isEmpty()) {
            if (!(entity.riding != null || !(entity instanceof EntityLiving) || entity instanceof Player || entity instanceof EntityWaterAnimal)) {
                this.mountEntity(entity);
            }
        }

        super.applyEntityCollision(entity);
    }

    @Override
    public String getInteractButtonText() {
        return this.passengers.isEmpty() ? "action.interact.ride.minecart" : "";
    }

    @Override
    public Vector3f getMountedOffset(Entity entity) {
        return entity instanceof Player ? RIDER_PLAYER_OFFSET : RIDER_OFFSET;
    }
}
