package cn.nukkit.entity.weather;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by boybook on 2016/2/27.
 */
public class EntityLightning extends Entity implements EntityLightningStrike {

    public static final int NETWORK_ID = 93;

    protected boolean isEffect = true;

    public int state;
    public int liveTime;


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

        this.state = 2;
        this.liveTime = ThreadLocalRandom.current().nextInt(3) + 1;

        if (isEffect && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) && (this.server.getDifficulty() >= 2)) {
            Block block = this.getLevelBlock();
            if (block.getId() == 0 || block.getId() == Block.TALL_GRASS) {
                BlockFire fire = new BlockFire();
                fire.x = block.x;
                fire.y = block.y;
                fire.z = block.z;
                fire.level = level;
                this.getLevel().setBlock(fire, fire, true);
                if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {

                    BlockIgniteEvent e = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                    getServer().getPluginManager().callEvent(e);

                    if (!e.isCancelled()) {
                        level.setBlock(fire, fire, true);
                        level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
                    }
                }
            }
        }
    }

    public boolean isEffect() {
        return this.isEffect;
    }

    public void setEffect(boolean e) {
        this.isEffect = e;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        //false?
        source.setDamage(0);
        return super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        this.entityBaseTick(tickDiff);

        if (this.state == 2) {
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_THUNDER, 93, -1);
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_EXPLODE, 93, -1);
        }

        this.state--;

        if (this.state < 0) {
            if (this.liveTime == 0) {
                this.close();
                return false;
            } else if (this.state < -ThreadLocalRandom.current().nextInt(10)) {
                this.liveTime--;
                this.state = 1;

                if (this.isEffect && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
                    Block block = this.getLevelBlock();

                    if (block.getId() == Block.AIR || block.getId() == Block.TALL_GRASS) {
                        BlockIgniteEvent e = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                        getServer().getPluginManager().callEvent(e);

                        if (!e.isCancelled()) {
                            Block fire = new BlockFire();
                            this.level.setBlock(block, fire);
                            this.getLevel().scheduleUpdate(fire, fire.tickRate());
                        }
                    }
                }
            }
        }

        if (this.state >= 0) {
            if (this.isEffect) {
                AxisAlignedBB bb = getBoundingBox().grow(3, 3, 3);
                bb.setMaxX(bb.getMaxX() + 6);

                for (Entity entity : this.level.getCollidingEntities(bb, this)) {
                    entity.onStruckByLightning(this);
                }
            }
        }

        return true;
    }


}
