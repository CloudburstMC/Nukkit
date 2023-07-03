package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by PetteriM1
 */
public class EntityEndCrystal extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = 71;

    protected boolean detonated = false;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityEndCrystal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("ShowBottom")) {
            this.setShowBase(this.namedTag.getBoolean("ShowBottom"));
        }

        this.fireProof = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("ShowBottom", this.showBase());
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
        this.close();

        if (!this.detonated && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
            EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, 6);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return;
            }

            this.detonated = true;

            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if (ev.isBlockBreaking()) {
                explosion.explodeA();
            }
            explosion.explodeB();
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean showBase() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE);
    }

    public void setShowBase(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOWBASE, value);
    }
}
