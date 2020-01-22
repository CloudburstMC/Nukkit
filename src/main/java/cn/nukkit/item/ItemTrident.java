package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.impl.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.ThrownTrident;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;

/**
 * Created by PetteriM1
 */
public class ItemTrident extends ItemTool {

    public ItemTrident(Identifier id) {
        super(id);
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_TRIDENT;
    }

    @Override
    public boolean isSword() {
        return true;
    }
    
    @Override
    public int getAttackDamage() {
        return 9;
    }

    @Override
    public boolean onClickAir(Player player, Vector3f directionVector) {
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        this.useOn(player);

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (player.yaw > 180 ? 360 : 0) - (float) player.yaw))
                        .add(new FloatTag("", (float) -player.pitch)));

        double p = (double) ticksUsed / 20;

        double f = Math.min((p * p + p * 2) / 3, 1) * 2;
        ThrownTrident trident = EntityRegistry.get().newEntity(EntityTypes.THROWN_TRIDENT, player.chunk, nbt);
        trident.setShooter(player);
        trident.setCritical(f == 2);
        trident.setTrident(this);

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, trident, f);

        if (f < 0.1 || ticksUsed < 5) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            if (entityShootBowEvent.getProjectile() instanceof EntityProjectile) {
                ProjectileLaunchEvent ev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_THROW);
                    if (!player.isCreative()) {
                        this.decrementCount();
                        player.getInventory().setItemInHand(this);
                    }
                }
            }
        }

        return true;
    }
}
