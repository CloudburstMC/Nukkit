package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.impl.misc.EntityAreaEffectCloud;
import cn.nukkit.entity.misc.AreaEffectCloud;
import cn.nukkit.entity.projectile.LingeringPotion;
import cn.nukkit.entity.projectile.SplashPotion;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.registry.EntityRegistry;

public class EntityLingeringPotion extends EntitySplashPotion implements LingeringPotion {
    
    public static final int NETWORK_ID = 101;
    
    public EntityLingeringPotion(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setFlag(EntityFlag.LINGER, true);
    }

    @Override
    protected void splash(Entity collidedWith) {
        super.splash(collidedWith);
        saveNBT();
        ListTag<?> pos = (ListTag<?>) namedTag.getList("Pos", CompoundTag.class).copy();
        CompoundTag nbt = new CompoundTag().putList(pos)
                .putList(new ListTag<>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0))
                )
                .putList(new ListTag<>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                )
                .putShort("PotionId", potionId);
    
        AreaEffectCloud entity = EntityRegistry.get().newEntity(EntityTypes.AREA_EFFECT_CLOUD, getChunk(), nbt);
        
        Effect effect = Potion.getEffect(potionId, true);
    
        if (effect != null && entity != null) {
            entity.getCloudEffects().add(effect.setDuration(1).setVisible(false).setAmbient(false));
            entity.spawnToAll();
        }
    }
}
