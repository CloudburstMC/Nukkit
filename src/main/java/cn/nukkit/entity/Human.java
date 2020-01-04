package cn.nukkit.entity;

import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerFlag;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import cn.nukkit.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.entity.data.EntityFlag.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Human extends Creature<Human> implements InventoryHolder {

    protected UUID identity;
    protected Skin skin;

    protected PlayerInventory inventory;
    protected PlayerEnderChestInventory enderChestInventory;

    public Human(EntityType<Human> type, Chunk chunk, CompoundTag tag) {
        super(type, chunk, tag);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    protected float getBaseOffset() {
        return this.getEyeHeight();
    }

    public Skin getSkin() {
        return skin;
    }

    public UUID getServerId() {
        return identity;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
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
        this.setPlayerFlag(PlayerFlag.SLEEP, false);
        this.setFlag(GRAVITY, true);

        if (!(this instanceof Player)) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"));
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = this.namedTag.getCompound("Skin");
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                Skin newSkin = new Skin();
                if (skinTag.contains("ModelId")) {
                    newSkin.setSkinId(skinTag.getString("ModelId"));
                }
                if (skinTag.contains("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        newSkin.setSkinData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setSkinData(data);
                    }
                }
                if (skinTag.contains("CapeId")) {
                    newSkin.setCapeId(skinTag.getString("CapeId"));
                }
                if (skinTag.contains("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        newSkin.setCapeData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setCapeData(data);
                    }
                }
                if (skinTag.contains("GeometryName")) {
                    newSkin.setGeometryName(skinTag.getString("GeometryName"));
                }
                if (skinTag.contains("SkinResourcePatch")) {
                    newSkin.setSkinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("GeometryData")) {
                    newSkin.setGeometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("AnimationData")) {
                    newSkin.setAnimationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("PremiumSkin")) {
                    newSkin.setPremium(skinTag.getBoolean("PremiumSkin"));
                }
                if (skinTag.contains("PersonaSkin")) {
                    newSkin.setPersona(skinTag.getBoolean("PersonaSkin"));
                }
                if (skinTag.contains("CapeOnClassicSkin")) {
                    newSkin.setCapeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
                }
                if (skinTag.contains("AnimatedImageData")) {
                    ListTag<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                    for (CompoundTag animationTag : list.getAll()) {
                        float frames = animationTag.getFloat("Frames");
                        int type = animationTag.getInt("Type");
                        byte[] image = animationTag.getByteArray("Image");
                        int width = animationTag.getInt("ImageWidth");
                        int height = animationTag.getInt("ImageHeight");
                        skin.getAnimations().add(new SkinAnimation(new SerializedImage(width, height, image), type, frames));
                    }
                }
                this.setSkin(newSkin);
            }

            this.identity = Utils.dataToUUID(String.valueOf(this.getUniqueId()).getBytes(StandardCharsets.UTF_8), this.getSkin()
                    .getSkinData().data, this.getNameTag().getBytes(StandardCharsets.UTF_8));
        }

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
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.inventory != null) {
            ListTag<CompoundTag> inventoryTag = new ListTag<>("Inventory");
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
                if (item != null && item.getId() != AIR) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot));
                }
            }
        }

        this.namedTag.putList(new ListTag<CompoundTag>("EnderItems"));
        if (this.enderChestInventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.enderChestInventory.getItem(slot);
                if (item != null && item.getId() != AIR) {
                    this.namedTag.getList("EnderItems", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }

        if (skin != null) {
            CompoundTag skinTag = new CompoundTag()
                    .putByteArray("Data", this.getSkin().getSkinData().data)
                    .putInt("SkinImageWidth", this.getSkin().getSkinData().width)
                    .putInt("SkinImageHeight", this.getSkin().getSkinData().height)
                    .putString("ModelId", this.getSkin().getSkinId())
                    .putString("CapeId", this.getSkin().getCapeId())
                    .putByteArray("CapeData", this.getSkin().getCapeData().data)
                    .putInt("CapeImageWidth", this.getSkin().getCapeData().width)
                    .putInt("CapeImageHeight", this.getSkin().getCapeData().height)
                    .putByteArray("SkinResourcePatch", this.getSkin().getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", this.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("AnimationData", this.getSkin().getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", this.getSkin().isPremium())
                    .putBoolean("PersonaSkin", this.getSkin().isPersona())
                    .putBoolean("CapeOnClassicSkin", this.getSkin().isCapeOnClassic());
            List<SkinAnimation> animations = this.getSkin().getAnimations();
            if (!animations.isEmpty()) {
                ListTag<CompoundTag> animationsTag = new ListTag<>("AnimationImageData");
                for (SkinAnimation animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putByteArray("Image", animation.image.data));
                }
                skinTag.putList(animationsTag);
            }
            this.namedTag.putCompound("Skin", skinTag);
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && !this.hasSpawned.contains(player)) {
            this.hasSpawned.add(player);

            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (this instanceof Player)
                this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getName(), this.skin, ((Player) this).getLoginChainData().getXUID(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getName(), this.skin, new Player[]{player});

            player.dataPacket(createAddEntityPacket());

            this.inventory.sendArmorContents(player);

            if (this.riding != null) {
                SetEntityLinkPacket pkk = new SetEntityLinkPacket();
                pkk.vehicleUniqueId = this.riding.getUniqueId();
                pkk.riderUniqueId = this.getUniqueId();
                pkk.type = 1;
                pkk.immediate = 1;

                player.dataPacket(pkk);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getServerId(), new Player[]{player});
            }
        }
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddPlayerPacket packet = new AddPlayerPacket();
        packet.uuid = this.getServerId();
        packet.username = this.getName();
        packet.entityUniqueId = this.getUniqueId();
        packet.entityRuntimeId = this.getUniqueId();
        packet.x = (float) this.x;
        packet.y = (float) this.y;
        packet.z = (float) this.z;
        packet.speedX = (float) this.motionX;
        packet.speedY = (float) this.motionY;
        packet.speedZ = (float) this.motionZ;
        packet.yaw = (float) this.yaw;
        packet.pitch = (float) this.pitch;
        packet.item = this.getInventory().getItemInHand();
        packet.dataMap.putAll(this.getData());
        return packet;
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.contains(player)) {

            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.entityUniqueId = this.getUniqueId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player);
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (inventory != null && (!(this instanceof Player) || ((Player) this).loggedIn)) {
                for (Player viewer : this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory);
                }
            }

            super.close();
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.VOID && source.getCause() != EntityDamageEvent.DamageCause.CUSTOM && source.getCause() != EntityDamageEvent.DamageCause.MAGIC) {
            int armorPoints = 0;
            int epf = 0;
            int toughness = 0;

            for (Item armor : inventory.getArmorContents()) {
                armorPoints += armor.getArmorPoints();
                epf += calculateEnchantmentProtectionFactor(armor, source);
                //toughness += armor.getToughness();
            }

            if (source.canBeReducedByArmor()) {
                source.setDamage(-source.getFinalDamage() * armorPoints * 0.04f, EntityDamageEvent.DamageModifier.ARMOR);
            }

            source.setDamage(-source.getFinalDamage() * Math.min(NukkitMath.ceilFloat(Math.min(epf, 25) * ((float) ThreadLocalRandom.current().nextInt(50, 100) / 100)), 20) * 0.04f,
                    EntityDamageEvent.DamageModifier.ARMOR_ENCHANTMENTS);

            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), EntityDamageEvent.DamageModifier.ABSORPTION);
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

                if (armor.isUnbreakable()) {
                    continue;
                }

                armor.setDamage(armor.getDamage() + 1);

                if (armor.getDamage() >= armor.getMaxDurability()) {
                    inventory.setArmorItem(slot, Item.get(AIR, 0, 0));
                } else {
                    inventory.setArmorItem(slot, armor, true);
                }
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
    public Item[] getDrops() {
        if (this.inventory != null) {
            return this.inventory.getContents().values().toArray(new Item[0]);
        }
        return new Item[0];
    }

    public boolean getPlayerFlag(PlayerFlag flag) {
        byte bitSet = this.getByteData(EntityData.PLAYER_FLAGS);
        return (bitSet & (1 << flag.getId())) != 0;
    }

    public void setPlayerFlag(PlayerFlag flag, boolean value) {
        byte bitSet = this.getByteData(EntityData.PLAYER_FLAGS);
        int mask = 1 << flag.getId();
        if (value) {
            bitSet |= mask;
        } else {
            bitSet &= ~mask;
        }
        setByteData(EntityData.PLAYER_FLAGS, bitSet);
    }

    public boolean isSneaking() {
        return getFlag(SNEAKING);
    }

    public void setSneaking(boolean value) {
        this.setFlag(SNEAKING, value);
    }

    public void setSneaking() {
        this.setSneaking(true);
    }

    public boolean isSwimming() {
        return this.getFlag(SWIMMING);
    }

    public void setSwimming(boolean value) {
        this.setFlag(SWIMMING, value);
    }

    public void setSwimming() {
        this.setSwimming(true);
    }

    public boolean isSprinting() {
        return this.getFlag(SPRINTING);
    }

    public void setSprinting(boolean value) {
        this.setFlag(SPRINTING, value);
    }

    public void setSprinting() {
        this.setSprinting(true);
    }

    public boolean isGliding() {
        return this.getFlag(GLIDING);
    }

    public void setGliding(boolean value) {
        this.setFlag(GLIDING, value);
    }

    public void setGliding() {
        this.setGliding(true);
    }

}
