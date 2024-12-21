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
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

public class ItemCrossbow extends ItemBow {

    public ItemCrossbow() {
        this(0, 1);
    }

    public ItemCrossbow(Integer meta) {
        this(meta, 1);
    }

    public ItemCrossbow(Integer meta, int count) {
        super(CROSSBOW, meta, count, "Crossbow");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CROSSBOW;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        int needTickUsed = 25;
        Enchantment enchantment = this.getEnchantment(Enchantment.ID_CROSSBOW_QUICK_CHARGE);
        if (enchantment != null) {
            needTickUsed -= enchantment.getLevel() * 5; //0.25s
        }

        if (ticksUsed < needTickUsed) {
            return true;
        }

        Item itemArrow = null;
        boolean offhand = false;
        Item offhandItem = player.getOffhandInventory().getItemFast(0);
        if (offhandItem.getId() == ItemID.ARROW) {
            itemArrow = offhandItem.clone();
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
                return true;
            }
        }

        if (!this.isLoaded()) {
            if (!player.isCreative()) {
                if (!this.isUnbreakable()) {
                    Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
                    if (!(durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= ThreadLocalRandom.current().nextInt(100))) {
                        this.setDamage(this.getDamage() + 2);
                        if (this.getDamage() >= DURABILITY_CROSSBOW) {
                            this.count--;
                        }
                    }
                }

                if (offhand) {
                    player.getOffhandInventory().removeItem(itemArrow);
                } else {
                    player.getInventory().removeItem(itemArrow);
                }
            }

            player.getInventory().setItemInHand(this.loadArrow(itemArrow));
            player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_CROSSBOW_LOADING_END);
        }

        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return false; // Handled directly from Player
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    public Item loadArrow(Item arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("null cannot be loaded into a crossbow!");
        }
        if (arrow.getId() != Item.ARROW) {
            throw new IllegalArgumentException(arrow + " cannot be loaded into a crossbow!");
        }
        if (arrow.getCount() != 1) {
            throw new IllegalArgumentException("Only one arrow per crossbow is supported!");
        }
        CompoundTag tag = this.getNamedTag() == null ? new CompoundTag() : this.getNamedTag();
        CompoundTag chargedItem = new CompoundTag("chargedItem")
                .putByte("Count", arrow.getCount())
                .putShort("Damage", arrow.getDamage())
                .putString("Name", "minecraft:arrow");
        CompoundTag cTag;
        tag.putBoolean("Charged", true).putCompound("chargedItem", chargedItem);
        this.setCompoundTag(tag);
        return this;
    }

    public boolean isLoaded() {
        Tag itemInfo = this.getNamedTagEntry("chargedItem");
        if (itemInfo != null) {
            CompoundTag tag = (CompoundTag) itemInfo;
            return tag.getByte("Count") > 0 && !tag.getString("Name").isEmpty();
        }

        return false;
    }

    /**
     * Launch the crossbow. Assumes that isLoaded() == true.
     * @param player player
     * @return launched successfully
     */
    public boolean launchArrow(Player player) {
        CompoundTag chargedItem = ((CompoundTag) this.getNamedTagEntry("chargedItem"));
        if (chargedItem == null) {
            throw new IllegalArgumentException("A crossbow without charged item cannot be launched!");
        }
        int arrowData = chargedItem.getShort("Damage");
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
        EntityProjectile arrow;
        {
            arrow = (EntityArrow) Entity.createEntity(EntityArrow.NETWORK_ID, player.chunk, nbt, player, false, true);
            if (arrowData > 0) {
                ((EntityArrow) arrow).setData(arrowData);
            }
            if (this.hasEnchantment(Enchantment.ID_CROSSBOW_PIERCING)) {
                arrow.piercing = 1;
            }
        }
        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(player, this, arrow, 3.5);
        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().close();
            return false;
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            if (entityShootBowEvent.getProjectile() != null) {
                EntityProjectile proj = entityShootBowEvent.getProjectile();
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(proj);
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    proj.close();
                    return false;
                } else {
                    proj.spawnToAll();
                    if (this.hasEnchantment(Enchantment.ID_CROSSBOW_MULTISHOT)) {
                        CompoundTag nbt1 = new CompoundTag()
                                .putList(new ListTag<DoubleTag>("Pos")
                                        .add(new DoubleTag("", player.x))
                                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                                        .add(new DoubleTag("", player.z)))
                                .putList(new ListTag<DoubleTag>("Motion")
                                        .add(new DoubleTag("", -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                                        .add(new DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI)))
                                        .add(new DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                                .putList(new ListTag<FloatTag>("Rotation")
                                        .add(new FloatTag("", (player.yaw > 180 ? 360 : 0) - (float) player.yaw - 10))
                                        .add(new FloatTag("", (float) -player.pitch)));
                        EntityArrow arrow1 = (EntityArrow) Entity.createEntity(EntityArrow.NETWORK_ID, player.chunk, nbt1, player, false, true);
                        arrow1.setPickupMode(EntityProjectile.PICKUP_NONE_REMOVE);
                        if (arrowData > 0) {
                            arrow1.setData(arrowData);
                        }
                        if (this.hasEnchantment(Enchantment.ID_CROSSBOW_PIERCING)) { // Illegal enchantment
                            arrow1.piercing = 1;
                        }
                        arrow1.setMotion(arrow1.getMotion().multiply(entityShootBowEvent.getForce()).add(-0.3, 0, 0.3));
                        arrow1.spawnToAll();
                        CompoundTag nbt2 = new CompoundTag()
                                .putList(new ListTag<DoubleTag>("Pos")
                                        .add(new DoubleTag("", player.x))
                                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                                        .add(new DoubleTag("", player.z)))
                                .putList(new ListTag<DoubleTag>("Motion")
                                        .add(new DoubleTag("", -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                                        .add(new DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI)))
                                        .add(new DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                                .putList(new ListTag<FloatTag>("Rotation")
                                        .add(new FloatTag("", (player.yaw > 180 ? 360 : 0) - (float) player.yaw + 10))
                                        .add(new FloatTag("", (float) -player.pitch)));
                        EntityArrow arrow2 = (EntityArrow) Entity.createEntity(EntityArrow.NETWORK_ID, player.chunk, nbt2, player, false, true);
                        arrow2.setPickupMode(EntityProjectile.PICKUP_NONE_REMOVE);
                        if (arrowData > 0) {
                            arrow2.setData(arrowData);
                        }
                        if (this.hasEnchantment(Enchantment.ID_CROSSBOW_PIERCING)) { // Illegal enchantment
                            arrow2.piercing = 1;
                        }
                        arrow2.setMotion(arrow2.getMotion().multiply(entityShootBowEvent.getForce()).add(0.3, 0, -0.3));
                        arrow2.spawnToAll();
                    }
                    player.getLevel().addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_CROSSBOW_SHOOT);
                    this.setCompoundTag(this.getNamedTag().putBoolean("Charged", false).remove("chargedItem"));
                    player.getInventory().setItemInHand(this);
                }
            }
        }
        return true;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }
}
