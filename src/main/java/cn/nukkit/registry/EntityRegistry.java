package cn.nukkit.registry;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Human;
import cn.nukkit.entity.hostile.*;
import cn.nukkit.entity.misc.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.vehicle.*;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nukkitx.network.raknet.util.FastBinaryMinHeap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static cn.nukkit.entity.EntityTypes.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class EntityRegistry implements Registry {
    private static final EntityRegistry INSTANCE;

    private static final BiMap<String, Identifier> LEGACY_NAMES;
    private static final List<CompoundTag> VANILLA_ENTITIES;

    static {
        try (InputStream stream = RegistryUtils.getOrAssertResource("legacy/entity_names.json")) {
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> legacyNames = gson.fromJson(reader, collectionType);

            ImmutableBiMap.Builder<String, Identifier> mapBuilder = ImmutableBiMap.builder();

            legacyNames.forEach((name, identifier) -> mapBuilder.put(name, Identifier.fromString(identifier)));
            LEGACY_NAMES = mapBuilder.build();
        } catch (IOException e) {
            throw new AssertionError("Unable to close resource stream", e);
        }

        try (InputStream stream = RegistryUtils.getOrAssertResource("entity_identifiers.dat")) {
            CompoundTag tag = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN, true);
            ListTag<CompoundTag> vanillaEntities = tag.getList("idlist", CompoundTag.class);
            VANILLA_ENTITIES = vanillaEntities.getAll();
        } catch (IOException e) {
            throw new AssertionError("Unable to close resource stream", e);
        }

        INSTANCE = new EntityRegistry();
    }

    private final BiMap<Identifier, EntityType<?>> identifierTypeMap = HashBiMap.create();
    private final AtomicLong entityIdAllocator = new AtomicLong();
    private final Map<EntityType<?>, EntityData> dataMap = new IdentityHashMap<>();
    private final Int2ObjectMap<EntityType<?>> runtimeTypeMap = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<EntityType<?>> typeToRuntimeMap = new Object2IntLinkedOpenHashMap<>();
    private final int customEntityStart;
    private int runtimeTypeAllocator;
    private volatile boolean closed;
    private byte[] cachedEntityIdentifiers;

    private EntityRegistry() {
        this.registerVanillaEntities();
        customEntityStart = runtimeTypeAllocator;
    }

    public static EntityRegistry get() {
        return INSTANCE;
    }

    public synchronized <T extends Entity> void register(EntityType<T> type, EntityFactory<T> factory, int weight,
                                                         boolean hasSpawnEgg) {
        this.registerInternal(type, factory, this.runtimeTypeAllocator++, weight, hasSpawnEgg);
    }

    private <T extends Entity> void registerVanilla(EntityType<T> type, EntityFactory<T> factory, int legacyId) {
        this.registerInternal(type, factory, legacyId, 100, false); // Vanilla NBT decides
    }

    private synchronized <T extends Entity> void registerInternal(EntityType<T> type, EntityFactory<T> factory,
                                                                  int runtimeType, int weight, boolean hasSpawnEgg)
            throws RegistryException {
        checkClosed();
        checkNotNull(type, "type");
        checkNotNull(factory, "factory");
        EntityType<?> existingType = this.identifierTypeMap.get(type.getIdentifier());

        if (existingType == null) { // new
            if (runtimeType >= this.runtimeTypeAllocator) {
                this.runtimeTypeAllocator = runtimeType + 1;
            }

            this.runtimeTypeMap.put(runtimeType, type);
            this.typeToRuntimeMap.put(type, runtimeType);
            this.identifierTypeMap.put(type.getIdentifier(), type);
            EntityData entityData = new EntityData(hasSpawnEgg);
            entityData.factories.insert(weight, factory);
            this.dataMap.put(type, entityData);
        } else if (existingType == type) { // existing
            this.dataMap.get(type).factories.insert(weight, factory);
        } else { // invalid
            throw new RegistryException(type.getIdentifier() + " is already registered");
        }
    }

    public int getRuntimeType(EntityType<?> type) {
        return typeToRuntimeMap.getOrDefault(type, -1);
    }

    public EntityType<?> getEntityType(int runtimeId) {
        return runtimeTypeMap.get(runtimeId);
    }

    public EntityType<?> getEntityType(Identifier identifier) {
        return identifierTypeMap.get(identifier);
    }

    /**
     * Creates new entity of given type
     *
     * @param type  entity type
     * @param chunk chunk entity is in
     * @param tag   compound tag with entity data
     * @param <T>   entity class type
     * @return new entity
     */
    public <T extends Entity> T newEntity(EntityType<T> type, Chunk chunk, CompoundTag tag) {
        checkNotNull(type, "type");
        checkNotNull(chunk, "chunk");
        checkNotNull(tag, "tag");
        FastBinaryMinHeap<EntityFactory<?>> factories = this.dataMap.get(type).factories;
        if (factories == null) {
            throw new RegistryException(type.getIdentifier() + " is not a registered entity");
        }
        //noinspection unchecked
        return ((EntityFactory<T>) factories.peek()).create(type, chunk, tag);
    }

    /**
     * Allocate new entity ID
     *
     * @return entity ID
     */
    public long newEntityId() {
        return this.entityIdAllocator.getAndIncrement();
    }

    public Identifier getIdentifier(String legacyName) {
        return LEGACY_NAMES.get(legacyName);
    }

    public String getLegacyName(Identifier identifier) {
        return LEGACY_NAMES.inverse().get(identifier);
    }

    public byte[] getCachedEntityIdentifiers() {
        return cachedEntityIdentifiers;
    }

    @Override
    public synchronized void close() throws RegistryException {
        checkClosed();
        this.closed = true;

        // generate cache

        List<CompoundTag> entityIdentifiers = new ArrayList<>(VANILLA_ENTITIES);

        for (int id = customEntityStart; id < runtimeTypeAllocator; id++) {
            EntityType<?> type = this.runtimeTypeMap.get(id);
            EntityData data = this.dataMap.get(type);

            entityIdentifiers.add(new CompoundTag()
                    .putBoolean("summonable", true) // TODO: 07/01/2020 This affects the summon command auto completion
                    .putBoolean("hasSpawnEgg", data.hasSpawnEgg)
                    .putBoolean("experimental", true) // If there are experimental features, we may as well enable them
                    .putString("id", type.getIdentifier().toString())
                    .putString("bid", "") // ???
                    .putInt("rid", id)
            );
        }

        ListTag<CompoundTag> idList = new ListTag<>("idlist");
        idList.setAll(entityIdentifiers);

        try {
            this.cachedEntityIdentifiers = NBTIO.write(new CompoundTag().putList(idList), ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RegistryException("Unable to create entity identifiers cache");
        }
    }

    private void checkClosed() {
        checkState(!closed, "Registration is already closed");
    }

    private void registerVanillaEntities() {
        registerVanilla(CHICKEN, Chicken::new, 10);
        registerVanilla(COW, Cow::new, 11);
        registerVanilla(PIG, Pig::new, 12);
        registerVanilla(SHEEP, Sheep::new, 13);
        registerVanilla(WOLF, Wolf::new, 14);
        registerVanilla(DEPRECATED_VILLAGER, DeprecatedVillager::new, 15);
        registerVanilla(MOOSHROOM, Mooshroom::new, 16);
        registerVanilla(SQUID, Squid::new, 17);
        registerVanilla(RABBIT, Rabbit::new, 18);
        registerVanilla(BAT, Bat::new, 19);
        registerVanilla(OCELOT, Ocelot::new, 22);
        registerVanilla(HORSE, Horse::new, 23);
        registerVanilla(DONKEY, Donkey::new, 24);
        registerVanilla(MULE, Mule::new, 25);
        registerVanilla(SKELETON_HORSE, SkeletonHorse::new, 26);
        registerVanilla(ZOMBIE_HORSE, ZombieHorse::new, 27);
        registerVanilla(POLAR_BEAR, PolarBear::new, 28);
        registerVanilla(LLAMA, Llama::new, 29);
        registerVanilla(PARROT, Parrot::new, 30);
        registerVanilla(DOLPHIN, Dolphin::new, 31);
        registerVanilla(ZOMBIE, Zombie::new, 32);
        registerVanilla(CREEPER, Creeper::new, 33);
        registerVanilla(SKELETON, Skeleton::new, 34);
        registerVanilla(SPIDER, Spider::new, 35);
        registerVanilla(ZOMBIE_PIGMAN, ZombiePigman::new, 36);
        registerVanilla(SLIME, Slime::new, 37);
        registerVanilla(ENDERMAN, Enderman::new, 38);
        registerVanilla(SILVERFISH, Silverfish::new, 39);
        registerVanilla(CAVE_SPIDER, CaveSpider::new, 40);
        registerVanilla(GHAST, Ghast::new, 41);
        registerVanilla(MAGMA_CUBE, MagmaCube::new, 42);
        registerVanilla(BLAZE, Blaze::new, 43);
        registerVanilla(DEPRECATED_ZOMBIE_VILLAGER, DeprecatedZombieVillager::new, 44);
        registerVanilla(WITCH, Witch::new, 45);
        registerVanilla(STRAY, Stray::new, 46);
        registerVanilla(HUSK, Husk::new, 47);
        registerVanilla(WITHER_SKELETON, WitherSkeleton::new, 48);
        registerVanilla(GUARDIAN, Guardian::new, 49);
        registerVanilla(ELDER_GUARDIAN, ElderGuardian::new, 50);
        registerVanilla(WITHER, Wither::new, 52);
        registerVanilla(ENDER_DRAGON, EnderDragon::new, 53);
        registerVanilla(SHULKER, Shulker::new, 54);
        registerVanilla(ENDERMITE, Endermite::new, 55);
        registerVanilla(VINDICATOR, Vindicator::new, 57);
        registerVanilla(PHANTOM, Phantom::new, 58);
        registerVanilla(RAVAGER, Ravager::new, 59);
        registerVanilla(PLAYER, Human::new, 63);
        registerVanilla(ITEM, DroppedItem::new, 64);
        registerVanilla(TNT, Tnt::new, 65);
        registerVanilla(FALLING_BLOCK, FallingBlock::new, 66);
        registerVanilla(XP_BOTTLE, XpBottle::new, 68);
        registerVanilla(XP_ORB, XpOrb::new, 69);
        registerVanilla(ENDER_CRYSTAL, EnderCrystal::new, 71);
        registerVanilla(FIREWORKS_ROCKET, FireworksRocket::new, 72);
        registerVanilla(THROWN_TRIDENT, ThrownTrident::new, 73);
        registerVanilla(TURTLE, Turtle::new, 74);
        registerVanilla(CAT, Cat::new, 75);
        registerVanilla(FISHING_HOOK, FishingHook::new, 77);
        registerVanilla(ARROW, Arrow::new, 80);
        registerVanilla(SNOWBALL, Snowball::new, 81);
        registerVanilla(EGG, Egg::new, 82);
        registerVanilla(PAINTING, Painting::new, 83);
        registerVanilla(MINECART, Minecart::new, 84);
        registerVanilla(SPLASH_POTION, SplashPotion::new, 86);
        registerVanilla(ENDER_PEARL, EnderPearl::new, 87);
        registerVanilla(BOAT, Boat::new, 90);
        registerVanilla(LIGHTNING_BOLT, LightningBolt::new, 93);
        registerVanilla(HOPPER_MINECART, HopperMinecart::new, 96);
        registerVanilla(TNT_MINECART, TntMinecart::new, 97);
        registerVanilla(CHEST_MINECART, ChestMinecart::new, 98);
        registerVanilla(EVOCATION_ILLAGER, EvocationIllager::new, 104);
        registerVanilla(VEX, Vex::new, 105);
        registerVanilla(PUFFERFISH, Pufferfish::new, 108);
        registerVanilla(SALMON, Salmon::new, 109);
        registerVanilla(DROWNED, Drowned::new, 110);
        registerVanilla(TROPICALFISH, TropicalFish::new, 111);
        registerVanilla(COD, Cod::new, 112);
        registerVanilla(PANDA, Panda::new, 113);
        registerVanilla(PILLAGER, Pillager::new, 114);
        registerVanilla(VILLAGER, Villager::new, 115);
        registerVanilla(ZOMBIE_VILLAGER, ZombieVillager::new, 116);
        registerVanilla(WANDERING_TRADER, WanderingTrader::new, 118);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class EntityData {
        private final boolean hasSpawnEgg;
        private final FastBinaryMinHeap<EntityFactory<?>> factories = new FastBinaryMinHeap<>();
    }
}
