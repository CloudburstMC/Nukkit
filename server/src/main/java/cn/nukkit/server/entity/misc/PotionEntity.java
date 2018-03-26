package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.misc.Potion;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class PotionEntity extends BaseEntity implements Potion {

    public PotionEntity(Vector3f position, NukkitLevel level, NukkitServer server, boolean lingering) {
        super(lingering ? EntityType.LINGERING_POTION : EntityType.SPLASH_POTION, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(1f, 0.01f));
    }

    /*@Override
    protected void initEntity() {
        super.initEntity();

        potionId = this.namedTag.getShort("PotionId");

        this.dataProperties.putShort(DATA_POTION_ID, this.potionId);

        /*Effect effect = Potion.getEffect(potionId, true); TODO: potion color

        if(effect != null) {
            int count = 0;
            int[] c = effect.getColor();
            count += effect.getAmplifier() + 1;

            int r = ((c[0] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int g = ((c[1] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int b = ((c[2] * (effect.getAmplifier() + 1)) / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_UNKNOWN, (r << 16) + (g << 8) + b));
        }
    }


    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        int tickDiff = currentTick - this.lastUpdate;
        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.kill();
            hasUpdate = true;
        }

        if (this.isCollided) {
            this.kill();

            Potion potion = Potion.getPotion(this.potionId);

            PotionCollideEvent event = new PotionCollideEvent(potion, this);
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            potion = event.getPotion();
            if (potion == null) {
                return false;
            }

            potion.setSplash(true);

            Particle particle;
            int r;
            int g;
            int b;

            Effect effect = Potion.getEffect(potion.getId(), true);

            if (effect == null) {
                r = 40;
                g = 40;
                b = 255;
            } else {
                int[] colors = effect.getColor();
                r = colors[0];
                g = colors[1];
                b = colors[2];
            }

            if (Potion.isInstant(potion.getId())) {
                particle = new InstantSpellParticle(this, r, g, b);
            } else {
                particle = new SpellParticle(this, r, g, b);
            }

            this.getLevel().addParticle(particle);

            hasUpdate = true;
            Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(8.25, 4.24, 8.25));
            for (Entity anEntity : entities) {
                double distance = anEntity.distanceSquared(this);

                if (distance < 16) {
                    double d = 1 - Math.sqrt(distance) / 4;

                    potion.applyPotion(anEntity, d);
                }
            }
        }
        this.timing.stopTiming();
        return hasUpdate;
    }*/
}
