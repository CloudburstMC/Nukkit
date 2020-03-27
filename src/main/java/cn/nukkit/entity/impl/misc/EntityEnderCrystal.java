package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.EnderCrystal;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Location;
import cn.nukkit.level.gamerule.GameRules;
import com.nukkitx.protocol.bedrock.data.EntityFlag;

/**
 * Created by PetteriM1
 */
public class EntityEnderCrystal extends BaseEntity implements EnderCrystal, EntityExplosive {

    public EntityEnderCrystal(EntityType<EnderCrystal> type, Location location) {
        super(type, location);
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
        Explosion explode = new Explosion(this.getLevel(), this.getPosition(), 6, this);

        this.close();

        if (this.level.getGameRules().get(GameRules.MOB_GRIEFING)) {
            explode.explode();
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean showBase() {
        return this.data.getFlag(EntityFlag.SHOW_BOTTOM);
    }

    public void setShowBase(boolean value) {
        this.data.setFlag(EntityFlag.SHOW_BOTTOM, value);
    }
}
