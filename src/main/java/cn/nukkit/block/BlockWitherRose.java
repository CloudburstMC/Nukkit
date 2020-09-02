package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.potion.Effect;

import javax.annotation.Nonnull;

public class BlockWitherRose extends BlockFlower {
    public BlockWitherRose() {
        this(0);
    }

    public BlockWitherRose(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Wither Rose";
    }

    @Override
    public int getId() {
        return WITHER_ROSE;
    }

    @Override
    public boolean canPlantOn(Block block) {
        return super.canPlantOn(block) || block.getId() == Block.NETHERRACK || block.getId() == Block.SOUL_SAND;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        return false;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (level.getServer().getDifficulty() != 0 && entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            if (!living.invulnerable && !living.hasEffect(Effect.WITHER)
                    && (!(living instanceof Player) || !((Player) living).isCreative() && !((Player) living).isSpectator())) {
                Effect effect = Effect.getEffect(Effect.WITHER);
                effect.setDuration(40);
                effect.setAmplifier(1);
                living.addEffect(effect);
            }
        }
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }
}
