package cn.nukkit.entity.impl.misc;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFire;
import cn.nukkit.block.BlockIds;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Location;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.math.AxisAlignedBB;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.TALL_GRASS;

/**
 * Created by boybook on 2016/2/27.
 */
public class EntityLightningBolt extends BaseEntity implements LightningBolt {

    public int state;
    public int liveTime;
    protected boolean isEffect = true;

    public EntityLightningBolt(EntityType<LightningBolt> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealth(4);
        this.setMaxHealth(4);

        this.state = 2;
        this.liveTime = ThreadLocalRandom.current().nextInt(3) + 1;

        if (isEffect && this.level.getGameRules().get(GameRules.DO_FIRE_TICK) && (this.server.getDifficulty().ordinal() >= 2)) {
            Block block = this.getLevel().getBlock(this.getPosition());
            if (block.getId() == AIR || block.getId() == TALL_GRASS) {
                BlockFire fire = (BlockFire) Block.get(BlockIds.FIRE);
                fire.setPosition(block.getPosition());
                fire.setLevel(this.level);
                this.getLevel().setBlock(fire.getPosition(), fire, true);
                if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {

                    BlockIgniteEvent e = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                    getServer().getPluginManager().callEvent(e);

                    if (!e.isCancelled()) {
                        level.setBlock(fire.getPosition(), fire, true);
                        level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
                    }
                }
            }
        }
    }

    @Override
    public boolean isEffect() {
        return this.isEffect;
    }

    @Override
    public void setEffect(boolean effect) {
        this.isEffect = effect;
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
            this.level.addLevelSoundEvent(this.getPosition(), SoundEvent.THUNDER, -1, EntityTypes.LIGHTNING_BOLT);
            this.level.addLevelSoundEvent(this.getPosition(), SoundEvent.EXPLODE, -1, EntityTypes.LIGHTNING_BOLT);
        }

        this.state--;

        if (this.state < 0) {
            if (this.liveTime == 0) {
                this.close();
                return false;
            } else if (this.state < -ThreadLocalRandom.current().nextInt(10)) {
                this.liveTime--;
                this.state = 1;

                if (this.isEffect && this.level.getGameRules().get(GameRules.DO_FIRE_TICK)) {
                    Block block = this.getLevel().getBlock(this.getPosition());

                    if (block.getId() == AIR || block.getId() == TALL_GRASS) {
                        BlockIgniteEvent e = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                        getServer().getPluginManager().callEvent(e);

                        if (!e.isCancelled()) {
                            Block fire = Block.get(BlockIds.FIRE);
                            this.level.setBlock(block.getPosition(), fire);
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
