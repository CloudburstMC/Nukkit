package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEnderPearl;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.math.BukkitMathHelper;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSnowball extends ProjectileItem {

    public ItemSnowball() {
        this(0, 1);
    }

    public ItemSnowball(Integer meta) {
        this(meta, 1);
    }

    public ItemSnowball(Integer meta, int count) {
        super(SNOWBALL, 0, count, "Snowball");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight() - 0.30000000149011612))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
/*                        .add(new DoubleTag("", directionVector.x))
                        .add(new DoubleTag("", directionVector.y))
                        .add(new DoubleTag("", directionVector.z)))*/
                        .add(new DoubleTag("", -BukkitMathHelper.sin((float) player.yaw / 180.0F * 3.1415927F) * BukkitMathHelper.cos((float) player.pitch / 180.0F * 3.1415927F)))
                        .add(new DoubleTag("", -BukkitMathHelper.sin((float) player.pitch / 180.0F * 3.1415927F)))
                        .add(new DoubleTag("", BukkitMathHelper.cos((float) player.yaw / 180.0F * 3.1415927F) * BukkitMathHelper.cos((float) player.pitch / 180.0F * 3.1415927F))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) player.yaw))
                        .add(new FloatTag("", (float) player.pitch)));

        this.correctNBT(nbt);

        Entity projectile = Entity.createEntity(this.getProjectileEntityType(), player.getLevel().getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4), nbt, player);
        if (projectile != null) {
            if (projectile instanceof EntityEnderPearl) {
                if (player.getServer().getTick() - player.getLastEnderPearlThrowingTick() < 20) {
                    projectile.kill();
                    return false;
                }
            }

            projectile.setMotion(projectile.getMotion().multiply(this.getThrowForce()));

            if (projectile instanceof EntityProjectile) {
                ProjectileLaunchEvent ev = new ProjectileLaunchEvent((EntityProjectile) projectile);

                player.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    projectile.kill();
                } else {
                    if (!player.isCreative()) {
                        this.count--;
                    }
                    if (projectile instanceof EntityEnderPearl) {
                        player.onThrowEnderPearl();
                    }
                    projectile.spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String getProjectileEntityType() {
        return "Snowball";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
