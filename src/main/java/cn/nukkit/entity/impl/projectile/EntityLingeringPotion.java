package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.misc.AreaEffectCloud;
import cn.nukkit.entity.projectile.LingeringPotion;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.registry.EntityRegistry;

import static com.nukkitx.protocol.bedrock.data.entity.EntityFlag.LINGERING;

public class EntityLingeringPotion extends EntitySplashPotion implements LingeringPotion {

    public EntityLingeringPotion(EntityType<LingeringPotion> type, Location location) {
        super(type, location);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.data.setFlag(LINGERING, true);
    }

    @Override
    protected void splash(Entity collidedWith) {
        super.splash(collidedWith);

        AreaEffectCloud entity = EntityRegistry.get().newEntity(EntityTypes.AREA_EFFECT_CLOUD, this.getLocation());
        entity.setPosition(this.getLocation().getPosition());
        entity.setPotionId(this.getPotionId());

        Effect effect = Potion.getEffect(this.getPotionId(), true);

        if (effect != null) {
            entity.getCloudEffects().add(effect);
            entity.spawnToAll();
        }
    }
}
