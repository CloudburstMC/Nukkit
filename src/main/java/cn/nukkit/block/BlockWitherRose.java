package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.potion.Effect;

public class BlockWitherRose extends BlockFlower {

    public BlockWitherRose() {
        this(0);
    }

    public BlockWitherRose(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return WITHER_ROSE;
    }
    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (level.getServer().getDifficulty() != 0 && entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            if (!living.invulnerable && !living.hasEffect(Effect.WITHER)
                    && (!(living instanceof Player) || !((Player) living).isCreative() && !((Player) living).isSpectator())) {
                Effect effect = Effect.getEffect(Effect.WITHER);
                effect.setDuration(50); // No damage is given if less due to how the effect is ticked
                living.addEffect(effect, EntityPotionEffectEvent.Cause.WITHER_ROSE);
            }
        }
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.2;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.2;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.8;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.8;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.8;
    }
}
