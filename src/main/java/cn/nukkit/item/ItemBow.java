package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Random;
import java.util.stream.Stream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBow extends ItemTool {

    public ItemBow() {
        this(0, 1);
    }

    public ItemBow(Integer meta) {
        this(meta, 1);
    }

    public ItemBow(Integer meta, int count) {
        super(BOW, meta, count, "Bow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_BOW;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.isCreative() ||
                Stream.of(player.getInventory(), player.getOffhandInventory())
                        .anyMatch(inv -> inv.contains(Item.get(ItemID.ARROW)));
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        Item itemArrow = Item.get(Item.ARROW, 0, 1);

        Inventory inventory = player.getOffhandInventory();

        if (!inventory.contains(itemArrow) && !(inventory = player.getInventory()).contains(itemArrow) && player.isSurvival()) {
            player.getOffhandInventory().sendContents(player);
            inventory.sendContents(player);
            return false;
        }

        double damage = 2;

        Enchantment bowDamage = this.getEnchantment(Enchantment.ID_BOW_POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += 0.25 * (bowDamage.getLevel() + 1);
        }

        Enchantment flameEnchant = this.getEnchantment(Enchantment.ID_BOW_FLAME);
        boolean flame = flameEnchant != null && flameEnchant.getLevel() > 0;

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
                        .add(new FloatTag("", (float) -player.pitch)))
                .putShort("Fire", flame ? 45 * 60 : 0)
                .putDouble("damage", damage);

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2;

        EntityArrow arrow = (EntityArrow) Entity.createEntity("Arrow", player.chunk, nbt, player, f == 2);

        if (arrow == null) {
            return false;
        }

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, f);

        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
            player.getInventory().sendContents(player);
            player.getOffhandInventory().sendContents(player);
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            Enchantment infinityEnchant = this.getEnchantment(Enchantment.ID_BOW_INFINITY);
            boolean infinity = infinityEnchant != null && infinityEnchant.getLevel() > 0;
            EntityProjectile projectile;
            if (infinity && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityArrow.PICKUP_CREATIVE);
            }
            if (player.isSurvival()) {
                if (!infinity) {
                    inventory.removeItem(itemArrow);
                }
                if (!this.isUnbreakable()) {
                    Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
                    if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100))) {
                        this.setDamage(this.getDamage() + 1);
                        if (this.getDamage() >= getMaxDurability()) {
                            this.count--;
                        }
                        player.getInventory().setItemInHand(this);
                    }
                }
            }
            if (entityShootBowEvent.getProjectile() != null) {
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile());
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
                }
            }
        }

        return true;
    }
}
