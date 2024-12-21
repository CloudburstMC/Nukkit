package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemBow extends ItemTool {

    public ItemBow() {
        this(0, 1);
    }

    public ItemBow(Integer meta) {
        this(meta, 1);
    }

    public ItemBow(Integer meta, int count) {
        this(BOW, meta, count, "Bow");
    }

    public ItemBow(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
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
        return playerHasArrow(player) || player.isCreative();
    }

    private boolean playerHasArrow(Player p) {
        if (p.getOffhandInventory().getItemFast(0).getId() == ItemID.ARROW) return true;
        for (Item i : p.getInventory().getContents().values()) {
            if (i.getId() == ItemID.ARROW) return true;
        }
        return false;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        Item itemArrow = null;
        boolean offhand = false;
        if (player.getOffhandInventory().getItemFast(0).getId() == ItemID.ARROW) {
            itemArrow = player.getOffhandInventory().getItemFast(0).clone();
            itemArrow.setCount(1);
            offhand = true;
        } else {
            for (Item i : player.getInventory().getContents().values()) {
                if (i.getId() == ItemID.ARROW) {
                    itemArrow = i.clone();
                    itemArrow.setCount(1);
                    break;
                }
            }
        }

        if (itemArrow == null) {
            if (player.isCreative()) {
                itemArrow = Item.get(Item.ARROW, 0, 1);
            } else {
                return false;
            }
        }

        double damage = 2;
        Enchantment bowDamage = this.getEnchantment(Enchantment.ID_BOW_POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += (double) bowDamage.getLevel() * 0.5 + 0.5;
        }

        Enchantment flameEnchant = this.getEnchantment(Enchantment.ID_BOW_FLAME);
        boolean flame = flameEnchant != null && flameEnchant.getLevel() > 0;

        float knockBack = 0.3f;
        Enchantment knockBackEnchantment = this.getEnchantment(Enchantment.ID_BOW_KNOCKBACK);
        if (knockBackEnchantment != null) {
            knockBack += knockBackEnchantment.getLevel() * 0.1f;
        }

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
                .putShort("Fire", flame ? 2700 : 0)
                .putDouble("damage", damage)
                .putFloat("knockback", knockBack);

        double p = (double) ticksUsed / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 2.8;

        EntityArrow arrow = (EntityArrow) Entity.createEntity(EntityArrow.NETWORK_ID, player.chunk, nbt, player, f > 2.3);
        if (itemArrow.getDamage() > 0) {
            arrow.setData(itemArrow.getDamage());
        }
        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, f);

        if (f < 0.1 || ticksUsed < 3) {
            entityShootBowEvent.setCancelled();
        }

        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
            return false;
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            Enchantment infinityEnchant = this.getEnchantment(Enchantment.ID_BOW_INFINITY);
            boolean infinity = infinityEnchant != null && infinityEnchant.getLevel() > 0;
            EntityProjectile projectile;
            if (infinity && itemArrow.getDamage() == 0 && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityArrow.PICKUP_CREATIVE);
            }
            if (!player.isCreative()) {
                if (!infinity || itemArrow.getDamage() != 0) {
                    if (offhand) {
                        player.getOffhandInventory().removeItem(itemArrow);
                    } else {
                        player.getInventory().removeItem(itemArrow);
                    }
                }

                if (!this.isUnbreakable()) {
                    Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
                    if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= ThreadLocalRandom.current().nextInt(100))) {
                        this.setDamage(this.getDamage() + 2);
                        if (this.getDamage() >= getMaxDurability()) {
                            this.count--;
                        }
                        player.getInventory().setItemInHand(this);
                    }
                }
            }

            if (entityShootBowEvent.getProjectile() != null) {
                EntityProjectile proj = entityShootBowEvent.getProjectile();
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(proj);
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    proj.close();
                } else {
                    proj.spawnToAll();
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BOW);
                }
            }
        }

        return true;
    }
}
