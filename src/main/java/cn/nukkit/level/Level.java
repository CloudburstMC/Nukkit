package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.EntityDefinition;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.GeneratorTaskFactory;
import cn.nukkit.level.generator.PopChunkManager;
import cn.nukkit.level.generator.task.GenerationTask;
import cn.nukkit.level.generator.task.PopulationTask;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.persistence.PersistentDataContainer;
import cn.nukkit.level.persistence.impl.DelegatePersistentDataContainer;
import cn.nukkit.math.*;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.BlockUpdateScheduler;
import cn.nukkit.utils.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.*;


/**
 * @author MagicDroidX Nukkit Project
 */
public class Level implements ChunkManager, Metadatable, GeneratorTaskFactory {

    private static int levelIdCounter = 1;
    private static int chunkLoaderCounter = 1;

    public static final int BLOCK_UPDATE_NORMAL = 1;
    public static final int BLOCK_UPDATE_RANDOM = 2;
    public static final int BLOCK_UPDATE_SCHEDULED = 3;
    public static final int BLOCK_UPDATE_WEAK = 4;
    public static final int BLOCK_UPDATE_TOUCH = 5;
    public static final int BLOCK_UPDATE_REDSTONE = 6;
    public static final int BLOCK_UPDATE_TICK = 7;

    public static final int TIME_DAY = 0;
    public static final int TIME_NOON = 6000;
    public static final int TIME_SUNSET = 12000;
    public static final int TIME_NIGHT = 14000;
    public static final int TIME_MIDNIGHT = 18000;
    public static final int TIME_SUNRISE = 23000;

    public static final int TIME_FULL = 24000;

    public static final int DIMENSION_OVERWORLD = 0;
    public static final int DIMENSION_NETHER = 1;
    public static final int DIMENSION_THE_END = 2;

    public static final int MAX_BLOCK_CACHE = 1024;

    private static final int LCG_CONSTANT = 1013904223;

    /**
     * The blocks that can be randomly ticked
     */
    public static final boolean[] randomTickBlocks = new boolean[Block.MAX_BLOCK_ID];

    static {
        randomTickBlocks[Block.GRASS] = true;
        randomTickBlocks[Block.FARMLAND] = true;
        randomTickBlocks[Block.MYCELIUM] = true;
        randomTickBlocks[Block.SAPLING] = true;
        randomTickBlocks[Block.LEAVES] = true;
        randomTickBlocks[Block.LEAVES2] = true;
        randomTickBlocks[Block.SNOW_LAYER] = true;
        randomTickBlocks[Block.ICE] = true;
        randomTickBlocks[Block.LAVA] = true;
        randomTickBlocks[Block.STILL_LAVA] = true;
        randomTickBlocks[Block.CACTUS] = true;
        randomTickBlocks[Block.BEETROOT_BLOCK] = true;
        randomTickBlocks[Block.CARROT_BLOCK] = true;
        randomTickBlocks[Block.POTATO_BLOCK] = true;
        randomTickBlocks[Block.MELON_STEM] = true;
        randomTickBlocks[Block.PUMPKIN_STEM] = true;
        randomTickBlocks[Block.WHEAT_BLOCK] = true;
        randomTickBlocks[Block.SUGARCANE_BLOCK] = true;
        randomTickBlocks[Block.NETHER_WART_BLOCK] = true;
        randomTickBlocks[Block.FIRE] = true;
        randomTickBlocks[Block.GLOWING_REDSTONE_ORE] = true;
        randomTickBlocks[Block.COCOA_BLOCK] = true;
        randomTickBlocks[Block.ICE_FROSTED] = true;
        randomTickBlocks[Block.VINE] = true;
        randomTickBlocks[Block.WATER] = true;
        randomTickBlocks[Block.CAULDRON_BLOCK] = true;
        randomTickBlocks[Block.CHORUS_FLOWER] = true;
        randomTickBlocks[Block.SWEET_BERRY_BUSH] = true;
        randomTickBlocks[Block.BAMBOO_SAPLING] = true;
        randomTickBlocks[Block.BAMBOO] = true;
        randomTickBlocks[Block.CORAL_FAN] = true;
        randomTickBlocks[Block.CORAL_FAN_DEAD] = true;
        randomTickBlocks[Block.BLOCK_KELP] = true;
        randomTickBlocks[BlockID.COPPER_BLOCK] = true;
        randomTickBlocks[BlockID.EXPOSED_COPPER] = true;
        randomTickBlocks[BlockID.WEATHERED_COPPER] = true;
        randomTickBlocks[BlockID.WAXED_COPPER] = true;
        randomTickBlocks[BlockID.CUT_COPPER] = true;
        randomTickBlocks[BlockID.EXPOSED_CUT_COPPER] = true;
        randomTickBlocks[BlockID.WEATHERED_CUT_COPPER] = true;
        randomTickBlocks[BlockID.CUT_COPPER_STAIRS] = true;
        randomTickBlocks[BlockID.EXPOSED_CUT_COPPER_STAIRS] = true;
        randomTickBlocks[BlockID.WEATHERED_CUT_COPPER_STAIRS] = true;
        randomTickBlocks[BlockID.CUT_COPPER_SLAB] = true;
        randomTickBlocks[BlockID.EXPOSED_CUT_COPPER_SLAB] = true;
        randomTickBlocks[BlockID.WEATHERED_CUT_COPPER_SLAB] = true;
        randomTickBlocks[BlockID.DOUBLE_CUT_COPPER_SLAB] = true;
        randomTickBlocks[BlockID.EXPOSED_DOUBLE_CUT_COPPER_SLAB] = true;
        randomTickBlocks[BlockID.WEATHERED_DOUBLE_CUT_COPPER_SLAB] = true;
        randomTickBlocks[BlockID.BUDDING_AMETHYST] = true;
        randomTickBlocks[BlockID.CAVE_VINES] = true;
        randomTickBlocks[BlockID.CAVE_VINES_BODY_WITH_BERRIES] = true;
        randomTickBlocks[BlockID.CAVE_VINES_HEAD_WITH_BERRIES] = true;
        randomTickBlocks[BlockID.AZALEA_LEAVES] = true;
        randomTickBlocks[BlockID.AZALEA_LEAVES_FLOWERED] = true;
        randomTickBlocks[BlockID.POINTED_DRIPSTONE] = true;
    }

    private final Long2ObjectOpenHashMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Player> players = new Long2ObjectOpenHashMap<>();

    public final Long2ObjectOpenHashMap<Entity> entities = new Long2ObjectOpenHashMap<>();

    public final Long2ObjectOpenHashMap<Entity> updateEntities = new Long2ObjectOpenHashMap<>();

    private final ConcurrentLinkedQueue<BlockEntity> updateBlockEntities = new ConcurrentLinkedQueue<>();

    private final Server server;

    private final int levelId;

    private LevelProvider provider;

    private final Int2ObjectMap<ChunkLoader> loaders = new Int2ObjectOpenHashMap<>();

    private final Int2IntMap loaderCounter = new Int2IntOpenHashMap();

    private final Long2ObjectOpenHashMap<Map<Integer, ChunkLoader>> chunkLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Map<Integer, Player>> playerLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Deque<DataPacket>> chunkPackets = new Long2ObjectOpenHashMap<>();

    private final Long2LongMap unloadQueue = Long2LongMaps.synchronize(new Long2LongOpenHashMap());

    private int time;
    public boolean stopTime;
    public int sleepTicks;
    public float skyLightSubtracted;
    @Getter
    @Setter
    public boolean lightUpdatesEnabled = true;

    private final String folderName;

    // Avoid OOM, gc'd references result in whole chunk being sent (possibly higher cpu)
    private final Long2ObjectOpenHashMap<SoftReference<Map<Integer, Object>>> changedBlocks = new Long2ObjectOpenHashMap<>();
    // Storing the vector is redundant
    private final Object changeBlocksPresent = new Object();
    // Storing extra blocks past 512 is redundant
    private final Int2ObjectOpenHashMap<Object> changeBlocksFullMap = new Int2ObjectOpenHashMap<>();

    private final BlockUpdateScheduler updateQueue;
    private final Queue<Block> normalUpdateQueue = new ConcurrentLinkedDeque<>();
    private final Map<Long, Set<Integer>> lightQueue = new ConcurrentHashMap<>(8, 0.9f, 1);

    private final ConcurrentMap<Long, Int2ObjectMap<Player>> chunkSendQueue = new ConcurrentHashMap<>();
    private final LongSet chunkSendTasks = new LongOpenHashSet();

    private final LongOpenHashSet chunkPopulationQueue = new LongOpenHashSet();
    private final LongOpenHashSet chunkPopulationLock = new LongOpenHashSet();
    private final LongOpenHashSet chunkGenerationQueue = new LongOpenHashSet();
    private final int chunkGenerationQueueSize;
    private final int chunkPopulationQueueSize;

    private boolean autoSave;
    @Getter
    @Setter
    private boolean saveOnUnloadEnabled = true;
    public boolean isBeingConverted;

    private BlockMetadataStore blockMetadata;

    private final boolean useSections;

    private boolean useChunkLoaderApi;
    private final Vector3 temporalVector = new Vector3(0, 0, 0);
    private final int chunkTickRadius;
    private final Long2IntMap chunkTickList = new Long2IntOpenHashMap();
    private final int chunksPerTicks;
    private final boolean clearChunksOnTick;

    private int updateLCG = ThreadLocalRandom.current().nextInt();

    private int tickRate = 1;
    public int tickRateTime;
    public int tickRateCounter;
    private long levelCurrentTick;

    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;

    @SuppressWarnings("FieldMayBeFinal") private Class<? extends Generator> generatorClass;
    @SuppressWarnings("FieldMayBeFinal") private ThreadLocal<Generator> generators = new ThreadLocal<Generator>() {

        @Override
        public Generator initialValue() {
            try {
                Generator generator = generatorClass.getConstructor(Map.class).newInstance(provider.getGeneratorOptions());
                NukkitRandom rand = new NukkitRandom(getSeed());
                if (Server.getInstance().isPrimaryThread()) {
                    generator.init(Level.this, rand);
                }
                generator.init(new PopChunkManager(getSeed(), Level.this::getDimensionData), rand);
                return generator;
            } catch (Throwable e) {
                Server.getInstance().getLogger().logException(e);
                return null;
            }
        }
    };

    @Getter
    @Setter
    private DimensionData dimensionData;

    public GameRules gameRules;

    private final AsyncChunkThread asyncChunkThread;

    private GeneratorTaskFactory generatorTaskFactory = this;

