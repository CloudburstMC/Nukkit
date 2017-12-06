package cn.nukkit.server.entity;

import cn.nukkit.api.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.api.event.entity.EntityDamageEvent;
import cn.nukkit.api.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.api.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.server.Player;
import cn.nukkit.server.block.BlockAir;
import cn.nukkit.server.inventory.InventoryHolder;
import cn.nukkit.server.inventory.PlayerEnderChestInventory;
import cn.nukkit.server.inventory.PlayerInventory;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.ListTag;

import java.util.Random;

public abstract class EntityHumanType extends EntityCreature implements InventoryHolder {

    protected PlayerInventory inventory;
    protected PlayerEnderChestInventory enderChestInventory;

    public EntityHumanType(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public PlayerInventory getInventory() {
        return inventory;
    }

    public PlayerEnderChestInventory getEnderChestInventory() {
        return enderChestInventory;
    }

    @Override
    protected void initEntity() {
        this.inventory = new PlayerInventory(this);

        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item);
                } else if (slot >= 100 && slot < 104) {
                    this.inventory.setItem(this.inventory.getSize() + slot - 100, NBTIO.getItemHelper(item));
                } else {
                    this.inventory.setItem(slot - 9, NBTIO.getItemHelper(item));
                }
            }
        }

        this.enderChestInventory = new PlayerEnderChestInventory(this);

        if (this.namedTag.contains("EnderItems") && this.namedTag.get("EnderItems") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.enderChestInventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        super.initEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Inventory"));
        if (this.inventory != null) {
            for (int slot = 0; slot < 9; ++slot) {
                this.namedTag.getList("Inventory", CompoundTag.class).add(new CompoundTag()
                        .putByte("Count", 0)
                        .putShort("Damage", 0)
                        .putByte("Slot", slot)
                        .putByte("TrueSlot", -1)
                        .putShort("id", 0)
                );
            }

            int slotCount = Player.SURVIVAL_SLOTS + 9;
            for (int slot = 9; slot < slotCount; ++slot) {
                Item item = this.inventory.getItem(slot - 9);
                this.namedTag.getList("Inventory", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
            }

            for (int slot = 100; slot < 104; ++slot) {
                Item item = this.inventory.getItem(this.inventory.getSize() + slot - 100);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("Inventory", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }

        this.namedTag.putList(new ListTag<CompoundTag>("EnderItems"));
        if (this.enderChestInventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.enderChestInventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("EnderItems", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }

    @Override
    public Item[] getDrops() {
        if (this.inventory != null) {
            return this.inventory.getContents().values().stream().toArray(Item[]::new);
        }
        return new Item[0];
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return false;
        }

        if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.CUSTOM && source.getCause() != DamageCause.MAGIC) {
            int points = 0;
            int epf = 0;
            int toughness = 0;

            for (Item armor : inventory.getArmorContents()) {
                points += armor.getArmorPoints();
                epf += calculateEnchantmentReduction(armor, source);
                toughness += armor.getToughness();
            }

            float originalDamage = source.getDamage();

            float finalDamage = (float) (originalDamage * (1 - Math.max(points / 5, points - originalDamage / (2 + toughness / 4)) / 25) * (1 - /*0.75 */ epf * 0.04));

            source.setDamage(finalDamage - originalDamage, DamageModifier.ARMOR);
            //source.setDamage(source.getDamage(DamageModifier.ARMOR_ENCHANTMENTS) - (originalDamage - originalDamage * (1 - epf / 25)), DamageModifier.ARMOR_ENCHANTMENTS);
        }

        if (super.attack(source)) {
            Entity damager = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }

            for (int slot = 0; slot < 4; slot++) {
                Item armor = this.inventory.getArmorItem(slot);

                if (armor.hasEnchantments()) {
                    if (damager != null) {
                        for (Enchantment enchantment : armor.getEnchantments()) {
                            enchantment.doPostAttack(damager, this);
                        }
                    }

                    Enchantment durability = armor.getEnchantment(Enchantment.ID_DURABILITY);
                    if (durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100))
                        continue;
                }
                armor.setDamage(armor.getDamage() + 1);

                if (armor.getDamage() >= armor.getMaxDurability()) {
                    inventory.setArmorItem(slot, new ItemBlock(new BlockAir()));
                } else {
                    inventory.setArmorItem(slot, armor, true);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected double calculateEnchantmentReduction(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }

        double reduction = 0;

        for (Enchantment ench : item.getEnchantments()) {
            reduction += ench.getDamageProtection(source);
        }

        return reduction;
    }

    @Override
    public void setOnFire(int seconds) {
        int level = 0;

        for (Item armor : this.inventory.getArmorContents()) {
            Enchantment fireProtection = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE);

            if (fireProtection != null && fireProtection.getLevel() > 0) {
                level = Math.max(level, fireProtection.getLevel());
            }
        }

        seconds = (int) (seconds * (1 - level * 0.15));

        super.setOnFire(seconds);
    }
}
