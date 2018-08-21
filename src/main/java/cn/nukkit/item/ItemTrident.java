package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.scheduler.NukkitRunnable;

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
    
    public boolean onReleaseUsing(Player player) {
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

        int diff = (Server.getInstance().getTick() - player.getStartActionTick());
        double p = (double) diff / 20;

        double f = Math.min((p * p + p * 2) / 3, 1) * 2;
        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, new EntityThrownTrident(player.chunk, nbt, player, f == 2), f);

        if (f < 0.1 || diff < 5) {
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
                    player.getLevel().addLevelSoundEvent(new Vector3(player.x, player.y, player.z), 183, 1, -1);
                    if (!player.isCreative()) {
                        // idk why but trident returns to inventory without this
                        new NukkitRunnable() {
                            public void run() {
                                player.getInventory().removeItem(Item.get(Item.TRIDENT, 0, 1));
                            }
                        }.runTaskLater(null, 1);
                    }
                }
            }
        }

        return true;
    }
}
