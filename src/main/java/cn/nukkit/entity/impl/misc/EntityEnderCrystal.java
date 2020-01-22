package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.EnderCrystal;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Position;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.nbt.tag.CompoundTag;

import static cn.nukkit.entity.data.EntityFlag.SHOWBASE;

/**
 * Created by PetteriM1
 */
public class EntityEnderCrystal extends BaseEntity implements EnderCrystal, EntityExplosive {

    public EntityEnderCrystal(EntityType<EnderCrystal> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            return false;
        }

        if (!super.attack(source)) {
            return false;
        }

        explode();

        return true;
    }

    @Override
    public void explode() {
        Position pos = this.getPosition();
        Explosion explode = new Explosion(pos, 6, this);

        close();

        if (this.level.getGameRules().get(GameRules.MOB_GRIEFING)) {
            explode.explode();
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean showBase() {
        return this.getFlag(SHOWBASE);
    }

    public void setShowBase(boolean value) {
        this.setFlag(SHOWBASE, value);
    }
}
