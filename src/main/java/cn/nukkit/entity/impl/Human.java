package cn.nukkit.entity.impl;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Location;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Utils;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityLinkPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.AIR;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Human extends EntityCreature implements InventoryHolder {

    protected UUID identity;
    private final PlayerInventory inventory = new PlayerInventory(this);
    private final PlayerEnderChestInventory enderChestInventory = new PlayerEnderChestInventory(this);

    protected SerializedSkin skin;

    public Human(EntityType<Human> type, Location location) {
        super(type, location);
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

    public SerializedSkin getSkin() {
        return skin;
    }

    public UUID getServerId() {
        return identity;
    }
    
    public void setServerId(UUID uuid) {
        this.identity = uuid;
    }

    public void setSkin(SerializedSkin skin) {
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
        this.data.setBoolean(EntityData.CAN_START_SLEEP, false);
        this.data.setFlag(HAS_GRAVITY, true);

        super.initEntity();
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        if (!(this instanceof Player)) {
            tag.listenForString("NameTag", this::setNameTag);


            if (tag.contains("Skin") && tag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = tag.getCompound("Skin");

                SerializedSkin.Builder skin = SerializedSkin.builder();

                skinTag.listenForString("ModelId", skin::skinId);
                if (skinTag.contains("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        skin.skinData(ImageData.of(width, height, data));
                    } else {
                        skin.skinData(ImageData.of(data));
                    }
                }

                skinTag.listenForString("CapeId", skin::capeId);
                if (skinTag.contains("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        skin.capeData(ImageData.of(width, height, data));
                    } else {
                        skin.capeData(ImageData.of(data));
                    }
                }
                skinTag.listenForString("GeometryName", skin::geometryName);
                skinTag.listenForString("SkinResourcePatch", skin::skinResourcePatch);
                skinTag.listenForByteArray("GeometryData", bytes -> skin.geometryData(new String(bytes, UTF_8)));
                skinTag.listenForByteArray("AnimationData", bytes -> skin.animationData(new String(bytes, UTF_8)));
                skinTag.listenForBoolean("PremiumSkin", skin::premium);
                skinTag.listenForBoolean("PersonaSkin", skin::persona);
                skinTag.listenForBoolean("CapeOnClassicSkin", skin::capeOnClassic);
                if (skinTag.contains("AnimatedImageData")) {
                    List<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                    List<AnimationData> animations = new ArrayList<>();
                    for (CompoundTag animationTag : list) {
                        float frames = animationTag.getFloat("Frames");
                        int type = animationTag.getInt("Type");
                        byte[] image = animationTag.getByteArray("Image");
                        int width = animationTag.getInt("ImageWidth");
                        int height = animationTag.getInt("ImageHeight");
                        animations.add(new AnimationData(ImageData.of(width, height, image), type, frames));
                    }
                    skin.animations(animations);
                }
                this.setSkin(skin.build());
            }

            this.identity = Utils.dataToUUID(String.valueOf(this.getUniqueId()).getBytes(UTF_8), this.getSkin()
                    .getSkinData().getImage(), this.getNameTag().getBytes(UTF_8));
        }

        tag.listenForList("Inventory", CompoundTag.class, items -> {
            for (CompoundTag itemTag : items) {
                int slot = itemTag.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, useless now
                } else if (slot >= 100 && slot < 105) {
                    this.inventory.setItem(this.inventory.getSize() + slot - 100, ItemUtils.deserializeItem(itemTag));
                } else {
                    this.inventory.setItem(slot - 9, ItemUtils.deserializeItem(itemTag));
                }
            }
        });

        tag.listenForList("EnderItems", CompoundTag.class, items -> {
            for (CompoundTag itemTag : items) {
                this.enderChestInventory.setItem(itemTag.getByte("Slot"), ItemUtils.deserializeItem(itemTag));
            }
        });
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> inventoryItems = new ArrayList<>();
        int slotCount = Player.SURVIVAL_SLOTS + 9;
        for (int slot = 9; slot < slotCount; ++slot) {
            Item item = this.inventory.getItem(slot - 9);
            if (!item.isNull()) {
                inventoryItems.add(ItemUtils.serializeItem(item, slot));
            }
        }

        for (int slot = 100; slot < 105; ++slot) {
            Item item = this.inventory.getItem(this.inventory.getSize() + slot - 100);
            if (!item.isNull()) {
                inventoryItems.add(ItemUtils.serializeItem(item, slot));
            }
        }

        tag.listTag("Inventory", CompoundTag.class, inventoryItems);

        List<CompoundTag> enderItems = new ArrayList<>();
        for (int slot = 0; slot < 27; ++slot) {
            Item item = this.enderChestInventory.getItem(slot);
            if (item != null && !item.isNull()) {
                enderItems.add(ItemUtils.serializeItem(item, slot));
            }
        }
        tag.listTag("EnderItems", CompoundTag.class, enderItems);

        if (skin != null) {
            CompoundTagBuilder skinTag = CompoundTag.builder()
                    .byteArrayTag("Data", this.getSkin().getSkinData().getImage())
                    .intTag("SkinImageWidth", this.getSkin().getSkinData().getWidth())
                    .intTag("SkinImageHeight", this.getSkin().getSkinData().getHeight())
                    .stringTag("ModelId", this.getSkin().getSkinId())
                    .stringTag("CapeId", this.getSkin().getCapeId())
                    .byteArrayTag("CapeData", this.getSkin().getCapeData().getImage())
                    .intTag("CapeImageWidth", this.getSkin().getCapeData().getWidth())
                    .intTag("CapeImageHeight", this.getSkin().getCapeData().getHeight())
                    .byteArrayTag("SkinResourcePatch", this.getSkin().getSkinResourcePatch().getBytes(UTF_8))
                    .byteArrayTag("GeometryData", this.getSkin().getGeometryData().getBytes(UTF_8))
                    .byteArrayTag("AnimationData", this.getSkin().getAnimationData().getBytes(UTF_8))
                    .booleanTag("PremiumSkin", this.getSkin().isPremium())
                    .booleanTag("PersonaSkin", this.getSkin().isPersona())
                    .booleanTag("CapeOnClassicSkin", this.getSkin().isCapeOnClassic());
            List<AnimationData> animations = this.getSkin().getAnimations();
            if (!animations.isEmpty()) {
                List<CompoundTag> animationsTag = new ArrayList<>();
                for (AnimationData animation : animations) {
                    animationsTag.add(CompoundTag.builder()
                            .floatTag("Frames", animation.getFrames())
                            .intTag("Type", animation.getType())
                            .intTag("ImageWidth", animation.getImage().getWidth())
                            .intTag("ImageHeight", animation.getImage().getHeight())
                            .byteArrayTag("Image", animation.getImage().getImage())
                            .buildRootTag());
                }
                skinTag.listTag("AnimationImageData", CompoundTag.class, animationsTag);
            }
            tag.tag(skinTag.build("Skin"));
        }
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && !this.hasSpawned.contains(player)) {
            this.hasSpawned.add(player);

            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (this instanceof Player)
                this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getName(), this.skin, ((Player) this).getXuid(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getServerId(), this.getUniqueId(), this.getName(), this.skin, new Player[]{player});

            player.sendPacket(createAddEntityPacket());

            this.inventory.sendArmorContents(player);

            if (this.vehicle != null) {
                SetEntityLinkPacket packet = new SetEntityLinkPacket();
                EntityLink link = new EntityLink(this.vehicle.getUniqueId(), this.getUniqueId(), EntityLink.Type.RIDER, true);
                packet.setEntityLink(link);

                player.sendPacket(packet);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getServerId(), new Player[]{player});
            }
        }
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        AddPlayerPacket packet = new AddPlayerPacket();
        packet.setUuid(this.getServerId());
        packet.setUsername(this.getName());
        packet.setUniqueEntityId(this.getUniqueId());
        packet.setRuntimeEntityId(this.getRuntimeId());
        packet.setPosition(this.getPosition());
        packet.setMotion(this.getMotion());
        packet.setRotation(Vector3f.from(this.getPitch(), this.getYaw(), this.getYaw()));
        packet.setHand(this.getInventory().getItemInHand().toNetwork());
        packet.setPlatformChatId("");
        packet.setDeviceId("");
        packet.getAdventureSettings().setCommandPermission(CommandPermission.NORMAL);
        packet.getAdventureSettings().setPlayerPermission(PlayerPermission.MEMBER);
        this.getData().putAllIn(packet.getMetadata());
        return packet;
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.contains(player)) {
            RemoveEntityPacket packet = new RemoveEntityPacket();
            packet.setUniqueEntityId(this.getUniqueId());
            player.sendPacket(packet);
            this.hasSpawned.remove(player);
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (!(this instanceof Player) || ((Player) this).loggedIn) {
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

                armor.setMeta(armor.getMeta() + 1);

                if (armor.getMeta() >= armor.getMaxDurability()) {
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

    public boolean isSneaking() {
        return this.data.getFlag(SNEAKING);
    }

    public void setSneaking(boolean value) {
        this.data.setFlag(SNEAKING, value);
    }

    public void setSneaking() {
        this.setSneaking(true);
    }

    public boolean isSwimming() {
        return this.data.getFlag(SWIMMING);
    }

    public void setSwimming(boolean value) {
        this.data.setFlag(SWIMMING, value);
    }

    public void setSwimming() {
        this.setSwimming(true);
    }

    public boolean isSprinting() {
        return this.data.getFlag(SPRINTING);
    }

    public void setSprinting(boolean value) {
        this.data.setFlag(SPRINTING, value);
    }

    public void setSprinting() {
        this.setSprinting(true);
    }

    public boolean isGliding() {
        return this.data.getFlag(GLIDING);
    }

    public void setGliding(boolean value) {
        this.data.setFlag(GLIDING, value);
    }

    public void setGliding() {
        this.setGliding(true);
    }

}
