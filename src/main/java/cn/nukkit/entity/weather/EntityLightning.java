package cn.nukkit.entity.weather;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created by boybook on 2016/2/27.
 */
public class EntityLightning extends Entity implements EntityLightningStrike {

    public static final int NETWORK_ID = 93;

    protected boolean isEffect = true;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityLightning(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealth(4);
        this.setMaxHealth(4);
    }

    public boolean isEffect() {
        return this.isEffect;
    }

    public void setEffect(boolean e) {
        this.isEffect = e;
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.type = EntityLightning.NETWORK_ID;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }

    @Override
    public void attack(EntityDamageEvent source) {
        source.setDamage(0);
        super.attack(source);
        if (source.isCancelled()) return;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.justCreated) {
            if (this.isEffect()) {
                for (Entity e : this.level.getNearbyEntities(this.boundingBox.grow(6, 12, 6), this)) {
                    if (e instanceof EntityLiving) {
                        e.attack(new EntityDamageByEntityEvent(this, e, EntityDamageEvent.CAUSE_LIGHTNING, 5));
                        e.setOnFire(5);  //how long?
                        //Creeper
                        if (e instanceof EntityCreeper) {
                            if (!((EntityCreeper) e).isPowered()) {
                                ((EntityCreeper) e).setPowered(this);
                            }
                        }

                        //TODO Pig
                        //TODO Villager
                    } else if (e instanceof EntityItem) {
                        e.kill();
                    }
                }
                if (Server.getInstance().getDifficulty() >= 2) {
                    Block block = this.getLevelBlock();
                    if (block.getId() == 0 || block.getId() == Block.TALL_GRASS) {
                        BlockFire fire = new BlockFire();
                        fire.x = block.x;
                        fire.y = block.y;
                        fire.z = block.z;
                        fire.level = level;
                        this.getLevel().setBlock(fire, fire, true);
                        if (fire.isBlockTopFacingSurfaceSolid(fire.getSide(Vector3.SIDE_DOWN)) || fire.canNeighborBurn()) {

                            BlockIgniteEvent e = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                            getServer().getPluginManager().callEvent(e);

                            if (!e.isCancelled()) {
                                level.setBlock(fire, fire, true);
                                level.scheduleUpdate(fire, fire.tickRate() + level.rand.nextInt(10));
                            }
                        }
                    }
                }
            }

        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = true;
        this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (this.age >= 1) {
                this.close();
                hasUpdate = false;
            }
        }

        return hasUpdate;
    }


}
