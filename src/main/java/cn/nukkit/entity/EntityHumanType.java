package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

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
                if (slot >= 0 && slot < 9) {
                    this.inventory.setHotbarSlotIndex(slot, item.contains("TrueSlot") ? item.getByte("TrueSlot") : -1);
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
                int hotbarSlot = this.inventory.getHotbarSlotIndex(slot);
                if (hotbarSlot != -1) {
                    Item item = this.inventory.getItem(hotbarSlot);
                    if (item.getId() != 0 && item.getCount() > 0) {
                        this.namedTag.getList("Inventory", CompoundTag.class).add(NBTIO.putItemHelper(item, slot).putByte("TrueSlot", hotbarSlot));
                        continue;
                    }
                }
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
    public void attack(EntityDamageEvent source) {
        if (!this.isAlive()) {
            return;
        }

        if (source.getCause() != EntityDamageEvent.CAUSE_VOID && source.getCause() != EntityDamageEvent.CAUSE_CUSTOM && source.getCause() != EntityDamageEvent.CAUSE_MAGIC) {
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

            source.setDamage(finalDamage - originalDamage, EntityDamageEvent.MODIFIER_ARMOR);
            //source.setDamage(source.getDamage(EntityDamageEvent.MODIFIER_ARMOR_ENCHANTMENTS) - (originalDamage - originalDamage * (1 - epf / 25)), EntityDamageEvent.MODIFIER_ARMOR_ENCHANTMENTS);
        }

        super.attack(source);

        if (!source.isCancelled()) {
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
                    inventory.setArmorItem(slot, armor);
                }
            }
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
