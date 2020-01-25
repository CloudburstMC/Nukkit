package cn.nukkit.registry;

import cn.nukkit.Nukkit;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFactory;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.Human;
import cn.nukkit.entity.impl.hostile.*;
import cn.nukkit.entity.impl.misc.*;
import cn.nukkit.entity.impl.passive.*;
import cn.nukkit.entity.impl.projectile.*;
import cn.nukkit.entity.impl.vehicle.*;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

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
            Map<String, String> legacyNames = Nukkit.JSON_MAPPER.readValue(stream, new TypeReference<Map<String, String>>() {});

            ImmutableBiMap.Builder<String, Identifier> mapBuilder = ImmutableBiMap.builder();

            legacyNames.forEach((name, identifier) -> mapBuilder.put(name, Identifier.fromString(identifier)));
            LEGACY_NAMES = mapBuilder.build();
        } catch (IOException e) {
            throw new AssertionError("Unable to load legacy entity names", e);
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
    private final Map<EntityType<?>, EntityData<?>> dataMap = new IdentityHashMap<>();
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

    public synchronized <T extends Entity> void register(Plugin plugin, EntityType<T> type, EntityFactory<T> factory,
                                                         int priority, boolean hasSpawnEgg) {
        this.registerInternal(plugin, type, factory, this.runtimeTypeAllocator++, priority, hasSpawnEgg);
    }

    private <T extends Entity> void registerVanilla(EntityType<T> type, EntityFactory<T> factory, int legacyId) {
        this.registerInternal(null, type, factory, legacyId, 1000, false); // Vanilla NBT decides
    }

    private synchronized <T extends Entity> void registerInternal(Plugin plugin, EntityType<T> type, EntityFactory<T> factory,
                                                                  int runtimeType, int priority, boolean hasSpawnEgg)
            throws RegistryException {
        checkClosed();
        checkNotNull(type, "type");
        checkNotNull(factory, "factory");
        EntityType<?> existingType = this.identifierTypeMap.get(type.getIdentifier());

        if (existingType == null) { // new entity
            if (runtimeType >= this.runtimeTypeAllocator) {
                this.runtimeTypeAllocator = runtimeType + 1;
            }

            this.runtimeTypeMap.put(runtimeType, type);
            this.typeToRuntimeMap.put(type, runtimeType);
            this.identifierTypeMap.put(type.getIdentifier(), type);

            EntityData<T> entityData = new EntityData<>(hasSpawnEgg, new RegistryProvider<>(factory, plugin, priority));
            this.dataMap.put(type, entityData);
        } else if (existingType == type) { // existing - add plugin's factory if one does not exist
            RegistryProvider<EntityFactory<T>> provider = new RegistryProvider<>(factory, plugin, priority);
            //noinspection unchecked
            ((EntityData<T>) this.dataMap.get(type)).serviceProvider.add(provider);
        } else { // invalid - registering EntityType with used identifier.
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
        checkState(closed, "Cannot create entity till registry is closed");
        checkNotNull(type, "type");
        checkNotNull(chunk, "chunk");
        checkNotNull(tag, "tag");
        EntityFactory<T> factory = getServiceProvider(type).getProvider().getValue();
        return factory.create(type, chunk, tag);
    }

    /**
     * Creates new entity of given type from specific plugin factory
     *
     * @param type  entity type
     * @param chunk chunk entity is in
     * @param tag   compound tag with entity data
     * @param <T>   entity class type
     * @return new entity
     */
    public <T extends Entity> T newEntity(EntityType<T> type, Plugin plugin, Chunk chunk, CompoundTag tag) {
        checkState(closed, "Cannot create entity till registry is closed");
        checkNotNull(type, "type");
        checkNotNull(plugin, "plugin");
        checkNotNull(chunk, "chunk");
        checkNotNull(tag, "tag");
        RegistryProvider<EntityFactory<T>> provider = getServiceProvider(type).getProvider(plugin);
        if (provider == null) {
            throw new RegistryException("Plugin has no registered provider for " + type.getIdentifier());
        }
        return provider.getValue().create(type, chunk, tag);
    }

    /**
     * Allocate new entity ID
     *
     * @return entity ID
     */
    public long newEntityId() {
        return this.entityIdAllocator.incrementAndGet();
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

    public ImmutableSet<EntityType<?>> getEntityTypes() {
        return ImmutableSet.copyOf(this.identifierTypeMap.values());
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> RegistryServiceProvider<EntityFactory<T>> getServiceProvider(EntityType<T> type) {
        EntityData<T> entityData = (EntityData<T>) this.dataMap.get(type);
        if (entityData == null) {
            throw new RegistryException(type.getIdentifier() + " is not a registered entity");
        }
        return entityData.serviceProvider;
    }

    @Override
    public synchronized void close() throws RegistryException {
        checkClosed();

        // Bake registry providers
        this.dataMap.values().forEach(entityData -> entityData.serviceProvider.bake());

        // generate cache

        List<CompoundTag> entityIdentifiers = new ArrayList<>(VANILLA_ENTITIES);

        for (int id = customEntityStart; id < runtimeTypeAllocator; id++) {
            EntityType<?> type = this.runtimeTypeMap.get(id);
            EntityData<?> data = this.dataMap.get(type);

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
        this.closed = true;
    }

    private void checkClosed() {
        checkState(!closed, "Registration is already closed");
    }

    private void registerVanillaEntities() {
        registerVanilla(CHICKEN, EntityChicken::new, 10);
        registerVanilla(COW, EntityCow::new, 11);
        registerVanilla(PIG, EntityPig::new, 12);
        registerVanilla(SHEEP, EntitySheep::new, 13);
        registerVanilla(WOLF, EntityWolf::new, 14);
        registerVanilla(DEPRECATED_VILLAGER, EntityDeprecatedVillager::new, 15);
        registerVanilla(MOOSHROOM, EntityMooshroom::new, 16);
        registerVanilla(SQUID, EntitySquid::new, 17);
        registerVanilla(RABBIT, EntityRabbit::new, 18);
        registerVanilla(BAT, EntityBat::new, 19);
        registerVanilla(OCELOT, EntityOcelot::new, 22);
        registerVanilla(HORSE, EntityHorse::new, 23);
        registerVanilla(DONKEY, EntityDonkey::new, 24);
        registerVanilla(MULE, EntityMule::new, 25);
        registerVanilla(SKELETON_HORSE, EntitySkeletonHorse::new, 26);
        registerVanilla(ZOMBIE_HORSE, EntityZombieHorse::new, 27);
        registerVanilla(POLAR_BEAR, EntityPolarBear::new, 28);
        registerVanilla(LLAMA, EntityLlama::new, 29);
        registerVanilla(PARROT, EntityParrot::new, 30);
        registerVanilla(DOLPHIN, EntityDolphin::new, 31);
        registerVanilla(ZOMBIE, EntityZombie::new, 32);
        registerVanilla(CREEPER, EntityCreeper::new, 33);
        registerVanilla(SKELETON, EntitySkeleton::new, 34);
        registerVanilla(SPIDER, EntitySpider::new, 35);
        registerVanilla(ZOMBIE_PIGMAN, EntityZombiePigman::new, 36);
        registerVanilla(SLIME, EntitySlime::new, 37);
        registerVanilla(ENDERMAN, EntityEnderman::new, 38);
        registerVanilla(SILVERFISH, EntitySilverfish::new, 39);
        registerVanilla(CAVE_SPIDER, EntityCaveSpider::new, 40);
        registerVanilla(GHAST, EntityGhast::new, 41);
        registerVanilla(MAGMA_CUBE, EntityMagmaCube::new, 42);
        registerVanilla(BLAZE, EntityBlaze::new, 43);
        registerVanilla(DEPRECATED_ZOMBIE_VILLAGER, EntityDeprecatedZombieVillager::new, 44);
        registerVanilla(WITCH, EntityWitch::new, 45);
        registerVanilla(STRAY, EntityStray::new, 46);
        registerVanilla(HUSK, EntityHusk::new, 47);
        registerVanilla(WITHER_SKELETON, EntityWitherSkeleton::new, 48);
        registerVanilla(GUARDIAN, EntityGuardian::new, 49);
        registerVanilla(ELDER_GUARDIAN, EntityElderGuardian::new, 50);
        registerVanilla(WITHER, EntityWither::new, 52);
        registerVanilla(ENDER_DRAGON, EntityEnderDragon::new, 53);
        registerVanilla(SHULKER, EntityShulker::new, 54);
        registerVanilla(ENDERMITE, EntityEndermite::new, 55);
        registerVanilla(VINDICATOR, EntityVindicator::new, 57);
        registerVanilla(PHANTOM, EntityPhantom::new, 58);
        registerVanilla(RAVAGER, EntityRavager::new, 59);
        registerVanilla(PLAYER, Human::new, 63);
        registerVanilla(ITEM, EntityDroppedItem::new, 64);
        registerVanilla(TNT, EntityTnt::new, 65);
        registerVanilla(FALLING_BLOCK, EntityFallingBlock::new, 66);
        registerVanilla(XP_BOTTLE, EntityXpBottle::new, 68);
        registerVanilla(XP_ORB, EntityXpOrb::new, 69);
        registerVanilla(ENDER_CRYSTAL, EntityEnderCrystal::new, 71);
        registerVanilla(FIREWORKS_ROCKET, EntityFireworksRocket::new, 72);
        registerVanilla(THROWN_TRIDENT, EntityThrownTrident::new, 73);
        registerVanilla(TURTLE, EntityTurtle::new, 74);
        registerVanilla(CAT, EntityCat::new, 75);
        registerVanilla(FISHING_HOOK, EntityFishingHook::new, 77);
        registerVanilla(ARROW, EntityArrow::new, 80);
        registerVanilla(SNOWBALL, EntitySnowball::new, 81);
        registerVanilla(EGG, EntityEgg::new, 82);
        registerVanilla(PAINTING, EntityPainting::new, 83);
        registerVanilla(MINECART, EntityMinecart::new, 84);
        registerVanilla(SPLASH_POTION, EntitySplashPotion::new, 86);
        registerVanilla(ENDER_PEARL, EntityEnderPearl::new, 87);
        registerVanilla(BOAT, EntityBoat::new, 90);
        registerVanilla(LIGHTNING_BOLT, EntityLightningBolt::new, 93);
        registerVanilla(HOPPER_MINECART, EntityHopperMinecart::new, 96);
        registerVanilla(TNT_MINECART, EntityTntMinecart::new, 97);
        registerVanilla(CHEST_MINECART, EntityChestMinecart::new, 98);
        registerVanilla(EVOCATION_ILLAGER, EntityEvocationIllager::new, 104);
        registerVanilla(VEX, EntityVex::new, 105);
        registerVanilla(PUFFERFISH, EntityPufferfish::new, 108);
        registerVanilla(SALMON, EntitySalmon::new, 109);
        registerVanilla(DROWNED, EntityDrowned::new, 110);
        registerVanilla(TROPICALFISH, EntityTropicalFish::new, 111);
        registerVanilla(COD, EntityCod::new, 112);
        registerVanilla(PANDA, EntityPanda::new, 113);
        registerVanilla(PILLAGER, EntityPillager::new, 114);
        registerVanilla(VILLAGER, EntityVillager::new, 115);
        registerVanilla(ZOMBIE_VILLAGER, EntityZombieVillager::new, 116);
        registerVanilla(WANDERING_TRADER, EntityWanderingTrader::new, 118);
    }

    private static class EntityData<T extends Entity> {
        private final boolean hasSpawnEgg;
        private final RegistryServiceProvider<EntityFactory<T>> serviceProvider;

        private EntityData(boolean hasSpawnEgg, RegistryProvider<EntityFactory<T>> provider) {
            this.hasSpawnEgg = hasSpawnEgg;
            this.serviceProvider = new RegistryServiceProvider<>(provider);
        }
    }
}
