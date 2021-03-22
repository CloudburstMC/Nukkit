package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageEvent.DamageModifier;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EntityHumanType extends EntityCreature implements InventoryHolder {

    protected PlayerInventory inventory;
    protected PlayerEnderChestInventory enderChestInventory;
    protected PlayerOffhandInventory offhandInventory;

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

    public PlayerOffhandInventory getOffhandInventory() {
        return offhandInventory;
    }

    @Override
    protected void initEntity() {
        this.inventory = new PlayerInventory(this);
        if (namedTag.containsNumber("SelectedInventorySlot")) {
            this.inventory.setHeldItemSlot(NukkitMath.clamp(this.namedTag.getInt("SelectedInventorySlot"), 0, 8));
        }
        this.offhandInventory = new PlayerOffhandInventory(this);

        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item);
                } else if (slot >= 100 && slot < 104) {
                    this.inventory.setItem(this.inventory.getSize() + slot - 100, NBTIO.getItemHelper(item));
                } else if (slot == -106) {
                    this.offhandInventory.setItem(0, NBTIO.getItemHelper(item));
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

        ListTag<CompoundTag> inventoryTag = null;
        if (this.inventory != null) {
            inventoryTag = new ListTag<>("Inventory");
            this.namedTag.putList(inventoryTag);

            for (int slot = 0; slot < 9; ++slot) {
                inventoryTag.add(new CompoundTag()
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
                inventoryTag.add(NBTIO.putItemHelper(item, slot));
            }

            for (int slot = 100; slot < 104; ++slot) {
                Item item = this.inventory.getItem(this.inventory.getSize() + slot - 100);
                if (item != null && item.getId() != Item.AIR) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot));
                }
            }
            
            this.namedTag.putInt("SelectedInventorySlot", this.inventory.getHeldItemIndex());
        }

        if (this.offhandInventory != null) {
            Item item = this.offhandInventory.getItem(0);
            if (item.getId() != Item.AIR) {
                if (inventoryTag == null) {
                    inventoryTag = new ListTag<>("Inventory");
                    this.namedTag.putList(inventoryTag);
                }
                inventoryTag.add(NBTIO.putItemHelper(item, -106));
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
            List<Item> drops = new ArrayList<>(this.inventory.getContents().values());
            drops.addAll(this.offhandInventory.getContents().values());
            return drops.toArray(Item.EMPTY_ARRAY);
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.CUSTOM && source.getCause() != DamageCause.MAGIC) {
            int armorPoints = 0;
            int epf = 0;
            int toughness = 0;

            for (Item armor : inventory.getArmorContents()) {
                armorPoints += armor.getArmorPoints();
                epf += calculateEnchantmentProtectionFactor(armor, source);
                //toughness += armor.getToughness();
            }

            if (source.canBeReducedByArmor()) {
                source.setDamage(-source.getFinalDamage() * armorPoints * 0.04f, DamageModifier.ARMOR);
            }

            source.setDamage(-source.getFinalDamage() * Math.min(NukkitMath.ceilFloat(Math.min(epf, 25) * ((float) ThreadLocalRandom.current().nextInt(50, 100) / 100)), 20) * 0.04f,
                    DamageModifier.ARMOR_ENCHANTMENTS);

            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), DamageModifier.ABSORPTION);
        }

        if (super.attack(source)) {
            Entity damager = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }

            for (int slot = 0; slot < 4; slot++) {
                Item armor = damageArmor(this.inventory.getArmorItem(slot), damager);
                inventory.setArmorItem(slot, armor, armor.getId() != BlockID.AIR);
            }

            return true;
        } else {
            return false;
        }
    }

    protected double calculateEnchantmentProtectionFactor(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }

        double epf = 0;

        for (Enchantment ench : item.getEnchantments()) {
            epf += ench.getProtectionFactor(source);
        }

        return epf;
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

    @Override
    protected boolean applyNameTag(@Nonnull Player player, @Nonnull Item item) {
        return false;
    }

    @PowerNukkitOnly
    @Deprecated
    @Override
    public boolean applyNameTag(Item item) {
        return false;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Won't damage items that has negative max durability (not damageable)")
    protected Item damageArmor(Item armor, Entity damager) {
        if (armor.hasEnchantments()) {
            if (damager != null) {
                for (Enchantment enchantment : armor.getEnchantments()) {
                    enchantment.doPostAttack(damager, this);
                }
            }

            Enchantment durability = armor.getEnchantment(Enchantment.ID_DURABILITY);
            if (durability != null
                    && durability.getLevel() > 0
                    && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100)) {
                return armor;
            }
        }

        if (armor.isUnbreakable() || armor.getMaxDurability() < 0) {
            return armor;
        }

        armor.setDamage(armor.getDamage() + 1);

        if (armor.getDamage() >= armor.getMaxDurability()) {
            getLevel().addSound(this, Sound.RANDOM_BREAK);
            return Item.get(BlockID.AIR, 0, 0);
        }

        return armor;
    }
}
