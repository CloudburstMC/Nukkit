package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.player.PlayerToggleSpinAttackEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

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

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        Enchantment riptide = this.getEnchantment(Enchantment.ID_TRIDENT_RIPTIDE);
        if (riptide != null) {
            PlayerToggleSpinAttackEvent playerToggleSpinAttackEvent = new PlayerToggleSpinAttackEvent(player, true);

            int riptideLevel = riptide.getLevel();
            if (riptideLevel < 1) {
                playerToggleSpinAttackEvent.setCancelled(true);
            } else {
                boolean inWater = false;
                for (Block block : player.getCollisionBlocks()) {
                    if (block instanceof BlockWater || block.level.isBlockWaterloggedAt(player.chunk, (int) block.x, (int) block.y, (int) block.z)) {
                        inWater = true;
                        break;
                    }
                }
                if (!(inWater || (player.getLevel().isRaining() && player.canSeeSky()))) {
                    playerToggleSpinAttackEvent.setCancelled(true);
                }
            }

            player.getServer().getPluginManager().callEvent(playerToggleSpinAttackEvent);

            if (playerToggleSpinAttackEvent.isCancelled()) {
                player.setNeedSendData(true);
            } else {
                player.onSpinAttack(riptideLevel);
                player.setSpinAttack(true);
                player.resetFallDistance();

                int riptideSound;
                if (riptideLevel >= 3) {
                    riptideSound = LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RIPTIDE_3;
                } else if (riptideLevel == 2) {
                    riptideSound = LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RIPTIDE_2;
                } else {
                    riptideSound = LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RIPTIDE_1;
                }

                player.getLevel().addLevelSoundEvent(player, riptideSound);
            }
            return true;
        }

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

        EntityThrownTrident trident = (EntityThrownTrident) EntityThrownTrident.createEntity(EntityThrownTrident.NETWORK_ID, player.chunk, nbt, player);
        trident.setItem(this);

        if (player.isCreative()) {
            trident.setPickupMode(EntityProjectile.PICKUP_CREATIVE);
        }

        if (this.hasEnchantment(Enchantment.ID_TRIDENT_LOYALTY)) {
            trident.setFavoredSlot(player.getInventory().getHeldItemIndex());
        }

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2.5;

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, trident, f);

        if (f < 0.1 || ticksUsed < 5) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            ProjectileLaunchEvent ev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                entityShootBowEvent.getProjectile().close();
            } else {
                entityShootBowEvent.getProjectile().spawnToAll();
                player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_THROW);
                if (!player.isCreative()) {
                    this.count--;
                    player.getInventory().setItemInHand(this);
                }
            }
        }

        return true;
    }
}
