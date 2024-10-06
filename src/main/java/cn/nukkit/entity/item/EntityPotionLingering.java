package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

public class EntityPotionLingering extends EntityPotion {

    public static final int NETWORK_ID = 101;

    public EntityPotionLingering(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityPotionLingering(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setDataFlag(DATA_FLAGS, DATA_FLAG_LINGER, true);
    }

    @Override
    protected void splash(Entity collidedWith) {
        super.splash(collidedWith);
        EntityAreaEffectCloud entity = (EntityAreaEffectCloud) Entity.createEntity("AreaEffectCloud", this.chunk,
                getDefaultNBT(this).putShort("PotionId", potionId)
        );

        Effect effect = Potion.getEffect(potionId, true);

        if (effect != null && entity != null) {
            entity.cloudEffects.add(effect.setDuration(1).setVisible(false).setAmbient(false));
            entity.spawnToAll();
        }
    }
}
