package cn.nukkit.registry;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlastFurnace;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityFactory;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.impl.*;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.plugin.Plugin;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nukkitx.math.vector.Vector3i;

import javax.annotation.Nonnull;
import java.util.IdentityHashMap;
import java.util.Map;

import static cn.nukkit.blockentity.BlockEntityTypes.*;
import static com.google.common.base.Preconditions.*;

public class BlockEntityRegistry implements Registry {
    private static final BlockEntityRegistry INSTANCE = new BlockEntityRegistry();

    private final Map<BlockEntityType<?>, RegistryServiceProvider<BlockEntityFactory<?>>> providers = new IdentityHashMap<>();
    private final BiMap<BlockEntityType<?>, String> persistentMap = HashBiMap.create();
    private volatile boolean closed;

    private BlockEntityRegistry() {
        this.registerVanillaEntities();
    }

    public static BlockEntityRegistry get() {
        return INSTANCE;
    }

    private <T extends BlockEntity> void registerVanilla(BlockEntityType<T> type, BlockEntityFactory<? extends T> factory, String persistentId) {
        checkNotNull(type, "type");
        checkNotNull(factory, "factory");
        checkNotNull(persistentId, "persistentId");

        this.persistentMap.put(type, persistentId);
        this.providers.put(type, new RegistryServiceProvider<>(new RegistryProvider<>(factory, null, 1000)));
    }

    public synchronized <T extends BlockEntity> void register(Plugin plugin, BlockEntityType<T> type,
                                                              BlockEntityFactory<T> factory, int priority) throws RegistryException {
        checkClosed();
        checkNotNull(type, "type");
        checkNotNull(factory, "factory");
        checkArgument(this.providers.containsKey(type), "Undefined BlockEntityType %", type);

        //noinspection unchecked,rawtypes
        RegistryServiceProvider<BlockEntityFactory<T>> service = (RegistryServiceProvider) this.providers.get(type);
        service.add(new RegistryProvider<>(factory, plugin, priority));

    }

    public String getPersistentId(BlockEntityType<?> type) {
        return persistentMap.get(type);
    }

    @Nonnull
    public BlockEntityType<?> getBlockEntityType(String persistentId) {
        BlockEntityType<?> type = persistentMap.inverse().get(persistentId);
        if (type == null) {
            throw new RegistryException("No BlockEntityType exists for id: " + persistentId);
        }
        return type;
    }

    /**
     * Creates new entity of given type
     *
     * @param type     entity type
     * @param chunk    chunk of block entity
     * @param position position of block entity in world
     * @param <T>      entity class type
     * @return new entity
     */
    public <T extends BlockEntity> T newEntity(BlockEntityType<T> type, Chunk chunk, Vector3i position) {
        checkState(closed, "Cannot create entity till registry is closed");
        checkNotNull(type, "type");
        checkNotNull(chunk, "chunk");
        checkNotNull(position, "position");
        BlockEntityFactory<T> factory = getServiceProvider(type).getProvider().getValue();
        return factory.create(type, chunk, position);
    }

    /**
     * Creates new entity of given type from specific plugin factory
     *
     * @param type     entity type
     * @param chunk    chunk of block entity
     * @param position position of block entity in world
     * @param <T>      entity class type
     * @return new entity
     */
    public <T extends BlockEntity> T newEntity(BlockEntityType<T> type, Plugin plugin, Chunk chunk, Vector3i position) {
        checkState(closed, "Cannot create entity till registry is closed");
        checkNotNull(type, "type");
        checkNotNull(plugin, "plugin");
        checkNotNull(chunk, "chunk");
        checkNotNull(position, "position");
        RegistryProvider<BlockEntityFactory<T>> provider = getServiceProvider(type).getProvider(plugin);
        if (provider == null) {
            throw new RegistryException("Plugin has no registered provider for " + type.getIdentifier());
        }
        return provider.getValue().create(type, chunk, position);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T extends BlockEntity> RegistryServiceProvider<BlockEntityFactory<T>> getServiceProvider(BlockEntityType<T> type) {
        RegistryServiceProvider<BlockEntityFactory<T>> service = (RegistryServiceProvider) this.providers.get(type);
        if (service == null) {
            throw new RegistryException(type.getIdentifier() + " is not a registered entity");
        }
        return service;
    }

    @Override
    public synchronized void close() throws RegistryException {
        checkClosed();

        // Bake registry providers
        this.providers.values().forEach(RegistryServiceProvider::bake);

        this.closed = true;
    }

    private void checkClosed() {
        checkState(!closed, "Registration is already closed");
    }

    private void registerVanillaEntities() {
        registerVanilla(CHEST, ChestBlockEntity::new, "Chest");
        registerVanilla(ENDER_CHEST, EnderChestBlockEntity::new, "EnderChest");
        registerVanilla(FURNACE, FurnaceBlockEntity::new, "Furnace");
        registerVanilla(SIGN, SignBlockEntity::new, "Sign");
        //registerVanilla(MOB_SPAWNER, MobSpawnerBlockEntity::new, "MobSpawner");
        registerVanilla(ENCHANTING_TABLE, EnchantingTableBlockEntity::new, "EnchantTable");
        registerVanilla(SKULL, SkullBlockEntity::new, "Skull");
        registerVanilla(FLOWER_POT, FlowerPotBlockEntity::new, "FlowerPot");
        registerVanilla(BREWING_STAND, BrewingStandBlockEntity::new, "BrewingStand");
        registerVanilla(DAYLIGHT_DETECTOR, DaylightDetectorBlockEntity::new, "DaylightDetector");
        registerVanilla(NOTEBLOCK, MusicBlockEntity::new, "Music");
        registerVanilla(ITEM_FRAME, ItemFrameBlockEntity::new, "ItemFrame");
        registerVanilla(CAULDRON, CauldronBlockEntity::new, "Cauldron");
        registerVanilla(BEACON, BeaconBlockEntity::new, "Beacon");
        registerVanilla(PISTON, PistonBlockEntity::new, "PistonArm");
        registerVanilla(MOVING_BLOCK, MovingBlockEntity::new, "MovingBlock");
        registerVanilla(COMPARATOR, ComparatorBlockEntity::new, "Comparator");
        registerVanilla(HOPPER, HopperBlockEntity::new, "Hopper");
        registerVanilla(BED, BedBlockEntity::new, "Bed");
        registerVanilla(JUKEBOX, JukeboxBlockEntity::new, "Jukebox");
        registerVanilla(SHULKER_BOX, ShulkerBoxBlockEntity::new, "ShulkerBox");
        registerVanilla(BANNER, BannerBlockEntity::new, "Banner");
        registerVanilla(CAMPFIRE, CampfireBlockEntity::new, "Campfire");
        registerVanilla(BLAST_FURNACE, BlastFurnaceBlockEntity::new, "BlastFurnace");
        registerVanilla(SMOKER, SmokerBlockEntity::new, "Smoker");
        registerVanilla(BARREL, BarrelBlockEntity::new, "Barrel");
    }
}