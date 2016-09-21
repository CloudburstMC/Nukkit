package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.InventoryHolder;
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

    public EntityHumanType(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public PlayerInventory getInventory() {
        return inventory;
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

            for (Item armor : inventory.getArmorContents()) {
                points += armor.getArmorPoints();
                epf += calculateEnchantmentReduction(armor, source);
            }

            float originalDamage = source.getDamage();
            float r = (source.getDamage(EntityDamageEvent.MODIFIER_ARMOR) - (originalDamage - originalDamage * (1 - Math.max(points / 5, points - originalDamage / 2) / 25)));

            originalDamage += r;

            epf = Math.min(20, epf);

            source.setDamage(r, EntityDamageEvent.MODIFIER_ARMOR);
            source.setDamage(source.getDamage(EntityDamageEvent.MODIFIER_ARMOR_ENCHANTMENTS) - (originalDamage - originalDamage * (1 - epf / 25)), EntityDamageEvent.MODIFIER_ARMOR_ENCHANTMENTS);
        }

        super.attack(source);

        if (!source.isCancelled()) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                int thornsDamage = 0;
                Random rnd = new Random();

                for (Item armor : inventory.getArmorContents()) {
                    Enchantment thorns = armor.getEnchantment(Enchantment.ID_THORNS);

                    if (thorns != null && thorns.getLevel() > 0) {
                        int chance = thorns.getLevel() * 15;

                        if (chance > 90) {
                            chance = 90;
                        }

                        if (rnd.nextInt(100) + 1 <= chance) {
                            thornsDamage += rnd.nextInt(4) + 1;
                        }
                    }

                    Enchantment fireAspect = armor.getEnchantment(Enchantment.ID_FIRE_ASPECT);

                    if (fireAspect != null && fireAspect.getLevel() > 0) {
                        damager.setOnFire(4 * fireAspect.getLevel());
                    }
                }

                if (thornsDamage > 0) {
                    damager.attack(new EntityDamageEvent(damager, EntityDamageEvent.CAUSE_MAGIC, rnd.nextInt(4) + 1));
                }
            }

            for (int slot = 0; slot < 4; slot++) {
                Item armor = this.inventory.getArmorItem(slot);

                if (armor.hasEnchantments()) {
                    Enchantment durability = armor.getEnchantment(Enchantment.ID_DURABILITY);
                    if (durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100))
                        continue;
                }
                armor.setDamage(armor.getDamage() - 1);

                if (armor.getDamage() <= 0) {
                    inventory.setArmorItem(slot, new ItemBlock(new BlockAir()));
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

        for(Item armor : this.inventory.getArmorContents()){
            Enchantment fireProtection = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE);

            if(fireProtection != null && fireProtection.getLevel() > 0){
                level = Math.max(level, fireProtection.getLevel());
            }
        }

        seconds = (int) (seconds * (1 - level * 0.15));

        super.setOnFire(seconds);
    }
}