    public Level(Server server, String name, String path, Class<? extends LevelProvider> provider) {
        this.levelId = levelIdCounter++;
        this.server = server;
        this.folderName = name;
        this.autoSave = server.getAutoSave();

        this.blockMetadata = new BlockMetadataStore(this);

        try {
            this.provider = provider.getConstructor(Level.class, String.class).newInstance(this, path);
        } catch (Exception e) {
            throw new LevelException("Caused by " + Utils.getExceptionMessage(e));
        }

        this.provider.updateLevelName(name);

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.preparing",
                TextFormat.GREEN + name + TextFormat.WHITE));

        if (this.provider instanceof Anvil) {
            this.server.getLogger().warning("Level \"" + name + "\" is in old format. Convert it to enable new features. Type 'convert " + name + "' to get started.");
        }

        this.generatorClass = Generator.getGenerator(this.provider.getGenerator());

        try {
            this.useSections = (boolean) provider.getMethod("usesChunkSection").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.time = (int) this.provider.getTime();

        this.raining = this.provider.isRaining();
        this.rainTime = this.provider.getRainTime();
        if (this.rainTime <= 0) {
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.thundering = this.provider.isThundering();
        this.thunderTime = this.provider.getThunderTime();
        if (this.thunderTime <= 0) {
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.levelCurrentTick = this.provider.getCurrentTick();
        this.updateQueue = new BlockUpdateScheduler(this, levelCurrentTick);

        this.chunkTickRadius = Math.min(this.server.getViewDistance(), Math.max(1, this.server.getConfig("chunk-ticking.tick-radius", 3)));
        this.chunksPerTicks = this.server.getConfig("chunk-ticking.per-tick", 40);
        this.chunkGenerationQueueSize = this.server.getConfig("chunk-generation.queue-size", 8);
        this.chunkPopulationQueueSize = this.server.getConfig("chunk-generation.population-queue-size", 8);
        this.clearChunksOnTick = this.server.getConfig("chunk-ticking.clear-tick-list", false);

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

        this.asyncChunkThread = new AsyncChunkThread(name);
    }

    public static long chunkHash(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public static long blockHash(int x, int y, int z, DimensionData dimensionData) {
        return (((long) x & (long) 0xFFFFFFF) << 36) | ((long) (capWorldY(y, dimensionData) - dimensionData.getMinHeight()) << 28) | ((long) z & (long) 0xFFFFFFF);
    }

    public static int localBlockHash(double x, double y, double z, DimensionData dimensionData) {
        byte hi = (byte) (((int) x & 15) + (((int) z & 15) << 4));
        short lo = (short) (capWorldY((int) y, dimensionData) - dimensionData.getMinHeight());
        return (hi & 0xFF) << 16 | lo;
    }

    public static Vector3 getBlockXYZ(long chunkHash, int blockHash, DimensionData dimensionData) {
        int hi = (byte) (blockHash >>> 16);
        int lo = (short) blockHash;
        int y = capWorldY(lo + dimensionData.getMinHeight(), dimensionData);
        int x = (hi & 0xF) + (getHashX(chunkHash) << 4);
        int z = ((hi >> 4) & 0xF) + (getHashZ(chunkHash) << 4);
        return new Vector3(x, y, z);
    }

    public static BlockVector3 blockHash(double x, double y, double z) {
        return new BlockVector3((int) x, (int) y, (int) z);
    }

    public static int chunkBlockHash(int x, int y, int z) {
        return (x << 13) | (z << 9) | (y + 64);
    }

    public static int getHashX(long hash) {
        return (int) (hash >> 32);
    }

    public static int getHashZ(long hash) {
        return (int) hash;
    }

    public static Vector3 getBlockXYZ(BlockVector3 hash) {
        return new Vector3(hash.x, hash.y, hash.z);
    }

    public static Chunk.Entry getChunkXZ(long hash) {
        return new Chunk.Entry(getHashX(hash), getHashZ(hash));
    }

    private static int capWorldY(int y, DimensionData dimensionData) {
        return Math.max(Math.min(y, dimensionData.getMaxHeight()), dimensionData.getMinHeight());
    }

    public static int generateChunkLoaderId(ChunkLoader loader) {
        if (loader.getLoaderId() == 0) {
            return chunkLoaderCounter++;
        } else {
            throw new IllegalStateException("ChunkLoader has a loader id already assigned: " + loader.getLoaderId());
        }
    }

    public int getTickRate() {
        return tickRate;
    }

    public int getTickRateTime() {
        return tickRateTime;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public void initLevel() {
        Generator generator = generators.get();
        this.dimensionData = generator.getDimensionData();
        if (this.dimensionData.getDimensionId() == DIMENSION_OVERWORLD &&
                this.provider instanceof Anvil) {
            this.dimensionData = DimensionData.LEGACY_DIMENSION;
        }

        this.gameRules = this.provider.getGamerules();
    }

    public Generator getGenerator() {
        return generators.get();
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    public Server getServer() {
        return server;
    }

    final public LevelProvider getProvider() {
        return this.provider;
    }

    final public int getId() {
        return this.levelId;
    }

    public void close() {
        if (this.asyncChunkThread != null) {
            this.asyncChunkThread.shutdown();
        }

        this.saveLevelData();
        this.provider.close(); // Also saves chunks on unload
        this.provider = null;
        this.blockMetadata = null;
        this.server.getLevels().remove(this.levelId);
    }

    public void addSound(Vector3 pos, Sound sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch, Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(new Player[0]));
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch, Player... players) {
        if (volume < 0 || volume > 1) throw new IllegalArgumentException("Sound volume must be between 0 and 1");
        if (pitch < 0) throw new IllegalArgumentException("Sound pitch must be higher than 0");

        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = pos.getFloorX();
        packet.y = pos.getFloorY();
        packet.z = pos.getFloorZ();

        if (players == null || players.length == 0) {
            this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
        } else {
            Server.broadcastPacket(players, packet);
        }
    }

    public void addLevelEvent(Vector3 pos, int event) {
        this.addLevelEvent(pos, event, 0);
    }

    public void addLevelEvent(Vector3 pos, int event, int data) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = event;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.data = data;
        this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, int entityType) {
        this.addLevelSoundEvent(pos, type, data, entityType, false, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, int entityType, boolean isBaby, boolean isGlobal) {
        String identifier = AddEntityPacket.LEGACY_IDS.get(entityType);
        if (identifier == null) {
            EntityDefinition definition = EntityManager.get().getDefinition(entityType);
            if (definition == null) {
                identifier = ":";
            } else {
                identifier = definition.getIdentifier();
            }
        }
        this.addLevelSoundEvent(pos, type, data, identifier, isBaby, isGlobal);
    }

    public void addLevelSoundEvent(Vector3 pos, int type) {
        this.addLevelSoundEvent(pos, type, -1);
    }

    /**
     * Broadcasts sound to players
     *
     * @param pos  position where sound should be played
     * @param type ID of the sound from {@link cn.nukkit.network.protocol.LevelSoundEventPacket}
     * @param data generic data that can affect sound
     */
    public void addLevelSoundEvent(Vector3 pos, int type, int data) {
        this.addLevelSoundEvent(pos, type, data, "", false, false);
    }

    public void addLevelSoundEvent(Vector3 pos, int type, int data, String identifier, boolean isBaby, boolean isGlobal) {
        LevelSoundEventPacket pk = new LevelSoundEventPacket();
        pk.sound = type;
        pk.extraData = data;
        pk.entityIdentifier = identifier;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.isGlobal = isGlobal;
        pk.isBabyMob = isBaby;

        this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), pk);
    }

    public void addParticle(Particle particle) {
        this.addParticle(particle, (Player[]) null);
    }

    public void addParticle(Particle particle, Player player) {
        this.addParticle(particle, new Player[]{player});
    }

    public void addParticle(Particle particle, Player[] players) {
        this.addParticle(particle, players, 1);
    }

    public void addParticle(Particle particle, Player[] players, int count) {
        if (players == null) {
            players = this.getChunkPlayers(particle.getChunkX(), particle.getChunkZ()).values().toArray(new Player[0]);
        }

        DataPacket[] packets = particle.encode();
        if (packets != null) {
            for (int i = 0; i < count; i++) {
                for (DataPacket pk : packets) {
                    Server.broadcastPacket(players, pk);
                }
            }
        }
    }

    public void addParticle(Particle particle, Collection<Player> players) {
        this.addParticle(particle, players.toArray(new Player[0]));
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect) {
        this.addParticleEffect(pos, particleEffect, -1, this.getDimension(), (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, this.getDimension(), (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId, Collection<Player> players) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, players.toArray(new Player[0]));
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId, Player... players) {
        this.addParticleEffect(pos.asVector3f(), particleEffect.getIdentifier(), uniqueEntityId, dimensionId, players);
    }

    public void addParticleEffect(Vector3f pos, String identifier, long uniqueEntityId, int dimensionId, Player... players) {
        SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
        pk.identifier = identifier;
        pk.uniqueEntityId = uniqueEntityId;
        pk.dimensionId = dimensionId;
        pk.position = pos;

        if (players == null || players.length == 0) {
            addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
        } else {
            Server.broadcastPacket(players, pk);
        }
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean unload() {
        return this.unload(false);
    }

    public boolean unload(boolean force) {
        LevelUnloadEvent ev = new LevelUnloadEvent(this);

        if (this == this.server.getDefaultLevel() && !force) {
            ev.setCancelled(true);
        }

        this.server.getPluginManager().callEvent(ev);

        if (!force && ev.isCancelled()) {
            return false;
        }

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.unloading",
                TextFormat.GREEN + this.getName() + TextFormat.WHITE));
        Level defaultLevel = this.server.getDefaultLevel();

        for (Player player : new ArrayList<>(this.getPlayers().values())) {
            if (this == defaultLevel || defaultLevel == null) {
                player.close(player.getLeaveMessage(), "Forced default level unload");
            } else {
                player.teleport(this.server.getDefaultLevel().getSafeSpawn());
            }
        }

        if (this == defaultLevel) {
            this.server.setDefaultLevel(null);
        }

        this.close();

        return true;
    }

    public Map<Integer, Player> getChunkPlayers(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        Map<Integer, Player> map = this.playerLoaders.get(index);
        if (map != null) {
            return new HashMap<>(map);
        } else {
            return new HashMap<>();
        }
    }

    public ChunkLoader[] getChunkLoaders(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        Map<Integer, ChunkLoader> map = this.chunkLoaders.get(index);
        if (map != null) {
            return map.values().toArray(new ChunkLoader[0]);
        } else {
            return new ChunkLoader[0];
        }
    }

    public void addChunkPacket(int chunkX, int chunkZ, DataPacket packet) {
        long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (chunkPackets) {
            Deque<DataPacket> packets = chunkPackets.computeIfAbsent(index, i -> new ArrayDeque<>());
            packets.add(packet);
        }
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        this.registerChunkLoader(loader, chunkX, chunkZ, true);
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ, boolean autoLoad) {
        if (!(loader instanceof Player) && !this.useChunkLoaderApi) {
            this.server.getLogger().debug("registerChunkLoader: full ChunkLoader API enabled");
            this.useChunkLoaderApi = true;
        }

        int hash = loader.getLoaderId();
        long index = Level.chunkHash(chunkX, chunkZ);

        Map<Integer, ChunkLoader> map = this.chunkLoaders.get(index);
        if (map == null) {
            Map<Integer, ChunkLoader> newChunkLoader = new HashMap<>();
            newChunkLoader.put(hash, loader);
            this.chunkLoaders.put(index, newChunkLoader);
            Map<Integer, Player> newPlayerLoader = new HashMap<>();
            if (loader instanceof Player) {
                newPlayerLoader.put(hash, (Player) loader);
            }
            this.playerLoaders.put(index, newPlayerLoader);
        } else if (map.containsKey(hash)) {
            return;
        } else {
            map.put(hash, loader);
            if (loader instanceof Player) {
                this.playerLoaders.get(index).put(hash, (Player) loader);
            }
        }

        if (!this.loaders.containsKey(hash)) {
            this.loaderCounter.put(hash, 1);
            this.loaders.put(hash, loader);
        } else {
            this.loaderCounter.put(hash, this.loaderCounter.get(hash) + 1);
        }

        this.cancelUnloadChunkRequest(hash);

        if (autoLoad) {
            this.loadChunk(chunkX, chunkZ);
        }
    }

    public void unregisterChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        int hash = loader.getLoaderId();
        long index = Level.chunkHash(chunkX, chunkZ);
        Map<Integer, ChunkLoader> chunkLoadersIndex = this.chunkLoaders.get(index);
        if (chunkLoadersIndex != null) {
            ChunkLoader oldLoader = chunkLoadersIndex.remove(hash);
            if (oldLoader != null) {
                if (chunkLoadersIndex.isEmpty()) {
                    this.chunkLoaders.remove(index);
                    this.playerLoaders.remove(index);
                    this.unloadChunkRequest(chunkX, chunkZ, true);
                } else {
                    Map<Integer, Player> playerLoadersIndex = this.playerLoaders.get(index);
                    playerLoadersIndex.remove(hash);
                }

                int count = this.loaderCounter.get(hash);
                if (--count <= 0) {
                    this.loaderCounter.remove(hash);
                    this.loaders.remove(hash);
                } else {
                    this.loaderCounter.put(hash, count);
                }
            }
        }
    }

    public void checkTime() {
        if (!this.stopTime && this.gameRules.getBoolean(GameRule.DO_DAYLIGHT_CYCLE)) {
            this.time = this.time + tickRate;
            if (this.time < 0) this.time = 0;
        }
    }

    public void sendTime(Player... players) {
        SetTimePacket pk = new SetTimePacket();
        pk.time = this.time;

        Server.broadcastPacket(players, pk);
    }

    public void sendTime() {
        sendTime(this.players.values().toArray(new Player[0]));
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public void doTick(int currentTick) {
        AsyncChunkData data;
        while ((data = this.asyncChunkThread.out.poll()) != null) {
            this.chunkRequestCallback(data.timestamp, data.x, data.z, data.count, data.data, data.hash);
        }

        this.updateBlockLight(this.lightQueue);
        this.checkTime();

        if (currentTick % 6000 == 0) { // Keep the time in sync
            this.sendTime();
        }

        // Tick Weather
        if (this.getDimension() != DIMENSION_NETHER && this.getDimension() != DIMENSION_THE_END && this.gameRules.getBoolean(GameRule.DO_WEATHER_CYCLE)) {
            this.rainTime--;
            if (this.rainTime <= 0) {
                if (!this.setRaining(!this.raining)) {
                    if (this.raining) {
                        setRainTime(ThreadLocalRandom.current().nextInt(12000) + 12000);
                    } else {
                        setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                    }
                }
            }

            this.thunderTime--;
            if (this.thunderTime <= 0) {
                if (!this.setThundering(!this.thundering)) {
                    if (this.thundering) {
                        setThunderTime(ThreadLocalRandom.current().nextInt(12000) + 3600);
                    } else {
                        setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                    }
                }
            }

            if (this.isThundering()) {
                Map<Long, ? extends FullChunk> chunks = getChunks();
                if (chunks instanceof Long2ObjectOpenHashMap) {
                    Long2ObjectOpenHashMap<? extends FullChunk> fastChunks = (Long2ObjectOpenHashMap) chunks;
                    ObjectIterator<? extends Long2ObjectMap.Entry<? extends FullChunk>> iter = fastChunks.long2ObjectEntrySet().fastIterator();
                    while (iter.hasNext()) {
                        Long2ObjectMap.Entry<? extends FullChunk> entry = iter.next();
                        performThunder(entry.getLongKey(), entry.getValue());
                    }
                } else {
                    for (Map.Entry<Long, ? extends FullChunk> entry : chunks.entrySet()) {
                        performThunder(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

        this.levelCurrentTick++;

        this.unloadChunks();

        this.updateQueue.tick(this.levelCurrentTick);

        Block block;
        while ((block = this.normalUpdateQueue.poll()) != null) {
            block.onUpdate(BLOCK_UPDATE_NORMAL);
        }


        if (!this.updateEntities.isEmpty()) {
            for (long id : new ArrayList<>(this.updateEntities.keySet())) {
                Entity entity = this.updateEntities.get(id);
                if (entity == null) {
                    this.updateEntities.remove(id);
                    continue;
                }
                if (entity.closed || !entity.onUpdate(currentTick)) {
                    this.updateEntities.remove(id);
                }
            }
        }

        this.updateBlockEntities.removeIf(blockEntity -> !blockEntity.isValid() || !blockEntity.onUpdate());

        this.tickChunks();

        synchronized (changedBlocks) {
            if (!this.changedBlocks.isEmpty()) {
                if (!this.players.isEmpty()) {
                    ObjectIterator<Long2ObjectMap.Entry<SoftReference<Map<Integer, Object>>>> iter = changedBlocks.long2ObjectEntrySet().fastIterator();
                    while (iter.hasNext()) {
                        Long2ObjectMap.Entry<SoftReference<Map<Integer, Object>>> entry = iter.next();
                        long index = entry.getLongKey();
                        Map<Integer, Object> blocks = entry.getValue().get();
                        int chunkX = Level.getHashX(index);
                        int chunkZ = Level.getHashZ(index);
                        if (blocks == null || blocks.size() > MAX_BLOCK_CACHE) {
                            FullChunk chunk = this.getChunkIfLoaded(chunkX, chunkZ);
                            if (chunk == null) {
                                continue;
                            }
                            for (Player p : this.getChunkPlayers(chunkX, chunkZ).values()) {
                                p.onChunkChanged(chunk);
                            }
                        } else {
                            Player[] playerArray = this.getChunkPlayers(chunkX, chunkZ).values().toArray(new Player[0]);
                            Vector3[] blocksArray = new Vector3[blocks.size()];
                            int i = 0;
                            for (int blockHash : blocks.keySet()) {
                                Vector3 hash = getBlockXYZ(index, blockHash, this.getDimensionData());
                                blocksArray[i++] = hash;
                            }
                            this.sendBlocks(playerArray, blocksArray, UpdateBlockPacket.FLAG_ALL);
                        }
                    }
                }
                this.changedBlocks.clear();
            }
        }

        this.processChunkRequest();

        if (this.sleepTicks > 0 && --this.sleepTicks <= 0) {
            this.checkSleep();
        }

        synchronized (chunkPackets) {
            for (long index : this.chunkPackets.keySet()) {
                int chunkX = Level.getHashX(index);
                int chunkZ = Level.getHashZ(index);
                Map<Integer, Player> map = this.getChunkPlayers(chunkX, chunkZ);
                if (!map.isEmpty()) {
                    Player[] chunkPlayers = map.values().toArray(new Player[0]);
                    for (DataPacket pk : this.chunkPackets.get(index)) {
                        Server.broadcastPacket(chunkPlayers, pk);
                    }
                }
            }
            this.chunkPackets.clear();
        }

        if (gameRules.isStale()) {
            GameRulesChangedPacket packet = new GameRulesChangedPacket();
            packet.gameRulesMap = gameRules.getGameRules();
            Server.broadcastPacket(this.players.values(), packet);
            gameRules.refresh();
        }
    }

    private void performThunder(long index, FullChunk chunk) {
        if (ThreadLocalRandom.current().nextInt(100000) < 1) {
            if (areNeighboringChunksLoaded(index)) {
                return;
            }

            int LCG = this.getUpdateLCG() >> 2;

            int chunkX = chunk.getX() << 4;
            int chunkZ = chunk.getZ() << 4;
            Vector3 vector = this.adjustPosToNearbyEntity(new Vector3(chunkX + (LCG & 0xf), 0, chunkZ + (LCG >> 8 & 0xf)));

            Biome biome = Biome.getBiome(this.getBiomeId(vector.getFloorX(), vector.getFloorZ()));
            if (!biome.canRain()) {
                return;
            }

            int bId = this.getBlockIdAt(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
            if (bId != Block.TALL_GRASS && bId != Block.WATER)
                vector.y += 1;
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", vector.x))
                            .add(new DoubleTag("", vector.y)).add(new DoubleTag("", vector.z)))
                    .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)));

            EntityLightning bolt = (EntityLightning) Entity.createEntity(EntityLightning.NETWORK_ID, chunk, nbt);
            LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
            server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                bolt.spawnToAll();
            } else {
                bolt.setEffect(false);
            }
        }
    }

    public Vector3 adjustPosToNearbyEntity(Vector3 pos) {
        pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
        AxisAlignedBB axisalignedbb = new SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), this.getMaxBlockY(), pos.getZ()).expand(3, 3, 3);
        List<Entity> list = new ArrayList<>();

        for (Entity entity : this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && entity.canSeeSky()) {
                list.add(entity);
            }
        }

        if (!list.isEmpty()) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size())).getPosition();
        } else {
            if (pos.getY() == -1) {
                pos = pos.up(2);
            }

            return pos;
        }
    }

    public void checkSleep() {
        if (this.players.isEmpty()) {
            return;
        }

        int playerCount = 0;
        int sleepingPlayerCount = 0;
        for (Player p : this.players.values()) {
            if (p.isSpectator()) {
                continue;
            }
            playerCount++;
            if (p.isSleeping()) {
                sleepingPlayerCount++;
            }
        }

        if (playerCount > 0 && sleepingPlayerCount / playerCount * 100 >= gameRules.getInteger(GameRule.PLAYERS_SLEEPING_PERCENTAGE)) {
            int time = this.getTime() % Level.TIME_FULL;

            if ((time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE) || this.isThundering()) {
                this.setTime(this.getTime() + Level.TIME_FULL - time);

                if (this.isThundering()) {
                    this.setThundering(false);
                    this.setRaining(false);
                }

                for (Player p : this.getPlayers().values()) {
                    p.stopSleep();
                }
            }
        }
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data) {
        this.sendBlockExtraData(x, y, z, id, data, this.getChunkPlayers(x >> 4, z >> 4).values());
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Collection<Player> players) {
        this.sendBlockExtraData(x, y, z, id, data, players.toArray(new Player[0]));
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Player[] players) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_SET_DATA;
        pk.x = x + 0.5f;
        pk.y = y + 0.5f;
        pk.z = z + 0.5f;
        pk.data = (data << 8) | id;

        Server.broadcastPacket(players, pk);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags) {
        this.sendBlocks(target, blocks, flags, false, Block.LAYER_NORMAL);
        this.sendBlocks(target, blocks, flags, false, Block.LAYER_WATERLOGGED);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, boolean optimizeRebuilds) {
        this.sendBlocks(target, blocks, flags, optimizeRebuilds, Block.LAYER_NORMAL);
        this.sendBlocks(target, blocks, flags, optimizeRebuilds, Block.LAYER_WATERLOGGED);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, BlockLayer blockLayer) {
        this.sendBlocks(target, blocks, flags, false, blockLayer);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, boolean optimizeRebuilds, BlockLayer layer) {
        LongSet chunks = null;
        if (optimizeRebuilds) {
            chunks = new LongOpenHashSet();
        }

        for (Vector3 b : blocks) {
            if (b == null) {
                continue;
            }
            boolean first = !optimizeRebuilds;

            if (optimizeRebuilds) {
                long index = Level.chunkHash((int) b.x >> 4, (int) b.z >> 4);
                if (!chunks.contains(index)) {
                    chunks.add(index);
                    first = true;
                }
            }
            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.x = (int) b.x;
            updateBlockPacket.y = (int) b.y;
            updateBlockPacket.z = (int) b.z;
            updateBlockPacket.flags = first ? flags : UpdateBlockPacket.FLAG_NONE;
            updateBlockPacket.dataLayer = layer.ordinal();

            Block block = (b instanceof Block && ((Block) b).getLayer() == layer) ? (Block) b : this.getBlockAsyncIfLoaded(null, (int) b.x, (int) b.y, (int) b.z, layer);

            UpdateBlockPacket packet = (UpdateBlockPacket) updateBlockPacket.clone();

            try {
                int fullBlock = block.getFullId();
                packet.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(fullBlock);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("Unable to create BlockUpdatePacket at (" + b.x + ", " + b.y + ", " + b.z + ") in " + getName());
            }

            Server.broadcastPacket(target, packet);
        }
    }

    public void sendBlocks(Player target, Vector3[] blocks, int flags) {
        for (Vector3 b : blocks) {
            if (b == null) {
                continue;
            }

            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.x = (int) b.x;
            updateBlockPacket.y = (int) b.y;
            updateBlockPacket.z = (int) b.z;
            updateBlockPacket.flags = flags;

            Block block = b instanceof Block ? (Block) b : this.getBlockAsyncIfLoaded(target.chunk, (int) b.x, (int) b.y, (int) b.z, BlockLayer.NORMAL);

            try {
                updateBlockPacket.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(block.getFullId());
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("Unable to create BlockUpdatePacket at (" + b.x + ", " + b.y + ", " + b.z + ") in " + getName() + " for player " + target.getName());
            }

            target.dataPacket(updateBlockPacket);
        }
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || this.loaders.isEmpty()) {
            this.chunkTickList.clear();
            return;
        }

        int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5))));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = Math.min(randRange, this.chunkTickRadius);

        for (ChunkLoader loader : this.loaders.values()) {
            int chunkX = NukkitMath.floorDouble(loader.getX()) >> 4;
            int chunkZ = NukkitMath.floorDouble(loader.getZ()) >> 4;

            long index = Level.chunkHash(chunkX, chunkZ);
            int existingLoaders = Math.max(0, this.chunkTickList.getOrDefault(index, 0));
            this.chunkTickList.put(index, existingLoaders + 1);
            for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                int dx = ThreadLocalRandom.current().nextInt(randRange << 1) - randRange;
                int dz = ThreadLocalRandom.current().nextInt(randRange << 1) - randRange;
                long hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
                if (!this.chunkTickList.containsKey(hash) && provider.isChunkLoaded(hash)) {
                    this.chunkTickList.put(hash, -1);
                }
            }
        }

        int blockTest = 0;

        if (!chunkTickList.isEmpty()) {
            int tickSpeed = gameRules.getInteger(GameRule.RANDOM_TICK_SPEED);

            ObjectIterator<Long2IntMap.Entry> iter = chunkTickList.long2IntEntrySet().iterator();
            while (iter.hasNext()) {
                Long2IntMap.Entry entry = iter.next();
                long index = entry.getLongKey();
                if (!areNeighboringChunksLoaded(index)) {
                    iter.remove();
                    continue;
                }

                int loaders = entry.getIntValue();

                int chunkX = getHashX(index);
                int chunkZ = getHashZ(index);

                FullChunk chunk;
                if ((chunk = this.getChunkIfLoaded(chunkX, chunkZ)) == null) {
                    iter.remove();
                    continue;
                } else if (loaders <= 0) {
                    iter.remove();
                }

                for (Entity entity : chunk.getEntities().values()) {
                    if (entity.updateMode < 1 || (entity.updateMode % 3 == 2 && server.getTick() - entity.lastUpdate > 300)) { // Force an update every 15 seconds to check age for despawning
                        if (entity.updateMode % 2 == 1) {
                            entity.updateMode = 1;
                        }
                        entity.scheduleUpdate();
                    }
                }

                if (this.useSections) {
                    for (ChunkSection section : ((Chunk) chunk).getSections()) {
                        if (!(section instanceof EmptyChunkSection)) {
                            int Y = section.getY();
                            for (int i = 0; i < tickSpeed; ++i) {
                                int n = ThreadLocalRandom.current().nextInt();
                                int x = n & 0xF;
                                int z = n >> 8 & 0xF;
                                int y = n >> 16 & 0xF;

                                int fullId = section.getFullBlock(x, y, z);
                                int blockId = fullId >> Block.DATA_BITS;
                                if (blockId < randomTickBlocks.length && randomTickBlocks[blockId]) {
                                    Block block = Block.get(fullId, this, (chunkX << 4) + x, (Y << 4) + y, (chunkZ << 4) + z);
                                    block.onUpdate(BLOCK_UPDATE_RANDOM);
                                }
                            }
                        }
                    }
                } else {
                    for (int Y = 0; Y < 8 && (Y < 3 || blockTest != 0); ++Y) {
                        blockTest = 0;
                        for (int i = 0; i < tickSpeed; ++i) {
                            int n = ThreadLocalRandom.current().nextInt();
                            int x = n & 0xF;
                            int z = n >> 8 & 0xF;
                            int y = n >> 16 & 0xF;

                            int fullId = chunk.getFullBlock(x, y + (Y << 4), z);
                            int blockId = fullId >> Block.DATA_BITS;
                            blockTest |= fullId;
                            if (blockId < randomTickBlocks.length && randomTickBlocks[blockId]) {
                                Block block = Block.get(fullId, this, x, y + (Y << 4), z);
                                block.onUpdate(BLOCK_UPDATE_RANDOM);
                            }
                        }
                    }
                }
            }
        }

        if (this.clearChunksOnTick) {
            this.chunkTickList.clear();
        }
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(boolean force) {
        if ((!this.autoSave || server.holdWorldSave) && !force) {
            return false;
        }

        this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

        this.saveLevelData();
        this.saveChunks();
        return true;
    }

    private void saveLevelData() {
        try {
            this.provider.setTime(this.time);
            this.provider.setRaining(this.raining);
            this.provider.setRainTime(this.rainTime);
            this.provider.setThundering(this.thundering);
            this.provider.setThunderTime(this.thunderTime);
            this.provider.setCurrentTick(this.levelCurrentTick);
            this.provider.setGameRules(this.gameRules);
            this.provider.saveLevelData();
        } catch (Exception ex) {
            Server.getInstance().getLogger().error("Failed to save level data for " + this.getFolderName(), ex);
        }
    }

    public void saveChunks() {
        provider.saveChunks();
    }

    public void updateAroundRedstone(Vector3 pos, BlockFace ignoredFace) {
        for (BlockFace side : BlockFace.values()) {
            if (ignoredFace != null && side == ignoredFace) {
                continue;
            }

            Vector3 sideVec = pos.getSideVec(side);
            this.getBlock(sideVec).onUpdate(BLOCK_UPDATE_REDSTONE);
        }
    }

    public void updateComparatorOutputLevel(Vector3 v) {
        for (BlockFace face : Plane.HORIZONTAL) {
            Vector3 pos = v.getSideVec(face);

            if (this.isChunkLoaded((int) pos.x >> 4, (int) pos.z >> 4)) {
                Block block1 = this.getBlock(pos);

                if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                } else if (block1.isNormalBlock()) {
                    pos = pos.getSideVec(face);
                    block1 = this.getBlock(pos);

                    if (BlockRedstoneDiode.isDiode(block1)) {
                        block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                    }
                }
            }
        }
    }

    public void updateAround(Vector3 pos) {
        this.updateAround((int) pos.x, (int) pos.y, (int) pos.z, Block.LAYER_NORMAL);
        this.updateAround((int) pos.x, (int) pos.y, (int) pos.z, Block.LAYER_WATERLOGGED);
    }

    public void updateAround(int x, int y, int z) {
        this.updateAround(x, y, z, Block.LAYER_NORMAL);
        this.updateAround(x, y, z, Block.LAYER_WATERLOGGED);
    }

    public void updateAround(int x, int y, int z, BlockLayer layer) {
        Vector3 updatePos = new Vector3(x, y, z);
        BlockUpdateEvent ev;
        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(null, x, y - 1, z, layer, true).setUpdatePos(updatePos)));
        if (!ev.isCancelled()) {
            normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(null, x, y + 1, z, layer, true).setUpdatePos(updatePos)));
        if (!ev.isCancelled()) {
            normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(null, x - 1, y, z, layer, true).setUpdatePos(updatePos)));
        if (!ev.isCancelled()) {
            normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(null, x + 1, y, z, layer, true).setUpdatePos(updatePos)));
        if (!ev.isCancelled()) {
            normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(null, x, y, z - 1, layer, true).setUpdatePos(updatePos)));
        if (!ev.isCancelled()) {
            normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(null, x, y, z + 1, layer, true).setUpdatePos(updatePos)));
        if (!ev.isCancelled()) {
            normalUpdateQueue.add(ev.getBlock());
        }
    }

    public void scheduleUpdate(Block pos, int delay) {
        this.scheduleUpdate(pos, pos, delay, 0, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay) {
        this.scheduleUpdate(block, pos, delay, 0, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority) {
        this.scheduleUpdate(block, pos, delay, priority, true);
    }

    public void scheduleUpdate(Block block, Vector3 pos, int delay, int priority, boolean checkArea) {
        if (block.getId() == 0 || (checkArea && !this.isChunkLoaded(block.getChunkX(), block.getChunkZ()))) {
            return;
        }

        BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, ((long) delay) + levelCurrentTick, priority);

        if (!this.updateQueue.contains(entry)) {
            this.updateQueue.add(entry);
        }
    }

    public boolean cancelSheduledUpdate(Vector3 pos, Block block) {
        return this.updateQueue.remove(new BlockUpdateEntry(pos, block));
    }

    public boolean isUpdateScheduled(Vector3 pos, Block block) {
        return this.updateQueue.contains(new BlockUpdateEntry(pos, block));
    }

    public boolean isBlockTickPending(Vector3 pos, Block block) {
        return this.updateQueue.isBlockTickPending(pos, block);
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(FullChunk chunk) {
        int minX = (chunk.getX() << 4) - 2;
        int maxX = minX + 18;
        int minZ = (chunk.getZ() << 4) - 2;
        int maxZ = minZ + 18;

        return this.getPendingBlockUpdates(new SimpleAxisAlignedBB(minX, this.getMinBlockY(), minZ, maxX, this.getMaxBlockY(), maxZ));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        return updateQueue.getPendingBlockUpdates(boundingBox);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
        return getCollisionBlocks(null, bb, targetFirst);
    }

    public Block[] getCollisionBlocks(Entity entity, AxisAlignedBB bb, boolean targetFirst) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        List<Block> collides = new ArrayList<>();

        if (targetFirst) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(entity == null ? null : entity.chunk, x, y, z, false);
                        if (block != null && block.getId() != 0 && block.collidesWithBB(bb)) {
                            return new Block[]{block};
                        }
                    }
                }
            }
        } else {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(entity == null ? null : entity.chunk, x, y, z, false);
                        if (block != null && block.getId() != 0 && block.collidesWithBB(bb)) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.toArray(new Block[0]);
    }

    public boolean hasCollisionBlocks(Entity entity, AxisAlignedBB bb) {
        return hasCollisionBlocks(entity, bb, false);
    }

    private boolean hasCollisionBlocks(Entity entity, AxisAlignedBB bb, boolean checkCanPassThrough) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(entity.chunk, x, y, z, false);
                    if ((!checkCanPassThrough || !block.canPassThrough()) && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isFullBlock(Vector3 pos) {
        AxisAlignedBB bb;
        if (pos instanceof Block) {
            if (((Block) pos).isSolid()) {
                return true;
            }
            bb = ((Block) pos).getBoundingBox();
        } else {
            bb = this.getBlock(pos).getBoundingBox();
        }

        return bb != null && bb.getAverageEdgeLength() >= 1;
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb) {
        return this.getCollisionCubes(entity, bb, true);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities) {
        return getCollisionCubes(entity, bb, entities, false);
    }

    public AxisAlignedBB[] getCollisionCubes(Entity entity, AxisAlignedBB bb, boolean entities, boolean solidEntities) {
        if (entity.noClip) return new AxisAlignedBB[0];

        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        List<AxisAlignedBB> collides = new ArrayList<>();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(entity.chunk, x, y, z, false);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities || !ent.canPassThrough()) {
                    collides.add(ent.boundingBox.clone());
                }
            }
        }

        return collides.toArray(new AxisAlignedBB[0]);
    }

    public boolean hasCollision(Entity entity, AxisAlignedBB bb, boolean entities) {
        if (this.hasCollisionBlocks(entity, bb, true)) {
            return true;
        }

        if (entities) {
            return this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).length > 0;
        }
        return false;
    }

    public int getFullLight(Vector3 pos) {
        if (pos.y < this.getMinBlockY() || pos.y > this.getMaxBlockY()) {
            return 0;
        }

        FullChunk chunk = this.getChunk(pos.getChunkX(), pos.getChunkZ(), false);
        int level = 0;
        if (chunk != null) {
            level = chunk.getBlockSkyLight((int) pos.x & 0x0f, (int) pos.y, (int) pos.z & 0x0f);
            level -= this.skyLightSubtracted;

            if (level < 15) {
                level = Math.max(chunk.getBlockLight((int) pos.x & 0x0f, (int) pos.y, (int) pos.z & 0x0f), level);
            }
        }

        return level;
    }

    public int calculateSkylightSubtracted(float tickDiff) {
        float light = 1 - (MathHelper.cos(this.calculateCelestialAngle(getTime(), tickDiff) * (6.2831855f)) * 2 + 0.5f);
        light = light < 0 ? 0 : light > 1 ? 1 : light;
        light = 1 - light;
        light = (float) ((double) light * ((raining ? 1 : 0) - 0.3125));
        light = (float) ((double) light * ((isThundering() ? 1 : 0) - 0.3125));
        light = 1 - light;
        return (int) (light * 11f);
    }

    public float calculateCelestialAngle(int time, float tickDiff) {
        float angle = ((float) time + tickDiff) / 24000f - 0.25f;

        if (angle < 0) {
            ++angle;
        }

        if (angle > 1) {
            --angle;
        }

        float i = 1 - (float) ((Math.cos((double) angle * Math.PI) + 1) / 2d);
        angle = angle + (i - angle) / 3;
        return angle;
    }

    public int getMoonPhase(long worldTime) {
        return (int) (worldTime / 24000 % 8 + 8) % 8;
    }

    public int getFullBlock(int x, int y, int z) {
        return this.getFullBlock(null, x, y, z);
    }

    public int getFullBlock(FullChunk chunk, int x, int y, int z) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) return 0;
        int cx = x >> 4;
        int cz = z >> 4;
        if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
            chunk = this.getChunk(cx, cz, false);
        }
        if (chunk == null) {
            return 0;
        }
        return chunk.getFullBlock(x & 0x0f, y, z & 0x0f, Block.LAYER_NORMAL);
    }

    public synchronized Block getBlock(Vector3 pos) { // Original API
        return this.getBlock(null, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), Block.LAYER_NORMAL, true);
    }

    public synchronized Block getBlock(Vector3 pos, boolean load) { // Original API
        return this.getBlock(null, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), Block.LAYER_NORMAL, load);
    }

    public synchronized Block getBlock(Vector3 pos, BlockLayer layer, boolean load) {
        return this.getBlock(null, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, load);
    }

    public synchronized Block getBlock(int x, int y, int z) { // Original API
        return getBlock(null, x, y, z, Block.LAYER_NORMAL, true);
    }

    public synchronized Block getBlock(int x, int y, int z, boolean load) { // Original API
        return this.getBlock(null, x, y, z, Block.LAYER_NORMAL, load);
    }

    public synchronized Block getBlock(FullChunk chunk, int x, int y, int z, boolean load) {
        return this.getBlock(chunk, x, y, z, Block.LAYER_NORMAL, load);
    }

    // The chunk doesn't have to be correct, give the most likely one
    public synchronized Block getBlock(FullChunk chunk, int x, int y, int z, BlockLayer layer, boolean load) {
        int fullState;
        if (y >= this.getMinBlockY() && y <= this.getMaxBlockY()) {
            int cx = x >> 4;
            int cz = z >> 4;
            if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
                if (load) {
                    chunk = getChunk(cx, cz);
                } else {
                    chunk = getChunkIfLoaded(cx, cz);
                }
            }
            if (chunk == null) {
                fullState = 0;
            } else {
                fullState = chunk.getFullBlock(x & 0xF, y, z & 0xF, layer);
            }
        } else {
            fullState = 0;
        }

        return Block.get(fullState, this, x, y, z, layer);
    }

    // Use only if getting correct block is not critical
    private Block getBlockAsyncIfLoaded(FullChunk chunk, int x, int y, int z, BlockLayer layer) {
        int fullState;
        if (y >= this.getMinBlockY() && y <= this.getMaxBlockY()) {
            int cx = x >> 4;
            int cz = z >> 4;
            if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
                chunk = getChunkIfLoaded(cx, cz);
            }
            if (chunk == null) {
                fullState = 0;
            } else {
                fullState = chunk.getFullBlock(x & 0xF, y, z & 0xF, layer);
            }
        } else {
            fullState = 0;
        }

        return Block.get(fullState, this, x, y, z, layer);
    }

    public void updateAllLight(Vector3 pos) {
        this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z);
        this.addLightUpdate((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateBlockSkyLight(int x, int y, int z) {
    }

    private void updateBlockLight(Map<Long, Set<Integer>> map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        Queue<Long> lightPropagationQueue = new ConcurrentLinkedQueue<>();
        Queue<Object[]> lightRemovalQueue = new ConcurrentLinkedQueue<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        LongOpenHashSet removalVisited = new LongOpenHashSet();

        Iterator<Map.Entry<Long, Set<Integer>>> iter = map.entrySet().iterator();
        while (iter.hasNext() && size-- > 0) {
            Map.Entry<Long, Set<Integer>> entry = iter.next();
            iter.remove();
            long index = entry.getKey();
            BaseFullChunk chunk = getChunk(getHashX(index), getHashZ(index), false);
            Set<Integer> blocks = entry.getValue();

            for (int blockHash : blocks) {
                Vector3 pos = getBlockXYZ(index, blockHash, this.getDimensionData());

                if (chunk != null) {
                    int lcx = ((int) pos.x) & 0xF;
                    int lcz = ((int) pos.z) & 0xF;
                    int oldLevel = chunk.getBlockLight(lcx, ((int) pos.y), lcz);
                    int newLevel = Block.getBlockLight(chunk.getBlockId(lcx, ((int) pos.y), lcz));
                    if (oldLevel != newLevel) {
                        chunk.setBlockLight(((int) pos.x) & 0x0f, ((int) pos.y), ((int) pos.z) & 0x0f, newLevel & 0x0f);

                        long hash = Hash.hashBlock(((int) pos.x), ((int) pos.y), ((int) pos.z));
                        if (newLevel < oldLevel) {
                            removalVisited.add(hash);
                            lightRemovalQueue.add(new Object[]{hash, oldLevel});
                        } else {
                            visited.add(hash);
                            lightPropagationQueue.add(hash);
                        }
                    }
                }
            }
        }

        while (!lightRemovalQueue.isEmpty()) {
            Object[] val = lightRemovalQueue.poll();
            long node = (long) val[0];
            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);

            int lightLevel = (int) val[1];

            this.computeRemoveBlockLight(x - 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight(x + 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight(x, y - 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight(x, y + 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z - 1, lightLevel, lightRemovalQueue, lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z + 1, lightLevel, lightRemovalQueue, lightPropagationQueue, removalVisited, visited);
        }

        while (!lightPropagationQueue.isEmpty()) {
            long node = lightPropagationQueue.poll();

            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);

            int lightLevel = this.getBlockLightAt(x, y, z)
                    - Block.getBlockLightFilter(this.getBlockIdAt(x, y, z));

            if (lightLevel >= 1) {
                this.computeSpreadBlockLight(x - 1, y, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x + 1, y, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y - 1, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y + 1, z, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y, z - 1, lightLevel, lightPropagationQueue, visited);
                this.computeSpreadBlockLight(x, y, z + 1, lightLevel, lightPropagationQueue, visited);
            }
        }
    }

    private void computeRemoveBlockLight(int x, int y, int z, int currentLight, Queue<Object[]> queue,
                                         Queue<Long> spreadQueue, Set<Long> visited, Set<Long> spreadVisited) {
        int current = this.getBlockLightAt(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);
            if (current > 1) {
                long index = Hash.hashBlock(x, y, z);
                if (!visited.contains(index)) {
                    visited.add(index);
                    queue.add(new Object[]{index, current});
                }
            }
        } else if (current >= currentLight) {
            long index = Hash.hashBlock(x, y, z);
            if (!spreadVisited.contains(index)) {
                spreadVisited.add(index);
                spreadQueue.add(index);
            }
        }
    }

    private void computeSpreadBlockLight(int x, int y, int z, int currentLight, Queue<Long> queue, Set<Long> visited) {
        int current = this.getBlockLightAt(x, y, z);
        if (current < currentLight - 1) {
            this.setBlockLightAt(x, y, z, currentLight);

            long index = Hash.hashBlock(x, y, z);
            if (!visited.contains(index)) {
                visited.add(index);
                if (currentLight > 1) {
                    queue.add(index);
                }
            }
        }
    }

    public void addLightUpdate(int x, int y, int z) {
        long index = chunkHash(x >> 4, z >> 4);
        Set<Integer> currentMap = this.lightQueue.computeIfAbsent(index, k -> ConcurrentHashMap.newKeySet(8));
        currentMap.add(Level.localBlockHash(x, y, z, this.getDimensionData()));
    }

    @Override
    public synchronized void setBlockFullIdAt(int x, int y, int z, int fullId) {
        this.setBlockFullIdAt(x, y, z, Block.LAYER_NORMAL, fullId);
    }

    @Override
    public synchronized void setBlockFullIdAt(int x, int y, int z, BlockLayer layer, int fullId) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }

        Block block = Block.fullList[fullId];

        this.setBlock(x, y, z, layer, block, false, false);
    }

    public synchronized boolean setBlock(Vector3 pos, Block block) { // Original API
        return this.setBlock(pos, Block.LAYER_NORMAL, block, false, true);
    }

    public synchronized boolean setBlock(Vector3 pos, Block block, boolean direct) { // Original API
        return this.setBlock(pos, Block.LAYER_NORMAL, block, direct, true);
    }

    public synchronized boolean setBlock(Vector3 pos, Block block, boolean direct, boolean update) { // Original API
        return this.setBlock(pos, Block.LAYER_NORMAL, block, direct, update);
    }

    public synchronized boolean setBlock(Vector3 pos, BlockLayer layer, Block block, boolean direct, boolean update) {
        return this.setBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, block, direct, update);
    }

    public synchronized boolean setBlock(int x, int y, int z, Block block, boolean direct, boolean update) { // Original API
        return this.setBlock(x, y, z, Block.LAYER_NORMAL, block, direct, update);
    }

    public synchronized boolean setBlock(int x, int y, int z, BlockLayer layer, Block block, boolean direct, boolean update) {
        return this.setBlock(x, y, z, layer, block, direct, update, true);
    }

    public synchronized boolean setBlock(int x, int y, int z, BlockLayer layer, Block block, boolean direct, boolean update, boolean send) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return false;
        }

        int cx = x >> 4;
        int cz = z >> 4;
        BaseFullChunk chunk = this.getChunk(cx, cz, true);
        Block blockPrevious;
        blockPrevious = chunk.getAndSetBlock(x & 0xF, y, z & 0xF, layer, block);
        if (blockPrevious.getFullId() == block.getFullId()) {
            return false;
        }

        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;
        block.setLayer(layer);

        if (send) {
            if (direct) {
                this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY, layer);
            } else {
                addBlockChange(Level.chunkHash(cx, cz), x, y, z);
            }
        }

        if (this.useChunkLoaderApi) {
            for (ChunkLoader loader : this.getChunkLoaders(cx, cz)) {
                loader.onBlockChanged(block);
            }
        }

        if (update) {
            if (this.lightUpdatesEnabled && (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel())) {
                addLightUpdate(x, y, z);
            }
            BlockUpdateEvent ev = new BlockUpdateEvent(block);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                for (Entity entity : this.getNearbyEntities(new SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1.1, y + 1.1, z + 1.1))) {
                    if (entity.updateMode % 2 == 1) {
                        entity.updateMode = 1;
                    }
                    entity.scheduleUpdate();
                }
                block = ev.getBlock();
                if (!(block instanceof BlockObserver)) block.onUpdate(BLOCK_UPDATE_NORMAL);
                this.updateAround(x, y, z);
            }
        }
        return true;
    }

    private void addBlockChange(int x, int y, int z) {
        long index = Level.chunkHash(x >> 4, z >> 4);
        addBlockChange(index, x, y, z);
    }

    private void addBlockChange(long index, int x, int y, int z) {
        synchronized (changedBlocks) {
            SoftReference<Map<Integer, Object>> current = changedBlocks.computeIfAbsent(index, k -> new SoftReference<>(new HashMap<>()));
            Map<Integer, Object> currentMap = current.get();
            if (currentMap != changeBlocksFullMap && currentMap != null) {
                if (currentMap.size() > MAX_BLOCK_CACHE) {
                    this.changedBlocks.put(index, new SoftReference<>(changeBlocksFullMap));
                } else {
                    currentMap.put(Level.localBlockHash(x, y, z, this.getDimensionData()), changeBlocksPresent);
                }
            }
        }
    }

    public void dropItem(Vector3 source, Item item) {
        this.dropAndGetItem(source, item);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion) {
        this.dropAndGetItem(source, item, motion);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, int delay) {
        this.dropAndGetItem(source, item, motion, delay);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, boolean dropAround, int delay) {
        this.dropAndGetItem(source, item, motion, dropAround, delay);
    }

    @SuppressWarnings("UnusedReturnValue")
    public EntityItem dropAndGetItem(Vector3 source, Item item) {
        return this.dropAndGetItem(source, item, null);
    }

    public EntityItem dropAndGetItem(Vector3 source, Item item, Vector3 motion) {
        return this.dropAndGetItem(source, item, motion, 10);
    }

    public EntityItem dropAndGetItem(Vector3 source, Item item, Vector3 motion, int delay) {
        return this.dropAndGetItem(source, item, motion, false, delay);
    }

    public EntityItem dropAndGetItem(Vector3 source, Item item, Vector3 motion, boolean dropAround, int delay) {
        if (item.getId() != 0 && item.getCount() > 0) {
            if (motion == null) {
                if (dropAround) {
                    float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                    float f1 = ThreadLocalRandom.current().nextFloat() * 6.2831855f;

                    motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
                } else {
                    motion = new Vector3(ThreadLocalRandom.current().nextDouble() * 0.2 - 0.1, 0.2, ThreadLocalRandom.current().nextDouble() * 0.2 - 0.1);
                }
            }

            CompoundTag itemTag = NBTIO.putItemHelper(item);
            itemTag.setName("Item");

            EntityItem itemEntity = (EntityItem) Entity.createEntity(EntityItem.NETWORK_ID,
                    this.getChunk(source.getChunkX(), source.getChunkZ(), true),
                    new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
                                    .add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))

                            .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.x))
                                    .add(new DoubleTag("", motion.y)).add(new DoubleTag("", motion.z)))

                            .putList(new ListTag<FloatTag>("Rotation")
                                    .add(new FloatTag("", ThreadLocalRandom.current().nextFloat() * 360))
                                    .add(new FloatTag("", 0)))

                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

            itemEntity.spawnToAll();
            return itemEntity;
        }

        return null;
    }

    public Item useBreakOn(Vector3 vector) {
        return this.useBreakOn(vector, null);
    }

    public Item useBreakOn(Vector3 vector, Item item) {
        return this.useBreakOn(vector, item, null);
    }

    public Item useBreakOn(Vector3 vector, Item item, Player player) {
        return this.useBreakOn(vector, item, player, false);
    }

    public Item useBreakOn(Vector3 vector, Item item, Player player, boolean createParticles) {
        return useBreakOn(vector, null, item, player, createParticles);
    }

    public Item useBreakOn(Vector3 vector, BlockFace face, Item item, Player player, boolean createParticles) {
        if (player != null && player.getGamemode() > Player.ADVENTURE) {
            return null;
        }
        Block target = this.getBlock(vector);
        Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }

        Vector3 dropPosition = vector;
        boolean isSilkTouch = item.isTool() && item.hasEnchantment(Enchantment.ID_SILK_TOUCH);

        if (player != null) {
            if (player.getGamemode() == Player.ADVENTURE) {
                Tag tag = item.getNamedTagEntry("CanDestroy");
                boolean canBreak = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<? extends Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() != 0 && entry.getBlockUnsafe() != null && entry.getBlockUnsafe().getId() == target.getId()) {
                                canBreak = true;
                                break;
                            }
                        }
                    }
                }
                if (!canBreak) {
                    return null;
                }
            }

            Item[] eventDrops;
            if (!player.isSurvival()) {
                if (target instanceof BlockShulkerBox && this.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {
                    eventDrops = target.getDrops(item);

                    if (eventDrops.length != 0 && !eventDrops[0].hasCompoundTag()) {
                        eventDrops = new Item[0];
                    }
                } else {
                    eventDrops = new Item[0];
                }
            } else if (isSilkTouch && target.canSilkTouch()) {
                eventDrops = new Item[]{target.toItem()};
            } else {
                eventDrops = target.getDrops(item);
            }

            double breakTime = target.getBreakTime(item, player);

            if (player.isCreative() && breakTime > 0.15) {
                breakTime = 0.15;
            }

            if (player.hasEffect(Effect.SWIFTNESS)) {
                breakTime *= 1 - (0.2 * (player.getEffect(Effect.SWIFTNESS).getAmplifier() + 1));
            }

            if (player.hasEffect(Effect.MINING_FATIGUE)) {
                breakTime *= 1 - (0.3 * (player.getEffect(Effect.MINING_FATIGUE).getAmplifier() + 1));
            }

            Enchantment eff = item.getEnchantment(Enchantment.ID_EFFICIENCY);

            if (eff != null && eff.getLevel() > 0) {
                breakTime *= 1 - (0.3 * eff.getLevel());
            }

            breakTime -= 0.15;

            long now = System.currentTimeMillis();

            BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                    (player.lastBreak + breakTime * 1000) > now, vector);

            if (!player.isCreative() && (!player.isBreakingBlock() || !target.equals(player.breakingBlock))) {
                ev.setCancelled(true);
            } else if ((player.isSurvival() || player.isAdventure()) && !target.isBreakable(item)) {
                ev.setCancelled(true);
            } else if (!player.isOp() && isInSpawnRadius(target)) {
                ev.setCancelled(true);
            } else if (!ev.getInstaBreak() && ev.isFastBreak()) {
                ev.setCancelled(true);
            }

            player.lastBreak = now;

            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return null;
            }

            if (target instanceof BlockBed) {
                // Drops only from the top part, prevent a dupe
                if ((target.getDamage() & 0x08) == 0x08) {
                    drops = ev.getDrops();
                } else {
                    drops = new Item[0];
                }
                // Make sure no item is dropped if drops are cleared in BlockBreakEvent
                ((BlockBed) target).canDropItem = ev.getDrops().length != 0;
            } else {
                drops = ev.getDrops();
            }

            dropExp = ev.getDropExp();

            if (ev.getDropPosition() != null) {
                dropPosition = ev.getDropPosition();
            }
        } else if (!target.isBreakable(item)) {
            return null;
        } else if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            drops = new Item[]{target.toItem()};
        } else {
            drops = target.getDrops(item);
        }

        if (createParticles) {
            this.addParticle(new DestroyBlockParticle(target.add(0.5), target));
        }

        BlockEntity blockEntity = this.getBlockEntity(target);
        if (blockEntity != null) {
            // Let client close the inventory for viewers, fixes refusing to open inventories afterward
            if (blockEntity instanceof InventoryHolder) {
                Inventory inventory = ((InventoryHolder) blockEntity).getInventory();
                if (inventory != null && !inventory.getViewers().isEmpty()) {
                    inventory.getViewers().clear();
                }
            }

            blockEntity.onBreak();
            blockEntity.close();

            this.updateComparatorOutputLevel(target);
        }

        Block waterlogged = target.getWaterloggingType() != Block.WaterloggingType.NO_WATERLOGGING ? target.getLevelBlock(Block.LAYER_WATERLOGGED) : null;

        target.onBreak(item, player);

        if (waterlogged instanceof BlockLiquid) {
            this.setBlock(target, Block.LAYER_WATERLOGGED, Block.get(Block.AIR), false, true);
            this.setBlock(target, Block.LAYER_NORMAL, waterlogged, false, true);
            this.scheduleUpdate(waterlogged, 1);
        }

        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            this.addSound(target, cn.nukkit.level.Sound.RANDOM_BREAK);
            this.addParticle(new ItemBreakParticle(target, item));
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }

        if (this.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {
            // For example no xp from redstone if it's mined with stone pickaxe
            // Spawners drop xp only when mined with a pickaxe and don't drop anything (to prevent xp dupe with plugins)
            if (!isSilkTouch && player != null && ((drops.length != 0 && target.getId() != Block.MONSTER_SPAWNER) || (drops.length == 0 && target.getId() == Block.MONSTER_SPAWNER && item.isPickaxe()))) {
                if (player.isSurvival() || player.isAdventure()) {
                    this.dropExpOrb(dropPosition.add(0.5, 0.5, 0.5), dropExp);
                }
            }

            for (Item drop : drops) {
                if (drop.getCount() > 0) {
                    this.dropItem(dropPosition.add(0.5, 0.5, 0.5), drop);
                }
            }
        }

        return item;
    }

    public void dropExpOrb(Vector3 source, int exp) {
        dropExpOrb(source, exp, null);
    }

    public void dropExpOrb(Vector3 source, int exp, Vector3 motion) {
        dropExpOrb(source, exp, motion, 10);
    }

    public void dropExpOrb(Vector3 source, int exp, Vector3 motion, int delay) {
        if (exp > 0) {
            Random rand = ThreadLocalRandom.current();
            for (int split : EntityXPOrb.splitIntoOrbSizes(exp)) {
                CompoundTag nbt = Entity.getDefaultNBT(source, motion == null ? new Vector3(
                                (rand.nextDouble() * 0.2 - 0.1) * 2,
                                rand.nextDouble() * 0.4,
                                (rand.nextDouble() * 0.2 - 0.1) * 2) : motion,
                        rand.nextFloat() * 360f, 0);
                nbt.putShort("Value", split);
                nbt.putShort("PickupDelay", delay);
                Entity.createEntity("XpOrb", this.getChunk(source.getChunkX(), source.getChunkZ()), nbt).spawnToAll();
            }
        }
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz) {
        return this.useItemOn(vector, item, face, fx, fy, fz, null);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return this.useItemOn(vector, item, face, fx, fy, fz, player, true);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player, boolean playSound) {
        Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (block.y > this.getMaxBlockY() || block.y < this.getMinBlockY()) {
            return null;
        }

        if (target.getId() == Item.AIR) {
            return null;
        }

        if (face == BlockFace.UP && item.getBlockId() == BlockID.SCAFFOLDING && block.getId() == BlockID.SCAFFOLDING) {
            while (block instanceof BlockScaffolding) {
                block = block.up();
            }
        }

        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face, Action.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > Player.ADVENTURE) {
                ev.setCancelled(true);
            }

            if (!player.isOp() && isInSpawnRadius(target)) {
                ev.setCancelled(true);
            }

            this.server.getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                target.onUpdate(BLOCK_UPDATE_TOUCH);

                if ((!player.sneakToBlockInteract() || item.isNull()) && target.canBeActivated() && target.onActivate(item, player)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                        this.addSound(target, cn.nukkit.level.Sound.RANDOM_BREAK);
                        this.addParticle(new ItemBreakParticle(target, item));
                        item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                    }

                    return item;
                }

                if (item.canBeActivated()) {
                    int oldCount = item.getCount();
                    int oldDamage = item.getDamage();
                    if (item.onActivate(this, player, block, target, face, fx, fy, fz)) {
                        if (oldCount != item.getCount() || oldDamage != item.getDamage()) {
                            if (item.getCount() <= 0) {
                                item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                            }
                            return item;
                        }
                    }
                }
            } else {
                if (item.getId() == ItemID.BUCKET && ItemBucket.getDamageByTarget(item.getDamage()) == BlockID.WATER) {
                    player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(Block.AIR, 0, target.getLevelBlock(BlockLayer.WATERLOGGED))}, UpdateBlockPacket.FLAG_ALL_PRIORITY, Block.LAYER_WATERLOGGED);
                }
                return null;
            }

            if (item.getId() == ItemID.BUCKET && ItemBucket.getDamageByTarget(item.getDamage()) == BlockID.WATER) {
                player.getLevel().sendBlocks(new Player[] {player}, new Block[] {target.getLevelBlock(BlockLayer.WATERLOGGED)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, Block.LAYER_WATERLOGGED);
            }
        } else if (target.canBeActivated() && target.onActivate(item, null)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
            }
            return item;
        }

        int blockID = item.getBlockId();
        if (blockID > 255 && provider instanceof Anvil) {
            return null;
        }

        Block hand;

        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        if (!(block.canBeReplaced() || (hand instanceof BlockSlab && block instanceof BlockSlab && hand.getId() == block.getId()))) {
            return null;
        }

        if (target.canBeReplaced()) {
            Block b = item.getBlockUnsafe();
            if (b != null && target.getId() == b.getId() && target.getDamage() == b.getDamage()) {
                return item; // No need to sync item
            }

            block = target;
            hand.position(block);
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null && !(hand instanceof BlockBed)) {
            Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
            for (Entity e : entities) {
                if (player == e || e instanceof EntityProjectile || e instanceof EntityItem || (e instanceof Player && ((Player) e).isSpectator() || !e.canCollide())) {
                    continue;
                }
                this.sendBlocks(player, new Block[]{block, target}, UpdateBlockPacket.FLAG_NONE); // Prevent ghost blocks
                return null; // Entity in block
            }

            if (player != null) {
                if (hand.getBoundingBox().intersectsWith(player.getNextPositionBB())) {
                    this.sendBlocks(player, new Block[]{block, target}, UpdateBlockPacket.FLAG_NONE); // Prevent ghost blocks
                    return null; // Player in block
                }
            }
        }

        if (player != null) {
            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            if (player.getGamemode() == Player.ADVENTURE) {
                Tag tag = item.getNamedTagEntry("CanPlaceOn");
                boolean canPlace = false;
                if (tag instanceof ListTag) {
                    //noinspection unchecked
                    for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() != 0 && entry.getBlockUnsafe() != null && entry.getBlockUnsafe().getId() == target.getId()) {
                                canPlace = true;
                                break;
                            }
                        }
                    }
                }
                if (!canPlace) {
                    event.setCancelled(true);
                }
            }

            if (!player.isOp() && isInSpawnRadius(target)) {
                event.setCancelled(true);
            }

            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        boolean liquidMoved = false;
        if (hand.getWaterloggingType() != Block.WaterloggingType.NO_WATERLOGGING || !hand.canBeFlowedInto() || !(block instanceof BlockLiquid || block.getLevelBlock(BlockLayer.WATERLOGGED) instanceof BlockLiquid)) {
            if ((block instanceof BlockLiquid) && ((BlockLiquid) block).usesWaterLogging() && hand.getWaterloggingType() != Block.WaterloggingType.NO_WATERLOGGING) {
                liquidMoved = true;
                this.setBlock(block, Block.LAYER_NORMAL, Block.get(BlockID.AIR), false, false);
                this.setBlock(block, Block.LAYER_WATERLOGGED, block, false, false);
            }
        }

        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
            if (liquidMoved) {
                this.setBlock(block, Block.LAYER_NORMAL, block, false, false);
                this.setBlock(block, Block.LAYER_WATERLOGGED, Block.get(BlockID.AIR), false, false);
            }
            return null;
        }

        if (item.hasPersistentDataContainer() && item.getPersistentDataContainer().convertsToBlock()) {
            block.getPersistentDataContainer().setStorage(item.getPersistentDataContainer().getStorage().clone());
        }

        if (liquidMoved) {
            this.scheduleUpdate(block, 1);
        }

        if (player != null) {
            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
        }

        if (playSound) {
            int soundData = GlobalBlockPalette.getOrCreateRuntimeId(hand.getId(), hand.getDamage());

            LevelSoundEventPacket pk = new LevelSoundEventPacket();
            pk.sound = LevelSoundEventPacket.SOUND_PLACE;
            pk.extraData = soundData;
            pk.entityIdentifier = "";
            pk.x = (float) hand.x;
            pk.y = (float) hand.y;
            pk.z = (float) hand.z;

            Server.broadcastPacket(this.getChunkPlayers(hand.getChunkX(), hand.getChunkZ()).values(), pk);
        }

        if (item.getCount() <= 0) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }
        return item;
    }

    public boolean isInSpawnRadius(Vector3 vector3) {
        Vector3 spawn;
        return server.getSpawnRadius() > -1 && new Vector2(vector3.x, vector3.z).distance(new Vector2((spawn = this.provider.getSpawn()).x, spawn.z)) <= server.getSpawnRadius();
    }

    public Entity getEntity(long entityId) {
        return this.entities.get(entityId);
    }

    public Entity[] getEntities() {
        return entities.values().toArray(new Entity[0]);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb, Entity entity) {
        List<Entity> nearby = new ArrayList<>();

        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (Entity ent : this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || (ent != entity && entity.canCollideWith(ent))) && ent.boundingBox.intersectsWith(bb)) {
                            nearby.add(ent);
                        }
                    }
                }
            }
        }

        return nearby.toArray(new Entity[0]);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null, false);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity) {
        return getNearbyEntities(bb, entity, false);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity, boolean loadChunks) {
        List<Entity> nearby = new ArrayList<>();

        int minX = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625);
        int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625);
        int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625);
        int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (Entity ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        nearby.add(ent);
                    }
                }
            }
        }

        return nearby.toArray(new Entity[0]);
    }

    public Map<Long, BlockEntity> getBlockEntities() {
        return blockEntities;
    }

    public BlockEntity getBlockEntityById(long blockEntityId) {
        return this.blockEntities.get(blockEntityId);
    }

    public Map<Long, Player> getPlayers() {
        return players;
    }

    public Map<Integer, ChunkLoader> getLoaders() {
        return loaders;
    }

    public BlockEntity getBlockEntity(Vector3 pos) {
        return getBlockEntity(null, pos);
    }

    public BlockEntity getBlockEntity(FullChunk chunk, Vector3 pos) {
        int by = pos.getFloorY();
        if (by < this.getMinBlockY() || by > this.getMaxBlockY()) return null;

        int cx = (int) pos.x >> 4;
        int cz = (int) pos.z >> 4;
        if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
            chunk = this.getChunk(cx, cz, false);
        }

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, by, (int) pos.z & 0x0f);
        }

        return null;
    }

    public BlockEntity getBlockEntityIfLoaded(Vector3 pos) {
        return getBlockEntityIfLoaded(null, pos);
    }

    public BlockEntity getBlockEntityIfLoaded(FullChunk chunk, Vector3 pos) {
        int by = pos.getFloorY();
        if (by < this.getMinBlockY() || by > this.getMaxBlockY()) return null;

        int cx = (int) pos.x >> 4;
        int cz = (int) pos.z >> 4;
        if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
            chunk = this.getChunkIfLoaded(cx, cz);
        }

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, by, (int) pos.z & 0x0f);
        }

        return null;
    }

    public Map<Long, Entity> getChunkEntities(int X, int Z) {
        return getChunkEntities(X, Z, true);
    }

    public Map<Long, Entity> getChunkEntities(int X, int Z, boolean loadChunks) {
        FullChunk chunk = loadChunks ? this.getChunk(X, Z) : this.getChunkIfLoaded(X, Z);
        return chunk != null ? chunk.getEntities() : Collections.emptyMap();
    }

    public Map<Long, BlockEntity> getChunkBlockEntities(int X, int Z) {
        FullChunk chunk;
        return (chunk = this.getChunk(X, Z)) != null ? chunk.getBlockEntities() : Collections.emptyMap();
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        return getBlockIdAt(x, y, z, Block.LAYER_NORMAL);
    }

    @Override
    public synchronized int getBlockIdAt(int x, int y, int z, BlockLayer layer) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }

        int cx = x >> 4;
        int cz = z >> 4;
        FullChunk chunk = this.getChunk(cx, cz, true);
        return chunk.getBlockId(x & 0x0f, y, z & 0x0f, layer);
    }

    public synchronized int getBlockIdAt(FullChunk chunk, int x, int y, int z) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }

        int cx = x >> 4;
        int cz = z >> 4;
        if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
            chunk = this.getChunk(cx, cz, true);
        }
        return chunk.getBlockId(x & 0x0f, y, z & 0x0f, Block.LAYER_NORMAL);
    }

    @Override
    public synchronized void setBlockIdAt(int x, int y, int z, int id) {
        this.setBlockIdAt(x, y, z, Block.LAYER_NORMAL, id);
    }

    @Override
    public synchronized void setBlockIdAt(int x, int y, int z, BlockLayer layer, int id) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }

        FullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        chunk.setBlockId(x & 0x0f, y, z & 0x0f, layer, id);
        addBlockChange(x, y, z);

        if (this.useChunkLoaderApi) {
            temporalVector.setComponents(x, y, z);
            for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
                loader.onBlockChanged(temporalVector);
            }
        }

        if (id == 0) {
            updateEntitiesOnBlockChange(chunk);
        }
    }

    @Override
    public synchronized void setBlockAt(int x, int y, int z, int id, int data) {
        this.setBlockAtLayer(x, y, z, Block.LAYER_NORMAL, id, data);
    }

    @Override
    public boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id) {
        return this.setBlockAtLayer(x, y, z, layer, id, 0);
    }

    @Override
    public synchronized boolean setBlockAtLayer(int x, int y, int z, BlockLayer layer, int id, int data) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return false;
        }

        BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        boolean changed = chunk.setBlockAtLayer(x & 0x0f, y, z & 0x0f, layer, id, data & Block.DATA_MASK);
        if (!changed) {
            return false;
        }

        addBlockChange(x, y, z);

        if (this.useChunkLoaderApi) {
            temporalVector.setComponents(x, y, z);
            for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
                loader.onBlockChanged(temporalVector);
            }
        }

        if (id == 0) {
            updateEntitiesOnBlockChange(chunk);
        }
        return true;
    }

    private void updateEntitiesOnBlockChange(FullChunk chunk) {
        if (chunk == null) {
            return;
        }
        for (Entity entity : chunk.getEntities().values()) {
            if (entity.updateMode % 2 == 1) {
                entity.updateMode = 1;
            }
        }
    }

    public synchronized int getBlockExtraDataAt(int x, int y, int z) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }
        return this.getChunk(x >> 4, z >> 4, true).getBlockExtraData(x & 0x0f, y, z & 0x0f);
    }

    public synchronized void setBlockExtraDataAt(int x, int y, int z, int id, int data) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }
        this.getChunk(x >> 4, z >> 4, true).setBlockExtraData(x & 0x0f, y, z & 0x0f, (data << 8) | id);

        this.sendBlockExtraData(x, y, z, id, data);
    }

    @Override
    public synchronized int getBlockDataAt(int x, int y, int z) {
        return this.getBlockDataAt(null, x, y, z, Block.LAYER_NORMAL);
    }

    @Override
    public synchronized int getBlockDataAt(int x, int y, int z, BlockLayer layer) {
        return this.getBlockDataAt(null, x, y, z, layer);
    }

    public synchronized int getBlockDataAt(FullChunk chunk, int x, int y, int z, BlockLayer layer) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }
        int cx = x >> 4;
        int cz = z >> 4;
        if (chunk == null || cx != chunk.getX() || cz != chunk.getZ()) {
            chunk = this.getChunk(cx, cz, true);
        }
        return chunk.getBlockData(x & 0x0f, y, z & 0x0f, layer);
    }

    @Override
    public synchronized void setBlockDataAt(int x, int y, int z, int data) {
        this.setBlockDataAt(x, y, z, Block.LAYER_NORMAL, data);
    }

    @Override
    public synchronized void setBlockDataAt(int x, int y, int z, BlockLayer layer, int data) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }

        this.getChunk(x >> 4, z >> 4, true).setBlockData(x & 0x0f, y, z & 0x0f, layer, data & Block.DATA_MASK);
        addBlockChange(x, y, z);

        if (this.useChunkLoaderApi) {
            temporalVector.setComponents(x, y, z);
            for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
                loader.onBlockChanged(temporalVector);
            }
        }
    }

    public synchronized int getBlockSkyLightAt(int x, int y, int z) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }
        return this.getChunk(x >> 4, z >> 4, true).getBlockSkyLight(x & 0x0f, y, z & 0x0f);
    }

    public synchronized void setBlockSkyLightAt(int x, int y, int z, int level) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }
        this.getChunk(x >> 4, z >> 4, true).setBlockSkyLight(x & 0x0f, y, z & 0x0f, level & 0x0f);
    }

    public synchronized int getBlockLightAt(int x, int y, int z) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return 0;
        }
        BaseFullChunk chunk = this.getChunkIfLoaded(x >> 4, z >> 4);
        return chunk == null ? 0 : chunk.getBlockLight(x & 0x0f, y, z & 0x0f);
    }

    public synchronized void setBlockLightAt(int x, int y, int z, int level) {
        if (y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return;
        }

        BaseFullChunk c = this.getChunkIfLoaded(x >> 4, z >> 4);
        if (null != c) {
            c.setBlockLight(x & 0x0f, y, z & 0x0f, level & 0x0f);
        }
    }

    public int getBiomeId(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 0x0f, z & 0x0f);
    }

    public void setBiomeId(int x, int z, int biomeId) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, z & 0x0f, biomeId & 0x0f);
    }

    public void setBiomeId(int x, int z, byte biomeId) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, z & 0x0f, biomeId & 0x0f);
    }

    public int getHeightMap(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHeightMap(x & 0x0f, z & 0x0f);
    }

    public void setHeightMap(int x, int z, int value) {
        this.getChunk(x >> 4, z >> 4, true).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
    }

    public Map<Long, ? extends FullChunk> getChunks() {
        return provider.getLoadedChunks();
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        BaseFullChunk chunk = this.provider.getLoadedChunk(index);
        if (chunk == null) {
            chunk = this.forceLoadChunk(index, chunkX, chunkZ, create);
        }
        return chunk;
    }

    @Nullable
    public BaseFullChunk getChunkIfLoaded(int chunkX, int chunkZ) {
        return this.provider.getLoadedChunk(Level.chunkHash(chunkX, chunkZ));
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk) {
        generateChunkCallback(x, z, chunk, true);
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk, boolean isPopulated) {
        long index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.contains(index)) {
            FullChunk oldChunk = this.getChunk(x, z, false);
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    this.chunkPopulationLock.remove(Level.chunkHash(x + xx, z + zz));
                }
            }
            this.chunkPopulationQueue.remove(index);
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
            chunk = this.getChunk(x, z, false);
            if (chunk != null && (oldChunk == null || !isPopulated) && chunk.isPopulated()
                    && chunk.getProvider() != null) {
                this.server.getPluginManager().callEvent(new ChunkPopulateEvent(chunk));

                if (this.useChunkLoaderApi) {
                    for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                        loader.onChunkPopulated(chunk);
                    }
                }
            }
        } else if (this.chunkGenerationQueue.contains(index) || this.chunkPopulationLock.contains(index)) {
            this.chunkGenerationQueue.remove(index);
            this.chunkPopulationLock.remove(index);
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
        } else {
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk) {
        this.setChunk(chunkX, chunkZ, chunk, true);
    }

    public void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk, boolean unload) {
        if (chunk == null) {
            return;
        }

        long index = Level.chunkHash(chunkX, chunkZ);
        FullChunk oldChunk = this.getChunk(chunkX, chunkZ, false);

        if (oldChunk != chunk) {
            if (unload && oldChunk != null) {
                this.unloadChunk(chunkX, chunkZ, false, false);
            } else {
                Map<Long, Entity> oldEntities = oldChunk != null ? oldChunk.getEntities() : Collections.emptyMap();

                Map<Long, BlockEntity> oldBlockEntities = oldChunk != null ? oldChunk.getBlockEntities() : Collections.emptyMap();

                if (!oldEntities.isEmpty()) {
                    Iterator<Map.Entry<Long, Entity>> iter = oldEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, Entity> entry = iter.next();
                        Entity entity = entry.getValue();
                        chunk.addEntity(entity);
                        if (oldChunk != null) {
                            iter.remove();
                            oldChunk.removeEntity(entity);
                            entity.chunk = chunk;
                        }
                    }
                }

                if (!oldBlockEntities.isEmpty()) {
                    Iterator<Map.Entry<Long, BlockEntity>> iter = oldBlockEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<Long, BlockEntity> entry = iter.next();
                        BlockEntity blockEntity = entry.getValue();
                        chunk.addBlockEntity(blockEntity);
                        if (oldChunk != null) {
                            iter.remove();
                            oldChunk.removeBlockEntity(blockEntity);
                            blockEntity.chunk = chunk;
                        }
                    }
                }

            }
            this.provider.setChunk(chunkX, chunkZ, chunk);
        }

        chunk.setChanged();

        if (!this.isChunkInUse(index)) {
            this.unloadChunkRequest(chunkX, chunkZ);
        } else {
            for (ChunkLoader loader : this.getChunkLoaders(chunkX, chunkZ)) {
                loader.onChunkChanged(chunk);
            }
        }
    }

    public int getHighestBlockAt(int x, int z) {
        return this.getHighestBlockAt(x, z, true);
    }

    public int getHighestBlockAt(int x, int z, boolean cache) {
        return this.getChunk(x >> 4, z >> 4, true).getHighestBlockAt(x & 0x0f, z & 0x0f, cache);
    }

    public BlockColor getMapColorAt(int x, int z) {
        return getMapColorAt(x, getHighestBlockAt(x, z, false), z);
    }

    public BlockColor getMapColorAt(int x, int y, int z) {
        int minY = this.getMinBlockY();
        if (y < minY || y > this.getMaxBlockY()) return BlockColor.VOID_BLOCK_COLOR;

        int checkY = y;
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        while (checkY > minY) {
            Block block = this.getBlock(chunk, x, checkY, z, BlockLayer.NORMAL, false);
            if (block instanceof BlockGrass) {
                return getGrassColorAt(chunk, x, z);
                //} else if (block instanceof BlockWater) {
                //    return getWaterColorAt(chunk, x, z);
            } else {
                BlockColor blockColor = block.getColor();
                if (blockColor.getAlpha() == 0x00) {
                    checkY--;
                } else {
                    return blockColor;
                }
            }
        }

        return BlockColor.VOID_BLOCK_COLOR;
    }

    private BlockColor getGrassColorAt(FullChunk chunk, int x, int z) {
        int biome = chunk.getBiomeId(x & 0x0f, z & 0x0f);

        switch (biome) {
            case 0: //ocean
            case 7: //river
            case 9: //end
            case 24: //deep ocean
                return new BlockColor("#8eb971");
            case 1: //plains
            case 16: //beach
            case 129: //sunflower plains
                return new BlockColor("#91bd59");
            case 2: //desert
            case 8: //hell
            case 17: //desert hills
            case 35: //savanna
            case 36: //savanna plateau
            case 130: //desert m
            case 163: //savanna m
            case 164: //savanna plateau m
                return new BlockColor("#bfb755");
            case 3: //extreme hills
            case 20: //extreme hills edge
            case 25: //stone beach
            case 34: //extreme hills
            case 131: //extreme hills m
            case 162: //extreme hills plus m
                return new BlockColor("#8ab689");
            case 4: //forest
            case 132: //flower forest
                return new BlockColor("#79c05a");
            case 5: //taiga
            case 19: //taiga hills
            case 32: //mega taiga
            case 33: //mega taiga hills
            case 133: //taiga m
            case 160: //mega spruce taiga
                return new BlockColor("#86b783");
            case 6: //swamp
            case 134: //swampland m
                return new BlockColor("#6A7039");
            case 10: //frozen ocean
            case 11: //frozen river
            case 12: //ice plains
            case 30: //cold taiga
            case 31: //cold taiga hills
            case 140: //ice plains spikes
            case 158: //cold taiga m
                return new BlockColor("#80b497");
            case 14: //mushroom island
            case 15: //mushroom island shore
                return new BlockColor("#55c93f");
            case 18: //forest hills
            case 27: //birch forest
            case 28: //birch forest hills
            case 155: //birch forest m
            case 156: //birch forest hills m
                return new BlockColor("#88bb67");
            case 21: //jungle
            case 22: //jungle hills
            case 149: //jungle m
                return new BlockColor("#59c93c");
            case 23: //jungle edge
            case 151: //jungle edge m
                return new BlockColor("#64c73f");
            case 26: //cold beach
                return new BlockColor("#83b593");
            case 29: //roofed forest
            case 157: //roofed forest m
                return new BlockColor("#507a32");
            case 37: //mesa
            case 38: //mesa plateau f
            case 39: //mesa plateau
            case 165: //mesa bryce
            case 166: //mesa plateau f m
            case 167: //mesa plateau m
                return new BlockColor("#90814d");
            default:
                return BlockColor.GRASS_BLOCK_COLOR;
        }
    }

    public boolean isChunkLoaded(int x, int z) {
        return this.provider.isChunkLoaded(x, z);
    }

    private boolean areNeighboringChunksLoaded(long hash) {
        return this.provider.isChunkLoaded(hash + 1) &&
               this.provider.isChunkLoaded(hash - 1) &&
               this.provider.isChunkLoaded(hash + (4294967296L)) &&
               this.provider.isChunkLoaded(hash - (4294967296L));
    }

    public boolean isChunkGenerated(int x, int z) {
        FullChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isGenerated();
    }

    public boolean isChunkPopulated(int x, int z) {
        FullChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isPopulated();
    }

    public Position getSpawnLocation() {
        return Position.fromObject(this.provider.getSpawn(), this);
    }

    public void setSpawnLocation(Vector3 pos) {
        Position previousSpawn = this.getSpawnLocation();
        this.provider.setSpawn(new Vector3(pos.x, pos.y, pos.z));
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        pk.dimension = this.getDimension();
        for (Player p : getPlayers().values()) p.dataPacket(pk);
    }

    public void requestChunk(int x, int z, Player player) {
        if (player.getLoaderId() <= 0) {
            throw new IllegalStateException(player.getName() + " has no chunk loader");
        }

        long index = Level.chunkHash(x, z);

        this.chunkSendQueue.putIfAbsent(index, new Int2ObjectOpenHashMap<>());
        this.chunkSendQueue.get(index).put(player.getLoaderId(), player);
    }

    private void sendChunk(int x, int z, long index, DataPacket packet) {
        if (this.chunkSendTasks.contains(index)) {
            for (Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, packet);
                }
            }

            this.chunkSendQueue.remove(index);
            this.chunkSendTasks.remove(index);
        }
    }

    private void processChunkRequest() {
        for (long index : this.chunkSendQueue.keySet()) {
            if (this.chunkSendTasks.contains(index)) {
                continue;
            }
            int x = getHashX(index);
            int z = getHashZ(index);
            this.chunkSendTasks.add(index);
            BaseFullChunk chunk = getChunk(x, z);
            if (chunk != null) {
                BatchPacket packet = chunk.getChunkPacket();
                if (packet != null) {
                    this.sendChunk(x, z, index, packet);
                    continue;
                }
            }
            this.provider.requestChunkTask(x, z);
        }
    }

    public void chunkRequestCallback(long timestamp, int x, int z, int subChunkCount, byte[] payload, long index) {
        if (server.cacheChunks) {
            BatchPacket data = Player.getChunkCacheFromData(x, z, subChunkCount, payload, this.getDimension());
            BaseFullChunk chunk = this.getChunkIfLoaded(x, z);
            if (chunk != null && chunk.getChanges() <= timestamp) {
                chunk.setChunkPacket(data);
            }
            this.sendChunk(x, z, index, data);
            return;
        }

        if (this.chunkSendTasks.contains(index)) {
            for (Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, subChunkCount, payload, this.getDimension());
                }
            }

            this.chunkSendQueue.remove(index);
            this.chunkSendTasks.remove(index);
        }
    }

    public void removeEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.remove(entity.getId());
            this.checkSleep();
        } else {
            entity.close();
        }

        this.entities.remove(entity.getId());
        this.updateEntities.remove(entity.getId());
    }

    public void addEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.put(entity.getId(), (Player) entity);
        }
        this.entities.put(entity.getId(), entity);
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != this) {
            throw new LevelException("BlockEntity is not in this level");
        }
        blockEntities.put(blockEntity.getId(), blockEntity);
    }

    public void scheduleBlockEntityUpdate(BlockEntity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("BlockEntity is not in this level");
        }
        if (!updateBlockEntities.contains(entity)) {
            updateBlockEntities.add(entity);
        }
    }

    public void removeBlockEntity(BlockEntity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("BlockEntity is not in this level");
        }
        blockEntities.remove(entity.getId());
        updateBlockEntities.remove(entity);
    }

    public boolean isChunkInUse(int x, int z) {
        return isChunkInUse(Level.chunkHash(x, z));
    }

    public boolean isChunkInUse(long hash) {
        Map<Integer, ChunkLoader> map = this.chunkLoaders.get(hash);
        return map != null && !map.isEmpty();
    }

    public boolean loadChunk(int x, int z) {
        return this.loadChunk(x, z, true);
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        long index = Level.chunkHash(x, z);
        if (this.provider.isChunkLoaded(index)) {
            return true;
        }
        return forceLoadChunk(index, x, z, generate) != null;
    }

    private synchronized BaseFullChunk forceLoadChunk(long index, int x, int z, boolean generate) {
        BaseFullChunk chunk = this.provider.getChunk(x, z, generate);

        if (chunk == null) {
            if (generate) {
                throw new IllegalStateException("Could not create new chunk");
            }
            return null;
        }

        if (chunk.getProvider() != null) {
            this.server.getPluginManager().callEvent(new ChunkLoadEvent(chunk, !chunk.isGenerated()));
        } else {
            this.unloadChunk(x, z, false);
            return chunk;
        }

        chunk.initChunk();

        /*if (!chunk.isLightPopulated() && chunk.isPopulated()) {
            this.server.getScheduler().scheduleAsyncTask(new LightPopulationTask(this, chunk));
        }*/

        if (this.isChunkInUse(index)) {
            this.unloadQueue.remove(index);

            if (this.useChunkLoaderApi) {
                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkLoaded(chunk);
                }
            }
        } else {
            this.unloadQueue.put(index, System.currentTimeMillis());
        }
        return chunk;
    }

    private void queueUnloadChunk(int x, int z) {
        long index = Level.chunkHash(x, z);
        this.unloadQueue.put(index, System.currentTimeMillis());
    }

    public boolean unloadChunkRequest(int x, int z) {
        return this.unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        if ((safe && this.isChunkInUse(x, z)) || this.isSpawnChunk(x, z)) {
            return false;
        }

        this.queueUnloadChunk(x, z);

        return true;
    }

    public void cancelUnloadChunkRequest(int x, int z) {
        this.cancelUnloadChunkRequest(Level.chunkHash(x, z));
    }

    public void cancelUnloadChunkRequest(long hash) {
        this.unloadQueue.remove(hash);
    }

    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean safe) {
        return this.unloadChunk(x, z, safe, true);
    }

    public synchronized boolean unloadChunk(int x, int z, boolean safe, boolean trySave) {
        if (safe && this.isChunkInUse(x, z)) {
            return false;
        }

        if (!this.isChunkLoaded(x, z)) {
            return true;
        }

        BaseFullChunk chunk = this.getChunk(x, z);

        if (chunk != null && chunk.getProvider() != null) {
            ChunkUnloadEvent ev = new ChunkUnloadEvent(chunk);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        try {
            if (chunk != null) {
                if (trySave && this.saveOnUnloadEnabled) {
                    boolean needSave = chunk.hasChanged();

                    if (!needSave) {
                        for (Entity e : chunk.getEntities().values()) {
                            if (e.canSaveToStorage() && !e.ignoredAsSaveReason()) {
                                needSave = true;
                                break;
                            }
                        }
                    }

                    if (needSave) {
                        this.provider.setChunk(x, z, chunk);
                        this.provider.saveChunk(x, z, chunk);
                    }
                }

                if (this.useChunkLoaderApi) {
                    for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                        loader.onChunkUnloaded(chunk);
                    }
                }
            }
            this.provider.unloadChunk(x, z, safe);
        } catch (Exception e) {
            MainLogger logger = this.server.getLogger();
            logger.error(this.server.getLanguage().translateString("nukkit.level.chunkUnloadError", e.toString()), e);
        }

        return true;
    }

    public boolean isSpawnChunk(int X, int Z) {
        Vector3 cachedSpawnPos = this.provider.getSpawn();

        return Math.abs(X - (cachedSpawnPos.getChunkX())) <= 1 && Math.abs(Z - (cachedSpawnPos.getChunkZ())) <= 1;
    }

    public Position getSafeSpawn() {
        return this.getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3 spawn) {
        if (spawn == null /*|| spawn.y < 1*/) {
            spawn = this.getSpawnLocation();
        }

        Vector3 pos = spawn.floor();
        FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
        int x = (int) pos.x & 0x0f;
        int z = (int) pos.z & 0x0f;
        if (chunk != null && chunk.isGenerated()) {
            int y = NukkitMath.clamp((int) pos.y, this.getMinBlockY() + 1, this.getMaxBlockY() - 1);
            boolean wasAir = chunk.getBlockId(x, y - 1, z) == 0;
            for (; y > this.getMinBlockY(); --y) {
                int fullId = chunk.getFullBlock(x, y, z);
                Block block = Block.get(fullId >> Block.DATA_BITS, fullId & Block.DATA_MASK);
                if (this.isFullBlock(block)) {
                    if (wasAir) {
                        y++;
                    }
                    break;
                } else {
                    wasAir = true;
                }
            }

            for (; y >= this.getMinBlockY() && y < this.getMaxBlockY(); y++) {
                int fullId = chunk.getFullBlock(x, y + 1, z);
                Block block = Block.get(fullId >>  Block.DATA_BITS, fullId & Block.DATA_MASK);
                if (!this.isFullBlock(block)) {
                    fullId = chunk.getFullBlock(x, y, z);
                    block = Block.get(fullId >>  Block.DATA_BITS, fullId & Block.DATA_MASK);
                    if (!this.isFullBlock(block)) {
                        return new Position(pos.x + 0.5, y + 0.51, pos.z + 0.5, this); // Hack: + 0.51 for slabs
                    }
                } else {
                    ++y;
                }
            }

            pos.y = y;
        }

        return new Position(pos.x + 0.5, pos.y + 0.1, pos.z + 0.5, this);
    }

    public int getTime() {
        return time;
    }

    public boolean isDaytime() {
        return this.skyLightSubtracted < 4;
    }

    public long getCurrentTick() {
        return this.levelCurrentTick;
    }

    public String getName() {
        return this.folderName;
        //return this.provider.getName();
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setTime(int time) {
        this.time = time;
        this.sendTime();
    }

    public void stopTime() {
        this.stopTime = true;
        this.sendTime();
    }

    public void startTime() {
        this.stopTime = false;
        this.sendTime();
    }

    @Override
    public long getSeed() {
        return this.provider.getSeed();
    }

    public void setSeed(int seed) {
        this.provider.setSeed(seed);
    }

    public boolean populateChunk(int x, int z) {
        return this.populateChunk(x, z, false);
    }

    public boolean populateChunk(int x, int z, boolean force) {
        long index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.contains(index) || this.chunkPopulationQueue.size() >= this.chunkPopulationQueueSize && !force) {
            return false;
        }

        BaseFullChunk chunk = this.getChunk(x, z, true);
        if (!chunk.isPopulated()) {
            boolean populate = true;

            top:
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (this.chunkPopulationLock.contains(Level.chunkHash(x + xx, z + zz))) {
                        populate = false;
                        break top;
                    }
                }
            }

            if (populate) {
                if (!this.chunkPopulationQueue.contains(index)) {
                    this.chunkPopulationQueue.add(index);
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            this.chunkPopulationLock.add(Level.chunkHash(x + xx, z + zz));
                        }
                    }

                    this.server.getScheduler().scheduleAsyncTask(null, this.generatorTaskFactory.populateChunkTask(chunk, this));
                }
            }
            return false;
        }

        return true;
    }

    @Override
    public AsyncTask populateChunkTask(BaseFullChunk chunk, Level level) {
        return new PopulationTask(this, chunk);
    }

    public void generateChunk(int x, int z) {
        this.generateChunk(x, z, false);
    }

    public void generateChunk(int x, int z, boolean force) {
        if (this.chunkGenerationQueue.size() >= this.chunkGenerationQueueSize && !force) {
            return;
        }

        long index = Level.chunkHash(x, z);
        if (!this.chunkGenerationQueue.contains(index)) {
            this.chunkGenerationQueue.add(index);
            this.server.getScheduler().scheduleAsyncTask(null, this.generatorTaskFactory.generateChunkTask(this.getChunk(x, z, true), this));
        }
    }

    @Override
    public AsyncTask generateChunkTask(BaseFullChunk chunk, Level level) {
        return new GenerationTask(level, chunk);
    }

    public void regenerateChunk(int x, int z) {
        this.unloadChunk(x, z, false, false);
        this.cancelUnloadChunkRequest(x, z);
        provider.setChunk(x, z, provider.getEmptyChunk(x, z));
        this.generateChunk(x, z, true);
    }

    public void doChunkGarbageCollection() {
        // Remove all invalid block entities
        if (!blockEntities.isEmpty()) {
            ObjectIterator<BlockEntity> iter = blockEntities.values().iterator();
            while (iter.hasNext()) {
                BlockEntity blockEntity = iter.next();
                if (blockEntity != null) {
                    if (!blockEntity.isValid()) {
                        iter.remove();
                        blockEntity.close();
                    }
                } else {
                    iter.remove();
                }
            }
        }

        for (Map.Entry<Long, ? extends FullChunk> entry : provider.getLoadedChunks().entrySet()) {
            long index = entry.getKey();
            if (!this.unloadQueue.containsKey(index)) {
                FullChunk chunk = entry.getValue();
                int X = chunk.getX();
                int Z = chunk.getZ();
                if (!this.isSpawnChunk(X, Z)) {
                    this.unloadChunkRequest(X, Z, true);
                }
            }
        }

        this.provider.doGarbageCollection();
    }


    public void doGarbageCollection(long allocatedTime) {
        long start = System.currentTimeMillis();
        if (unloadChunks(start, allocatedTime, false)) {
            allocatedTime -= (System.currentTimeMillis() - start);
            provider.doGarbageCollection(allocatedTime);
        }
    }

    public void unloadChunks() {
        this.unloadChunks(false);
    }

    public void unloadChunks(boolean force) {
        this.unloadChunks(50, force);
    }

    public void unloadChunks(int maxUnload, boolean force) {
        if (server.holdWorldSave && !force && this.saveOnUnloadEnabled) {
            return;
        }

        if (!this.unloadQueue.isEmpty()) {
            long now = System.currentTimeMillis();

            int unloaded = 0;
            LongList toRemove = null;
            for (Long2LongMap.Entry entry : unloadQueue.long2LongEntrySet()) {
                if (!force) {
                    long time = entry.getLongValue();
                    if (unloaded > maxUnload) {
                        break;
                    } else if (time > (now - 30000)) {
                        continue;
                    }
                }

                long index = entry.getLongKey();
                if (isChunkInUse(index)) {
                    continue;
                }

                if (toRemove == null) toRemove = new LongArrayList();
                toRemove.add(index);
                unloaded++;
            }

            if (toRemove != null) {
                int size = toRemove.size();
                for (int i = 0; i < size; i++) {
                    long index = toRemove.getLong(i);
                    int X = getHashX(index);
                    int Z = getHashZ(index);

                    if (this.unloadChunk(X, Z, true)) {
                        this.unloadQueue.remove(index);
                    }
                }
            }
        }
    }

    private int lastUnloadIndex;

    /**
     * @param now current time
     * @param allocatedTime allocated time
     * @param force force
     * @return true if there is allocated time remaining
     */
    private boolean unloadChunks(long now, long allocatedTime, boolean force) {
        if (server.holdWorldSave && !force && this.saveOnUnloadEnabled) {
            return false;
        }

        if (!this.unloadQueue.isEmpty()) {
            boolean result = true;
            int maxIterations = this.unloadQueue.size();

            if (lastUnloadIndex > maxIterations) lastUnloadIndex = 0;
            ObjectIterator<Long2LongMap.Entry> iter = this.unloadQueue.long2LongEntrySet().iterator();
            if (lastUnloadIndex != 0) iter.skip(lastUnloadIndex);

            LongList toUnload = null;

            for (int i = 0; i < maxIterations; i++) {
                if (!iter.hasNext()) {
                    iter = this.unloadQueue.long2LongEntrySet().iterator();
                }
                Long2LongMap.Entry entry = iter.next();

                if (!force) {
                    long time = entry.getLongValue();
                    if (time > (now - 30000)) {
                        continue;
                    }
                }

                long index = entry.getLongKey();
                if (isChunkInUse(index)) {
                    continue;
                }

                if (toUnload == null) toUnload = new LongArrayList();
                toUnload.add(index);
            }

            if (toUnload != null) {
                //long[] arr = toUnload.toLongArray();
                for (long index : toUnload) {
                    int X = getHashX(index);
                    int Z = getHashZ(index);
                    if (this.unloadChunk(X, Z, true)) {
                        this.unloadQueue.remove(index);
                        if (System.currentTimeMillis() - now >= allocatedTime) {
                            result = false;
                            break;
                        }
                    }
                }
            }
            return result;
        } else {
            return true;
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception {
        this.server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) throws Exception {
        return this.server.getLevelMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) throws Exception {
        return this.server.getLevelMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception {
        this.server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public void addPlayerMovement(Entity entity, double x, double y, double z, double yaw, double pitch, double headYaw) {
        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = entity.getId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        pk.onGround = entity.onGround;

        if (entity.riding != null) {
            pk.ridingEid = entity.riding.getId();
            pk.mode = MovePlayerPacket.MODE_PITCH;
        }

        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public void addEntityMovement(Entity entity, double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = entity.getId();
        pk.x = x;
        pk.y = y;
        pk.z = z;
        pk.yaw = yaw;
        pk.headYaw = headYaw;
        pk.pitch = pitch;
        pk.onGround = entity.onGround;

        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public boolean isRaining() {
        return this.raining;
    }

    public boolean setRaining(boolean raining) {
        WeatherChangeEvent ev = new WeatherChangeEvent(this, raining);
        this.server.getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        this.raining = raining;

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft

        if (raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            int time = ThreadLocalRandom.current().nextInt(12000) + 12000;
            pk.data = time;
            setRainTime(time);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    public boolean isThundering() {
        return raining && this.thundering;
    }

    public boolean setThundering(boolean thundering) {
        ThunderChangeEvent ev = new ThunderChangeEvent(this, thundering);
        this.server.getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        if (thundering && !raining) {
            setRaining(true);
        }

        this.thundering = thundering;

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft
        if (thundering) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            int time = ThreadLocalRandom.current().nextInt(12000) + 3600;
            pk.data = time;
            setThunderTime(time);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public void sendWeather(Player[] players) {
        if (players == null) {
            players = this.getPlayers().values().toArray(new Player[0]);
        }

        LevelEventPacket pk = new LevelEventPacket();
        if (this.raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = this.rainTime;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
        }

        Server.broadcastPacket(players, pk);

        pk = new LevelEventPacket();
        if (this.isThundering()) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = this.thunderTime;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
        }

        Server.broadcastPacket(players, pk);
    }

    public void sendWeather(Player player) {
        if (player != null) {
            this.sendWeather(new Player[]{player});
        }
    }

    public void sendWeather(Collection<Player> players) {
        if (players == null) {
            players = this.getPlayers().values();
        }
        this.sendWeather(players.toArray(new Player[0]));
    }

    public int getDimension() {
        return this.dimensionData.getDimensionId();
    }

    public int getMinBlockY() {
        return this.dimensionData.getMinHeight();
    }

    public int getMaxBlockY() {
        return this.dimensionData.getMaxHeight();
    }

    public boolean canBlockSeeSky(Vector3 pos) {
        return this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY();
    }

    public boolean canBlockSeeSky(Block block) {
        return this.getHighestBlockAt((int) block.getX(), (int) block.getZ()) < block.getY();
    }

    public int getStrongPower(Vector3 pos, BlockFace direction) {
        return getStrongPower(null, pos, direction);
    }

    private int getStrongPower(FullChunk cachedChunk, Vector3 pos, BlockFace direction) {
        return this.getBlock(cachedChunk, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), true).getStrongPower(direction);
    }

    public int getStrongPower(Vector3 pos) {
        return getStrongPower(null, pos);
    }

    private int getStrongPower(FullChunk cachedChunk, Vector3 pos) {
        int i = 0;

        for (BlockFace face : BlockFace.values()) {
            i = Math.max(i, this.getStrongPower(cachedChunk, pos.getSideVec(face), face));

            if (i >= 15) {
                return i;
            }
        }

        return i;
    }

    public boolean isSidePowered(Vector3 pos, BlockFace face) {
        return this.getRedstonePower(pos, face) > 0;
    }

    public int getRedstonePower(Vector3 pos, BlockFace face) {
        return getRedstonePower(null, pos, face);
    }

    private int getRedstonePower(FullChunk cachedChunk, Vector3 pos, BlockFace face) {
        Block block = this.getBlock(cachedChunk, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), true);
        return block.isNormalBlock() ? this.getStrongPower(cachedChunk, pos) : block.getWeakPower(face);
    }

    public boolean isBlockPowered(Vector3 pos) {
        return isBlockPowered(null, pos);
    }

    public boolean isBlockPowered(FullChunk cachedChunk, Vector3 pos) {
        for (BlockFace face : BlockFace.values()) {
            if (this.getRedstonePower(cachedChunk, pos.getSideVec(face), face) > 0) {
                return true;
            }
        }
        return false;
    }

    public int isBlockIndirectlyGettingPowered(Vector3 pos) {
        int power = 0;

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getRedstonePower(pos.getSideVec(face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    public boolean isAreaLoaded(AxisAlignedBB bb) {
        int minX = NukkitMath.floorDouble(bb.getMinX()) >> 4;
        int minZ = NukkitMath.floorDouble(bb.getMinZ()) >> 4;
        int maxX = NukkitMath.floorDouble(bb.getMaxX()) >> 4;
        int maxZ = NukkitMath.floorDouble(bb.getMaxZ()) >> 4;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (!this.isChunkLoaded(x, z)) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getUpdateLCG() {
        return (this.updateLCG = (this.updateLCG * 3) ^ LCG_CONSTANT);
    }

    public boolean createPortal(Block target) {
        if (this.getDimension() == DIMENSION_THE_END) return false;
        final int maxPortalSize = 23;
        final int targX = target.getFloorX();
        final int targY = target.getFloorY();
        final int targZ = target.getFloorZ();
        //check if there's air above (at least 3 blocks)
        for (int i = 1; i < 4; i++) {
            if (this.getBlockIdAt(targX, targY + i, targZ) != BlockID.AIR) {
                return false;
            }
        }
        int sizePosX = 0;
        int sizeNegX = 0;
        int sizePosZ = 0;
        int sizeNegZ = 0;
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(targX + i, targY, targZ) == BlockID.OBSIDIAN) {
                sizePosX++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(targX - i, targY, targZ) == BlockID.OBSIDIAN) {
                sizeNegX++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(targX, targY, targZ + i) == BlockID.OBSIDIAN) {
                sizePosZ++;
            } else {
                break;
            }
        }
        for (int i = 1; i < maxPortalSize; i++) {
            if (this.getBlockIdAt(targX, targY, targZ - i) == BlockID.OBSIDIAN) {
                sizeNegZ++;
            } else {
                break;
            }
        }
        //plus one for target block
        int sizeX = sizePosX + sizeNegX + 1;
        int sizeZ = sizePosZ + sizeNegZ + 1;
        if (sizeX >= 2 && sizeX <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            int scanY = targY + 1;
            for (int i = 0; i < sizePosX + 1; i++) {
                //this must be air
                if (this.getBlockIdAt(scanX + i, scanY, targZ) != BlockID.AIR) {
                    return false;
                }
                if (this.getBlockIdAt(scanX + i + 1, scanY, targZ) == BlockID.OBSIDIAN) {
                    scanX += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(scanX + 1, scanY, targZ) != BlockID.OBSIDIAN) {
                return false;
            }

            int innerWidth = 0;
            LOOP: for (int i = 0; i < 21; i++) {
                int id = this.getBlockIdAt(scanX - i, scanY, targZ);
                switch (id) {
                    case BlockID.AIR:
                        innerWidth++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            int innerHeight = 0;
            LOOP: for (int i = 0; i < 21; i++) {
                int id = this.getBlockIdAt(scanX, scanY + i, targZ);
                switch (id) {
                    case BlockID.AIR:
                        innerHeight++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            if (!(innerWidth <= 21
                    && innerWidth >= 2
                    && innerHeight <= 21
                    && innerHeight >= 3))   {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++)    {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(scanX - width, scanY + height, targZ) != BlockID.OBSIDIAN) {
                            return false;
                        }
                    }
                } else {
                    if (this.getBlockIdAt(scanX + 1, scanY + height, targZ) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(scanX - innerWidth, scanY + height, targZ) != BlockID.OBSIDIAN) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(scanX - width, scanY + height, targZ) != BlockID.AIR) {
                            return false;
                        }
                    }
                }
            }

            for (int height = 0; height < innerHeight; height++)    {
                for (int width = 0; width < innerWidth; width++)    {
                    this.setBlock(new Vector3(scanX - width, scanY + height, targZ), Block.get(BlockID.NETHER_PORTAL));
                }
            }

            return true;
        } else if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanY = targY + 1;
            int scanZ = targZ;
            for (int i = 0; i < sizePosZ + 1; i++) {
                //this must be air
                if (this.getBlockIdAt(targX, scanY, scanZ + i) != BlockID.AIR) {
                    return false;
                }
                if (this.getBlockIdAt(targX, scanY, scanZ + i + 1) == BlockID.OBSIDIAN) {
                    scanZ += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(targX, scanY, scanZ + 1) != BlockID.OBSIDIAN) {
                return false;
            }

            int innerWidth = 0;
            LOOP: for (int i = 0; i < 21; i++) {
                int id = this.getBlockIdAt(targX, scanY, scanZ - i);
                switch (id) {
                    case BlockID.AIR:
                        innerWidth++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            int innerHeight = 0;
            LOOP: for (int i = 0; i < 21; i++) {
                int id = this.getBlockIdAt(targX, scanY + i, scanZ);
                switch (id) {
                    case BlockID.AIR:
                        innerHeight++;
                        break;
                    case BlockID.OBSIDIAN:
                        break LOOP;
                    default:
                        return false;
                }
            }
            if (!(innerWidth <= 21
                    && innerWidth >= 2
                    && innerHeight <= 21
                    && innerHeight >= 3))   {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++)    {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(targX, scanY + height, scanZ - width) != BlockID.OBSIDIAN) {
                            return false;
                        }
                    }
                } else {
                    if (this.getBlockIdAt(targX, scanY + height, scanZ + 1) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(targX, scanY + height, scanZ - innerWidth) != BlockID.OBSIDIAN) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(targX, scanY + height, scanZ - width) != BlockID.AIR) {
                            return false;
                        }
                    }
                }
            }

            for (int height = 0; height < innerHeight; height++)    {
                for (int width = 0; width < innerWidth; width++)    {
                    this.setBlock(new Vector3(targX, scanY + height, scanZ - width), Block.get(BlockID.NETHER_PORTAL));
                }
            }

            return true;
        }

        return false;
    }

    public Position calculatePortalMirror(Vector3 portal) {
        double x;
        double y;
        double z;

        if (this.getDimension() == DIMENSION_NETHER) {
            x = Math.floor(portal.getFloorX() << 3);
            y = NukkitMath.clamp(EnumLevel.mRound32(portal.getFloorY()), 70, 256 - 10);
            z = Math.floor(portal.getFloorZ() << 3);

            return new Position(x, y, z, Server.getInstance().getDefaultLevel());
        } else {
            Level nether = Server.getInstance().getLevelByName("nether");
            if (nether == null) {
                return null;
            }

            x = Math.floor(portal.getFloorX() >> 3);
            y = NukkitMath.clamp(EnumLevel.mRound32(portal.getFloorY()), 70, 128 - 10);
            z = Math.floor(portal.getFloorZ() >> 3);

            return new Position(x, y, z, nether);
        }
    }

    public void setGeneratorTaskFactory(GeneratorTaskFactory factory) {
        if (factory == null) {
            throw new NullPointerException("GeneratorTaskFactory can not be null!");
        }
        this.generatorTaskFactory = factory;
    }

    public boolean isBlockWaterloggedAt(FullChunk chunk, int x, int y, int z) {
        if (chunk == null || y < this.getMinBlockY() || y > this.getMaxBlockY()) {
            return false;
        }
        int block = chunk.getBlockId(x & 0x0f, y, z & 0x0f, Block.LAYER_WATERLOGGED);
        return Block.isWater(block);
    }

    @Override
    public String toString() {
        return "Level@" + Integer.toHexString(hashCode()) + "[" + this.folderName + ']';
    }

    public void asyncChunk(BaseChunk chunk, long timestamp, int x, int z) {
        this.asyncChunkThread.queue(chunk, timestamp, x, z, this.dimensionData);
    }

    public PersistentDataContainer getPersistentDataContainer(Vector3 position) {
        return this.getPersistentDataContainer(position, false);
    }

    public PersistentDataContainer getPersistentDataContainer(Vector3 position, boolean create) {
        BlockEntity blockEntity = this.getBlockEntity(position);
        if (blockEntity != null) {
            return blockEntity.getPersistentDataContainer();
        }

        if (create) {
            CompoundTag compound = BlockEntity.getDefaultCompound(position, BlockEntity.PERSISTENT_CONTAINER);
            blockEntity = BlockEntity.createBlockEntity(BlockEntity.PERSISTENT_CONTAINER, this.getChunk(position.getChunkX(), position.getChunkZ()), compound);

            if (blockEntity == null) {
                throw new IllegalStateException("Failed to create persistent container block entity at " + position);
            }
            return blockEntity.getPersistentDataContainer();
        }

        return new DelegatePersistentDataContainer() {
            @Override
            protected PersistentDataContainer createDelegate() {
                return getPersistentDataContainer(position, true);
            }
        };
    }

    public boolean hasPersistentDataContainer(Vector3 position) {
        BlockEntity blockEntity = this.getBlockEntity(position);
        return blockEntity != null && blockEntity.hasPersistentDataContainer();
    }
}
