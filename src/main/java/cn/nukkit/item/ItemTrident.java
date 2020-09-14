package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

/**
 * Created by PetteriM1
 */
public class ItemTrident extends ItemTool {

    public ItemTrident() {
        this(0, 1);
    }

    public ItemTrident(Integer meta) {
        this(meta, 1);
    }

    public ItemTrident(Integer meta, int count) {
        super(TRIDENT, meta, count, "Trident");
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
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
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
        EntityThrownTrident trident = (EntityThrownTrident) Entity.createEntity("ThrownTrident", player.chunk, nbt, player, f == 2);

        if (trident == null) {
            return false;
        }

        trident.setItem(this);

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
                    player.getLevel().addSound(player, Sound.ITEM_TRIDENT_THROW);
                    if (!player.isCreative()) {
                        this.count--;
                        player.getInventory().setItemInHand(this);
                    }
                }
            }
        }

        return true;
    }
}
