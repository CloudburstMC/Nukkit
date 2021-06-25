package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.PopChunkManager;
import cn.nukkit.level.generator.task.GenerationTask;
import cn.nukkit.level.generator.task.LightPopulationTask;
import cn.nukkit.level.generator.task.PopulationTask;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.*;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.BlockUpdateScheduler;
import cn.nukkit.timings.LevelTimings;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsHistory;
import co.aikar.timings.TimingsManager;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class Level implements ChunkManager, Metadatable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Level[] EMPTY_ARRAY = new Level[0];
    
    static {
        Timings.isTimingsEnabled(); // Fixes Concurrency issues on static initialization
    }

    private static int levelIdCounter = 1;
    private static int chunkLoaderCounter = 1;
    @SuppressWarnings({"java:S1444", "java:S3008"})
    public static int COMPRESSION_LEVEL = 8;

    public static final int BLOCK_UPDATE_NORMAL = 1;
    public static final int BLOCK_UPDATE_RANDOM = 2;
    public static final int BLOCK_UPDATE_SCHEDULED = 3;
    public static final int BLOCK_UPDATE_WEAK = 4;
    public static final int BLOCK_UPDATE_TOUCH = 5;
    public static final int BLOCK_UPDATE_REDSTONE = 6;
    public static final int BLOCK_UPDATE_TICK = 7;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int BLOCK_UPDATE_MOVED = dynamic(1_000_000);

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

    // Lower values use less memory
    public static final int MAX_BLOCK_CACHE = 512;

    // The blocks that can randomly tick
    private static final boolean[] randomTickBlocks = new boolean[Block.MAX_BLOCK_ID];

    static {
        randomTickBlocks[BlockID.GRASS] = true;
        randomTickBlocks[BlockID.FARMLAND] = true;
        randomTickBlocks[BlockID.MYCELIUM] = true;
        randomTickBlocks[BlockID.SAPLING] = true;
        randomTickBlocks[BlockID.LEAVES] = true;
        randomTickBlocks[BlockID.LEAVES2] = true;
        randomTickBlocks[BlockID.SNOW_LAYER] = true;
        randomTickBlocks[BlockID.ICE] = true;
        randomTickBlocks[BlockID.LAVA] = true;
        randomTickBlocks[BlockID.STILL_LAVA] = true;
        randomTickBlocks[BlockID.CACTUS] = true;
        randomTickBlocks[BlockID.BEETROOT_BLOCK] = true;
        randomTickBlocks[BlockID.CARROT_BLOCK] = true;
        randomTickBlocks[BlockID.POTATO_BLOCK] = true;
        randomTickBlocks[BlockID.MELON_STEM] = true;
        randomTickBlocks[BlockID.PUMPKIN_STEM] = true;
        randomTickBlocks[BlockID.WHEAT_BLOCK] = true;
        randomTickBlocks[BlockID.SUGARCANE_BLOCK] = true;
        randomTickBlocks[BlockID.RED_MUSHROOM] = true;
        randomTickBlocks[BlockID.BROWN_MUSHROOM] = true;
        randomTickBlocks[BlockID.NETHER_WART_BLOCK] = true;
        randomTickBlocks[BlockID.FIRE] = true;
        randomTickBlocks[BlockID.GLOWING_REDSTONE_ORE] = true;
        randomTickBlocks[BlockID.COCOA_BLOCK] = true;
        randomTickBlocks[BlockID.VINE] = true;
        randomTickBlocks[BlockID.CORAL_FAN] = true;
        randomTickBlocks[BlockID.CORAL_FAN_DEAD] = true;
        randomTickBlocks[BlockID.BLOCK_KELP] = true;
        randomTickBlocks[BlockID.SWEET_BERRY_BUSH] = true;
        randomTickBlocks[BlockID.TURTLE_EGG] = true;
        randomTickBlocks[BlockID.BAMBOO] = true;
        randomTickBlocks[BlockID.BAMBOO_SAPLING] = true;
        randomTickBlocks[BlockID.CRIMSON_NYLIUM] = true;
        randomTickBlocks[BlockID.WARPED_NYLIUM] = true;
        randomTickBlocks[BlockID.TWISTING_VINES] = true;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean canRandomTick(int blockId) {
        return blockId < randomTickBlocks.length && randomTickBlocks[blockId];
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static void setCanRandomTick(int blockId, boolean newValue) {
        randomTickBlocks[blockId] = newValue;
    }

    private final Long2ObjectOpenHashMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Player> players = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Entity> entities = new Long2ObjectOpenHashMap<>();

    public final Long2ObjectOpenHashMap<Entity> updateEntities = new Long2ObjectOpenHashMap<>();

    private final ConcurrentLinkedQueue<BlockEntity> updateBlockEntities = new ConcurrentLinkedQueue<>();

    private boolean cacheChunks = false;

    private final Server server;
    
    private final int levelId;

    private LevelProvider provider;

    private final Int2ObjectOpenHashMap<ChunkLoader> loaders = new Int2ObjectOpenHashMap<>();

    private final Int2IntMap loaderCounter = new Int2IntOpenHashMap();

    private final Long2ObjectOpenHashMap<Map<Integer, ChunkLoader>> chunkLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Map<Integer, Player>> playerLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Deque<DataPacket>> chunkPackets = new Long2ObjectOpenHashMap<>();

    private final Long2LongMap unloadQueue = Long2LongMaps.synchronize(new Long2LongOpenHashMap());

    private float time;
    public boolean stopTime;

    public float skyLightSubtracted;

    private String folderName;

    private Vector3 mutableBlock;

    // Avoid OOM, gc'd references result in whole chunk being sent (possibly higher cpu)
    private final Long2ObjectOpenHashMap<SoftReference<Map<Character, Object>>> changedBlocks = new Long2ObjectOpenHashMap<>();
    // Storing the vector is redundant
    private final Object changeBlocksPresent = new Object();
    // Storing extra blocks past 512 is redundant
    private final Map<Character, Object> changeBlocksFullMap = new HashMap<Character, Object>() {
        @Override
        public int size() {
            return Character.MAX_VALUE;
        }
    };


    private final BlockUpdateScheduler updateQueue;
    private final Queue<QueuedUpdate> normalUpdateQueue = new ConcurrentLinkedDeque<>();
//    private final TreeSet<BlockUpdateEntry> updateQueue = new TreeSet<>();
//    private final List<BlockUpdateEntry> nextTickUpdates = Lists.newArrayList();
    //private final Map<BlockVector3, Integer> updateQueueIndex = new HashMap<>();

    private final ConcurrentMap<Long, Int2ObjectMap<Player>> chunkSendQueue = new ConcurrentHashMap<>();
    private final LongSet chunkSendTasks = new LongOpenHashSet();

    private final Long2ObjectOpenHashMap<Boolean> chunkPopulationQueue = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<Boolean> chunkPopulationLock = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<Boolean> chunkGenerationQueue = new Long2ObjectOpenHashMap<>();
    private int chunkGenerationQueueSize = 8;
    private int chunkPopulationQueueSize = 2;

    private boolean autoSave;

    private BlockMetadataStore blockMetadata;

    private boolean useSections;

    private Position temporalPosition;
    private Vector3 temporalVector;

    public int sleepTicks = 0;

    private int chunkTickRadius;
    private final Long2IntMap chunkTickList = new Long2IntOpenHashMap();
    private int chunksPerTicks;
    private boolean clearChunksOnTick;

    private int updateLCG = ThreadLocalRandom.current().nextInt();

    private static final int LCG_CONSTANT = 1013904223;

    public LevelTimings timings;

    private int tickRate;
    public int tickRateTime = 0;
    public int tickRateCounter = 0;

    private Class<? extends Generator> generatorClass;
    private IterableThreadLocal<Generator> generators = new IterableThreadLocal<Generator>() {
        @Override
        public Generator init() {
            try {
                Generator generator = generatorClass.getConstructor(Map.class).newInstance(requireProvider().getGeneratorOptions());
                NukkitRandom rand = new NukkitRandom(getSeed());
                ChunkManager manager;
                if (Server.getInstance().isPrimaryThread()) {
                    generator.init(Level.this, rand);
                }
                generator.init(new PopChunkManager(getSeed()), rand);
                return generator;
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    private boolean raining = false;
    private int rainTime = 0;
    private boolean thundering = false;
    private int thunderTime = 0;

    private long levelCurrentTick = 0;

    private int dimension;

    public GameRules gameRules;

    public Level(Server server, String name, String path, Class<? extends LevelProvider> provider) {
        this(server, name, path,
                ()-> {
                    try {
                        return (boolean) provider.getMethod("usesChunkSection").invoke(null);
                    } catch (ReflectiveOperationException e) {
                        throw new LevelException("usesChunkSection of "+provider+" failed", e);
                    }
                },
                (level, levelPath) -> {
                    try {
                        return provider.getConstructor(Level.class, String.class).newInstance(level, levelPath);
                    } catch (ReflectiveOperationException e) {
                        throw new LevelException("Constructor of "+provider+" failed", e);
                    }
                }
        );
    }

    @PowerNukkitOnly("Makes easier to create tests")
    @Since("1.4.0.0-PN")
    Level(Server server, String name, File path, boolean usesChunkSection, LevelProvider provider) {
        this(server, name, path.getAbsolutePath()+"/", ()-> usesChunkSection, (lvl, p)-> provider);
    }

    @PowerNukkitOnly("Makes easier to create tests")
    @Since("1.4.0.0-PN")        
    Level(Server server, String name, String path, BooleanSupplier usesChunkSection, BiFunction<Level, String, LevelProvider> provider) {
        this.levelId = levelIdCounter++;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();
        this.provider = provider.apply(this, path);
        LevelProvider levelProvider = requireProvider();
        this.timings = new LevelTimings(this);
        levelProvider.updateLevelName(name);

        log.info(this.server.getLanguage().translateString("nukkit.level.preparing",
                TextFormat.GREEN + levelProvider.getName() + TextFormat.WHITE));

        this.generatorClass = Generator.getGenerator(levelProvider.getGenerator());

        this.useSections = usesChunkSection.getAsBoolean();

        this.folderName = name;
        this.time = levelProvider.getTime();

        this.raining = levelProvider.isRaining();
        this.rainTime = this.requireProvider().getRainTime();
        if (this.rainTime <= 0) {
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.thundering = levelProvider.isThundering();
        this.thunderTime = levelProvider.getThunderTime();
        if (this.thunderTime <= 0) {
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.levelCurrentTick = levelProvider.getCurrentTick();
        this.updateQueue = new BlockUpdateScheduler(this, levelCurrentTick);

        this.chunkTickRadius = Math.min(this.server.getViewDistance(),
                Math.max(1, this.server.getConfig("chunk-ticking.tick-radius", 4)));
        this.chunksPerTicks = this.server.getConfig("chunk-ticking.per-tick", 40);
        this.chunkGenerationQueueSize = this.server.getConfig("chunk-generation.queue-size", 8);
        this.chunkPopulationQueueSize = this.server.getConfig("chunk-generation.population-queue-size", 2);
        this.chunkTickList.clear();
        this.clearChunksOnTick = this.server.getConfig("chunk-ticking.clear-tick-list", true);
        this.cacheChunks = this.server.getConfig("chunk-sending.cache-chunks", false);
        this.temporalPosition = new Position(0, 0, 0, this);
        this.temporalVector = new Vector3(0, 0, 0);
        this.tickRate = 1;

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);
    }

    public static long chunkHash(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public static long blockHash(int x, int y, int z) {
        if (y < 0 || y >= 256) {
            throw new IllegalArgumentException("Y coordinate y is out of range!");
        }
        return (((long) x & (long) 0xFFFFFFF) << 36) | (((long) y & (long) 0xFF) << 28) | ((long) z & (long) 0xFFFFFFF);
    }

    public static char localBlockHash(double x, double y, double z) {
        byte hi = (byte) (((int) x & 15) + (((int) z & 15) << 4));
        byte lo = (byte) y;
        return (char) (((hi & 0xFF) << 8) | (lo & 0xFF));
    }

    public static Vector3 getBlockXYZ(long chunkHash, char blockHash) {
        int hi = (byte) (blockHash >>> 8);
        int lo = (byte) blockHash;
        int y = lo & 0xFF;
        int x = (hi & 0xF) + (getHashX(chunkHash) << 4);
        int z = ((hi >> 4) & 0xF) + (getHashZ(chunkHash) << 4);
        return new Vector3(x, y, z);
    }

    public static int chunkBlockHash(int x, int y, int z) {
        return (x << 12) | (z << 8) | y;
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
        this.dimension = generator.getDimension();
        this.gameRules = this.requireProvider().getGamerules();

        log.info("Preparing start region for level \"{}\"", this.getFolderName());
        Position spawn = this.getSpawnLocation();
        this.populateChunk(spawn.getChunkX(), spawn.getChunkZ(), true);
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

    public final LevelProvider getProvider() {
        return provider;
    }

    /**
     * Returns the level provider if it exists. Tries to close and unregister the level and then throw an exception if it doesn't.
     * @throws LevelException If the level is already closed
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public final LevelProvider requireProvider() {
        LevelProvider levelProvider = getProvider();
        if (levelProvider == null) {
            LevelException levelException = new LevelException("The level \"" + getFolderName() + "\" is already closed (have no providers)");
            try {
                close();
            } catch (Exception e) {
                levelException.addSuppressed(e);
            }
            throw levelException;
        }
        return levelProvider;
    }

    public final int getId() {
        return this.levelId;
    }

    public void close() {
        LevelProvider levelProvider = this.provider;
        if (levelProvider != null) {
            if (this.getAutoSave()) {
                this.save(true);
            }
            levelProvider.close();
        }
        
        this.provider = null;
        this.blockMetadata = null;
        this.temporalPosition = null;
        this.server.getLevels().remove(this.levelId);
        this.generators.clean();
    }

    public void addSound(Vector3 pos, Sound sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch, Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(Player.EMPTY_ARRAY));
    }

    public void addSound(Vector3 pos, Sound sound, float volume, float pitch, Player... players) {
        Preconditions.checkArgument(volume >= 0 && volume <= 1, "Sound volume must be between 0 and 1");
        Preconditions.checkArgument(pitch >= 0, "Sound pitch must be higher than 0");

        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = pos.getFloorX();
        packet.y = pos.getFloorY();
        packet.z = pos.getFloorZ();

        if (players == null || players.length == 0) {
            addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, packet);
        } else {
            Server.broadcastPacket(players, packet);
        }
    }
    
    public void addLevelEvent(int type, int data) {
        addLevelEvent(type, data, null);
    }
    
    public void addLevelEvent(int type, int data, Vector3 pos) {
        if (pos == null) {
            addLevelEvent(type, data, 0, 0, 0);
        } else {
            addLevelEvent(type, data, (float) pos.x, (float) pos.y, (float) pos.z);
        }
    }
    
    public void addLevelEvent(int type, int data, float x, float y, float z) {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = type;
        packet.x = x;
        packet.y = y;
        packet.z = z;
        packet.data = data;
        
        this.addChunkPacket(NukkitMath.floorFloat(x) >> 4, NukkitMath.floorFloat(z) >> 4, packet);
    }

    public void addLevelEvent(Vector3 pos, int event) {
        this.addLevelEvent(pos, event, 0);
    }

    @Since("1.4.0.0-PN")
    public void addLevelEvent(Vector3 pos, int event, int data) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = event;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;
        pk.data = data;

        addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    public void addLevelSoundEvent(Vector3 pos, int type, int data, int entityType) {
        addLevelSoundEvent(pos, type, data, entityType, false, false);
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    public void addLevelSoundEvent(Vector3 pos, int type, int data, int entityType, boolean isBaby, boolean isGlobal) {
        String identifier = AddEntityPacket.LEGACY_IDS.getOrDefault(entityType, ":");
        addLevelSoundEvent(pos, type, data, identifier, isBaby, isGlobal);
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
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
    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
    public void addLevelSoundEvent(Vector3 pos, int type, int data) {
        this.addLevelSoundEvent(pos, type, data, ":", false, false);
    }

    @PowerNukkitDifference(info = "Default sound method changed to addSound", since = "1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "Old method, use addSound(pos, Sound.<SOUND_VALUE>).", since = "1.4.0.0-PN")
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

        this.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
    }

    public void addParticle(Particle particle) {
        this.addParticle(particle, (Player[]) null);
    }

    public void addParticle(Particle particle, Player player) {
        this.addParticle(particle, new Player[]{player});
    }

    public void addParticle(Particle particle, Player[] players) {
        DataPacket[] packets = particle.encode();

        if (players == null) {
            if (packets != null) {
                for (DataPacket packet : packets) {
                    this.addChunkPacket((int) particle.x >> 4, (int) particle.z >> 4, packet);
                }
            }
        } else {
            if (packets != null) {
                if (packets.length == 1) {
                    Server.broadcastPacket(players, packets[0]);
                } else {
                    this.server.batchPackets(players, packets, false);
                }
            }
        }
    }

    public void addParticle(Particle particle, Collection<Player> players) {
        this.addParticle(particle, players.toArray(Player.EMPTY_ARRAY));
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect) {
        this.addParticleEffect(pos, particleEffect, -1, this.dimension, (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, this.dimension, (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, (Player[]) null);
    }

    public void addParticleEffect(Vector3 pos, ParticleEffect particleEffect, long uniqueEntityId, int dimensionId, Collection<Player> players) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, players.toArray(Player.EMPTY_ARRAY));
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
            ev.setCancelled();
        }

        this.server.getPluginManager().callEvent(ev);

        if (!force && ev.isCancelled()) {
            return false;
        }

        log.info(this.server.getLanguage().translateString("nukkit.level.unloading",
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
        if (this.playerLoaders.containsKey(index)) {
            return new HashMap<>(this.playerLoaders.get(index));
        } else {
            return new HashMap<>();
        }
    }

    public ChunkLoader[] getChunkLoaders(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunkLoaders.containsKey(index)) {
            return this.chunkLoaders.get(index).values().toArray(ChunkLoader.EMPTY_ARRAY);
        } else {
            return ChunkLoader.EMPTY_ARRAY;
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
        int hash = loader.getLoaderId();
        long index = Level.chunkHash(chunkX, chunkZ);
        if (!this.chunkLoaders.containsKey(index)) {
            this.chunkLoaders.put(index, new HashMap<>());
            this.playerLoaders.put(index, new HashMap<>());
        } else if (this.chunkLoaders.get(index).containsKey(hash)) {
            return;
        }

        this.chunkLoaders.get(index).put(hash, loader);
        if (loader instanceof Player) {
            this.playerLoaders.get(index).put(hash, (Player) loader);
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
                if (--count == 0) {
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
            this.time += tickRate;
        }
    }

    public void sendTime(Player... players) {
        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;

        Server.broadcastPacket(players, pk);
    }

    public void sendTime() {
        sendTime(this.players.values().toArray(Player.EMPTY_ARRAY));
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public void doTick(int currentTick) {
        this.timings.doTick.startTiming();
        
        requireProvider();

        updateBlockLight(lightQueue);
        this.checkTime();

        if (currentTick % 1200 == 0) { // Send time to client every 60 seconds to make sure it stay in sync
            this.sendTime();
        }

        // Tick Weather
        if (this.dimension != DIMENSION_NETHER && this.dimension != DIMENSION_THE_END && gameRules.getBoolean(GameRule.DO_WEATHER_CYCLE)) {
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
                    for (Map.Entry<Long, ? extends FullChunk> entry : getChunks().entrySet()) {
                        performThunder(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

        this.levelCurrentTick++;

        this.unloadChunks();
        this.timings.doTickPending.startTiming();

        int polled = 0;

        this.updateQueue.tick(this.getCurrentTick());
        this.timings.doTickPending.stopTiming();

        while (!this.normalUpdateQueue.isEmpty()) {
            QueuedUpdate queuedUpdate = this.normalUpdateQueue.poll();
            Block block = getBlock(queuedUpdate.block, queuedUpdate.block.layer);
            BlockUpdateEvent event = new BlockUpdateEvent(block);
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                block.onUpdate(BLOCK_UPDATE_NORMAL);
                if (queuedUpdate.neighbor != null) {
                    block.onNeighborChange(queuedUpdate.neighbor.getOpposite());
                }
            }
        }

        TimingsHistory.entityTicks += this.updateEntities.size();
        this.timings.entityTick.startTiming();

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
        this.timings.entityTick.stopTiming();

        TimingsHistory.tileEntityTicks += this.updateBlockEntities.size();
        this.timings.blockEntityTick.startTiming();
        this.updateBlockEntities.removeIf(blockEntity -> !blockEntity.isValid() || !blockEntity.onUpdate());
        this.timings.blockEntityTick.stopTiming();

        this.timings.tickChunks.startTiming();
        this.tickChunks();
        this.timings.tickChunks.stopTiming();

        synchronized (changedBlocks) {
            if (!this.changedBlocks.isEmpty()) {
                if (!this.players.isEmpty()) {
                    ObjectIterator<Long2ObjectMap.Entry<SoftReference<Map<Character, Object>>>> iter = changedBlocks.long2ObjectEntrySet().fastIterator();
                    while (iter.hasNext()) {
                        Long2ObjectMap.Entry<SoftReference<Map<Character, Object>>> entry = iter.next();
                        long index = entry.getLongKey();
                        Map<Character, Object> blocks = entry.getValue().get();
                        int chunkX = Level.getHashX(index);
                        int chunkZ = Level.getHashZ(index);
                        if (blocks == null || blocks.size() > MAX_BLOCK_CACHE) {
                            FullChunk chunk = this.getChunk(chunkX, chunkZ);
                            for (Player p : this.getChunkPlayers(chunkX, chunkZ).values()) {
                                p.onChunkChanged(chunk);
                            }
                        } else {
                            Collection<Player> toSend = this.getChunkPlayers(chunkX, chunkZ).values();
                            Player[] playerArray = toSend.toArray(Player.EMPTY_ARRAY);
                            Vector3[] blocksArray = new Vector3[blocks.size()];
                            int i = 0;
                            for (char blockHash : blocks.keySet()) {
                                Vector3 hash = getBlockXYZ(index, blockHash);
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
                Player[] chunkPlayers = this.getChunkPlayers(chunkX, chunkZ).values().toArray(Player.EMPTY_ARRAY);
                if (chunkPlayers.length > 0) {
                    for (DataPacket pk : this.chunkPackets.get(index)) {
                        Server.broadcastPacket(chunkPlayers, pk);
                    }
                }
            }
            this.chunkPackets.clear();
        }

        if (gameRules.isStale()) {
            GameRulesChangedPacket packet = new GameRulesChangedPacket();
            packet.gameRules = gameRules;
            Server.broadcastPacket(players.values().toArray(Player.EMPTY_ARRAY), packet);
            gameRules.refresh();
        }

        this.timings.doTick.stopTiming();
    }

    private void performThunder(long index, FullChunk chunk) {
        if (areNeighboringChunksLoaded(index)) return;
        if (ThreadLocalRandom.current().nextInt(10000) == 0) {
            int LCG = this.getUpdateLCG() >> 2;

            int chunkX = chunk.getX() * 16;
            int chunkZ = chunk.getZ() * 16;
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

            EntityLightning bolt = new EntityLightning(chunk, nbt);
            LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
            getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                bolt.spawnToAll();
            } else {
                bolt.setEffect(false);
            }

            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_THUNDER, -1, EntityLightning.NETWORK_ID);
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_EXPLODE, -1, EntityLightning.NETWORK_ID);
        }
    }

    public Vector3 adjustPosToNearbyEntity(Vector3 pos) {
        pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
        AxisAlignedBB axisalignedbb = new SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), 255, pos.getZ()).expand(3, 3, 3);
        List<Entity> list = new ArrayList<>();

        for (Entity entity : this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && canBlockSeeSky(entity)) {
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

        boolean resetTime = true;
        for (Player p : this.getPlayers().values()) {
            if (!p.isSleeping()) {
                resetTime = false;
                break;
            }
        }

        if (resetTime) {
            int time = this.getTime() % Level.TIME_FULL;

            if (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE) {
                this.setTime(this.getTime() + Level.TIME_FULL - time);

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
        sendBlockExtraData(x, y, z, id, data, players.toArray(Player.EMPTY_ARRAY));
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
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, 0);
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, 1);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags) {
        this.sendBlocks(target, blocks, flags, 0);
        this.sendBlocks(target, blocks, flags, 1);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, boolean optimizeRebuilds) {
        this.sendBlocks(target, blocks, flags, 0, optimizeRebuilds);
        this.sendBlocks(target, blocks, flags, 1, optimizeRebuilds);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, int dataLayer) {
        this.sendBlocks(target, blocks, flags, dataLayer, false);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, int dataLayer, boolean optimizeRebuilds) {
        int size = 0;
        for (Vector3 block : blocks) {
            if (block != null) size++;
        }
        int packetIndex = 0;
        UpdateBlockPacket[] packets = new UpdateBlockPacket[size];
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
            updateBlockPacket.dataLayer = dataLayer;
            int runtimeId;
            if (b instanceof Block) {
                runtimeId = ((Block) b).getRuntimeId();
            } else {
                runtimeId = getBlockRuntimeId((int) b.x, (int) b.y, (int) b.z, dataLayer);
            }
            try {
                updateBlockPacket.blockRuntimeId = runtimeId;
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("Unable to create BlockUpdatePacket at (" +
                        b.x + ", " + b.y + ", " + b.z + ") in " + getName(), e);
            }
            packets[packetIndex++] = updateBlockPacket;
        }
        this.server.batchPackets(target, packets);
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || this.loaders.isEmpty()) {
            this.chunkTickList.clear();
            return;
        }

        int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5))));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = Math.min(randRange, this.chunkTickRadius);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (!this.loaders.isEmpty()) {
            for (ChunkLoader loader : this.loaders.values()) {
                int chunkX = (int) loader.getX() >> 4;
                int chunkZ = (int) loader.getZ() >> 4;

                long index = Level.chunkHash(chunkX, chunkZ);
                int existingLoaders = Math.max(0, this.chunkTickList.getOrDefault(index, 0));
                this.chunkTickList.put(index, existingLoaders + 1);
                for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                    int dx = random.nextInt(2 * randRange) - randRange;
                    int dz = random.nextInt(2 * randRange) - randRange;
                    long hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
                    if (!this.chunkTickList.containsKey(hash) && requireProvider().isChunkLoaded(hash)) {
                        this.chunkTickList.put(hash, -1);
                    }
                }
            }
        }

        boolean blockTest = false;

        if (!chunkTickList.isEmpty()) {
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
                if ((chunk = this.getChunk(chunkX, chunkZ, false)) == null) {
                    iter.remove();
                    continue;
                } else if (loaders <= 0) {
                    iter.remove();
                }

                for (Entity entity : chunk.getEntities().values()) {
                    entity.scheduleUpdate();
                }
                int tickSpeed = gameRules.getInteger(GameRule.RANDOM_TICK_SPEED);

                if (tickSpeed > 0) {
                    if (this.useSections) {
                        for (ChunkSection section : ((Chunk) chunk).getSections()) {
                            if (!(section instanceof EmptyChunkSection)) {
                                int Y = section.getY();
                                for (int i = 0; i < tickSpeed; ++i) {
                                    int lcg = this.getUpdateLCG();
                                    int x = lcg & 0x0f;
                                    int y = lcg >>> 8 & 0x0f;
                                    int z = lcg >>> 16 & 0x0f;

                                    BlockState state = section.getBlockState(x, y, z);
                                    if (randomTickBlocks[state.getBlockId()]) {
                                        Block block = state.getBlockRepairing(this, chunkX * 16 + x, (Y << 4) + y, chunkZ * 16 + z);
                                        block.onUpdate(BLOCK_UPDATE_RANDOM);
                                    }
                                }
                            }
                        }
                    } else {
                        for (int Y = 0; Y < 8 && (Y < 3 || blockTest); ++Y) {
                            blockTest = false;
                            for (int i = 0; i < tickSpeed; ++i) {
                                int lcg = this.getUpdateLCG();
                                int x = lcg & 0x0f;
                                int y = lcg >>> 8 & 0x0f;
                                int z = lcg >>> 16 & 0x0f;

                                BlockState state = chunk.getBlockState(x, y + (Y << 4), z);
                                blockTest = blockTest || !state.equals(BlockState.AIR);
                                if (Level.randomTickBlocks[state.getBlockId()]) {
                                    Block block = state.getBlockRepairing(this, x, y + (Y << 4), z);
                                    block.onUpdate(BLOCK_UPDATE_RANDOM);
                                }
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
        if (!this.getAutoSave() && !force) {
            return false;
        }

        this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

        LevelProvider levelProvider = this.requireProvider();
        levelProvider.setTime((int) this.time);
        levelProvider.setRaining(this.raining);
        levelProvider.setRainTime(this.rainTime);
        levelProvider.setThundering(this.thundering);
        levelProvider.setThunderTime(this.thunderTime);
        levelProvider.setCurrentTick(this.levelCurrentTick);
        levelProvider.setGameRules(this.gameRules);
        this.saveChunks();
        if (levelProvider instanceof BaseLevelProvider) {
            levelProvider.saveLevelData();
        }

        return true;
    }

    public void saveChunks() {
        requireProvider().saveChunks();
    }

    @Deprecated @DeprecationDetails(reason = "Was moved to RedstoneComponent", since = "1.4.0.0-PN",
            replaceWith = "RedstoneComponent#updateAroundRedstone", by = "PowerNukkit")
    public void updateAroundRedstone(Vector3 pos, BlockFace face) {
        Location loc = new Location(pos.x, pos.y, pos.z, this);
        RedstoneComponent.updateAroundRedstone(loc, face);
    }

    public void updateComparatorOutputLevel(Vector3 v) {
        updateComparatorOutputLevelSelective(v, true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void updateComparatorOutputLevelSelective(Vector3 v, boolean observer) {
        for (BlockFace face : Plane.HORIZONTAL) {
            temporalVector.setComponentsAdding(v, face);

            if (this.isChunkLoaded((int) temporalVector.x >> 4, (int) temporalVector.z >> 4)) {
                Block block1 = this.getBlock(temporalVector);
                
                if (block1.getId() == BlockID.OBSERVER) {
                    if (observer) {
                        block1.onNeighborChange(face.getOpposite());
                    }
                } else if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                } else if (block1.isNormalBlock()) {
                    block1 = this.getBlock(temporalVector.setComponentsAdding(temporalVector, face));

                    if (BlockRedstoneDiode.isDiode(block1)) {
                        block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                    }
                }
            }
        }
        
        if (!observer) {
            return;
        }
        
        for (BlockFace face : Plane.VERTICAL) {
            Block block1 = this.getBlock(temporalVector.setComponentsAdding(v, face));

            if (block1.getId() == BlockID.OBSERVER) {
                block1.onNeighborChange(face.getOpposite());
            }
        }
    }

    public void updateAround(Vector3 pos) {
        Block block = getBlock(pos);
        for (BlockFace face : BlockFace.values()) {
            final Block side = block.getSideAtLayer(0, face);
            normalUpdateQueue.add(new QueuedUpdate(side, face));
            normalUpdateQueue.add(new QueuedUpdate(side.getLevelBlockAtLayer(1), face));
        }
    }

    public void updateAround(int x, int y, int z) {
        updateAround(new Vector3(x, y, z));
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
        if (block.getId() == 0 || (checkArea && !this.isChunkLoaded(block.getFloorX() >> 4, block.getFloorZ() >> 4))) {
            return;
        }

        BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, ((long) delay) + getCurrentTick(), priority);

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
        int maxX = minX + 16 + 2;
        int minZ = (chunk.getZ() << 4) - 2;
        int maxZ = minZ + 16 + 2;

        return this.getPendingBlockUpdates(new SimpleAxisAlignedBB(minX, 0, minZ, maxX, 256, maxZ));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        return updateQueue.getPendingBlockUpdates(boundingBox);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<Block> scanBlocks(@Nonnull AxisAlignedBB bb, @Nonnull BiPredicate<BlockVector3, BlockState> condition) {
        BlockVector3 min = new BlockVector3(NukkitMath.floorDouble(bb.getMinX()), NukkitMath.floorDouble(bb.getMinY()), NukkitMath.floorDouble(bb.getMinZ()));
        BlockVector3 max = new BlockVector3(NukkitMath.floorDouble(bb.getMaxX()), NukkitMath.floorDouble(bb.getMaxY()), NukkitMath.floorDouble(bb.getMaxZ()));
        ChunkVector2 minChunk = min.getChunkVector();
        ChunkVector2 maxChunk = max.getChunkVector();
        return IntStream.rangeClosed(minChunk.getX(), maxChunk.getX())
                .mapToObj(x-> IntStream.rangeClosed(minChunk.getZ(), maxChunk.getZ()).mapToObj(z-> new ChunkVector2(x, z)))
                .flatMap(Function.identity()).parallel()
                .map(this::getChunk).filter(Objects::nonNull)
                .flatMap(chunk-> chunk.scanBlocks(min, max, condition))
                .collect(Collectors.toList());
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }
    
    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
        return getCollisionBlocks(bb, targetFirst, false);
    }
    
    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst, boolean ignoreCollidesCheck) {
        return getCollisionBlocks(bb, targetFirst, ignoreCollidesCheck, block -> block.getId() != 0);
    }
    
    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst, boolean ignoreCollidesCheck, Predicate<Block> condition) {
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
                        Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            return new Block[]{block};
                        }
                    }
                }
            }
        } else {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && condition.test(block) && (ignoreCollidesCheck || block.collidesWithBB(bb))) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.toArray(Block.EMPTY_ARRAY);
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
                    Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities && !ent.canPassThrough()) {
                    collides.add(ent.boundingBox.clone());
                }
            }
        }

        return collides.toArray(AxisAlignedBB.EMPTY_ARRAY);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Rounds the AABB to have precision 4 before checking for collision, fix PowerNukkit#506")
    public boolean hasCollision(Entity entity, AxisAlignedBB bb, boolean entities) {
        int minX = NukkitMath.floorDouble(NukkitMath.round(bb.getMinX(), 4));
        int minY = NukkitMath.floorDouble(NukkitMath.round(bb.getMinY(), 4));
        int minZ = NukkitMath.floorDouble(NukkitMath.round(bb.getMinZ(), 4));
        int maxX = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxX(), 4) - 0.00001);
        int maxY = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxY(), 4) - 0.00001);
        int maxZ = NukkitMath.ceilDouble(NukkitMath.round(bb.getMaxZ(), 4) - 0.00001);

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        if (entities) {
            return this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).length > 0;
        }
        return false;
    }

    public int getFullLight(Vector3 pos) {
        FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
        int level = 0;
        if (chunk != null) {
            level = chunk.getBlockSkyLight((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
            level -= this.skyLightSubtracted;

            if (level < 15) {
                level = Math.max(chunk.getBlockLight((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f),
                        level);
            }
        }

        return level;
    }

    public int calculateSkylightSubtracted(float tickDiff) {
        float angle = this.getCelestialAngle(tickDiff);
        float light = 1.0F - (MathHelper.cos(angle * ((float) Math.PI * 2F)) * 2.0F + 0.5F);
        light = MathHelper.clamp(light, 0.0F, 1.0F);
        light = 1.0F - light;
        light = (float)((double)light * (1.0D - (double)(this.getRainStrength(tickDiff) * 5.0F) / 16.0D));
        light = (float)((double)light * (1.0D - (double)(this.getThunderStrength(tickDiff) * 5.0F) / 16.0D));
        light = 1.0F - light;
        return (int)(light * 11.0F);
    }

    public float getRainStrength(float tickDiff) {
        return isRaining() ? 1 : 0; // TODO: real implementation
    }

    public float getThunderStrength(float tickDiff) {
        return isThundering() ? 1 : 0; // TODO: real implementation
    }

    public float getCelestialAngle(float tickDiff) {
        return calculateCelestialAngle(getTime(), tickDiff);
    }

    public float calculateCelestialAngle(int time, float tickDiff) {
        int i = (int)(time % 24000L);
        float angle = ((float)i + tickDiff) / 24000.0F - 0.25F;

        if (angle < 0.0F) {
            ++angle;
        }

        if (angle > 1.0F) {
            --angle;
        }

        float f1 = 1.0F - (float)((Math.cos((double) angle * Math.PI) + 1.0D) / 2.0D);
        angle = angle + (f1 - angle) / 3.0F;
        return angle;
    }

    public int getMoonPhase(long worldTime) {
        return (int) (worldTime / 24000 % 8 + 8) % 8;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public int getFullBlock(int x, int y, int z) {
        return getFullBlock(x, y, z, 0);
    }

    @Deprecated
    @DeprecationDetails(reason ="The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public int getFullBlock(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4, false).getFullBlock(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int getBlockRuntimeId(int x, int y, int z) {
        return getBlockRuntimeId(x, y, z, 0);
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4, false).getBlockRuntimeId(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    public synchronized Block getBlock(Vector3 pos) {
        return getBlock(pos, 0);
    }

    public synchronized Block getBlock(Vector3 pos, int layer) {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer);
    }

    public synchronized Block getBlock(Vector3 pos, boolean load) {
        return getBlock(pos, 0, load);
    }

    public synchronized Block getBlock(Vector3 pos, int layer, boolean load) {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, load);
    }

    public synchronized Block getBlock(int x, int y, int z) {
        return getBlock(x, y, z, 0);
    }

    public synchronized Block getBlock(int x, int y, int z, int layer) {
        return getBlock(x, y, z, layer, true);
    }

    public synchronized Block getBlock(int x, int y, int z, boolean load) {
        return getBlock(x, y, z, 0, load);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will automatically repair broken block states")
    public synchronized Block getBlock(int x, int y, int z, int layer, boolean load) {
        BlockState fullState;
        if (y >= 0 && y < 256) {
            int cx = x >> 4;
            int cz = z >> 4;
            BaseFullChunk chunk;
            if (load) {
                chunk = getChunk(cx, cz);
            } else {
                chunk = getChunkIfLoaded(cx, cz);
            }
            if (chunk != null) {
                fullState = chunk.getBlockState(x & 0xF, y, z & 0xF, layer);
            } else {
                fullState = BlockState.AIR;
            }
        } else {
            fullState = BlockState.AIR;
        }
        OptionalBoolean valid = fullState.getCachedValidation();
        if (valid == OptionalBoolean.TRUE) {
            return fullState.getBlock(this, x, y, z, layer);
        }
        if (valid == OptionalBoolean.EMPTY) {
            try {
                return fullState.getBlock(this, x, y, z, layer);
            } catch (InvalidBlockStateException ignored) {
            }
        }
        Block block = fullState.getBlockRepairing(this, x, y, z, layer);
        setBlock(x, y, z, layer, block, false, false); // Update set to false to fix PowerNukkit#650 
        return block;
    }
    
    public void updateAllLight(Vector3 pos) {
        this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z);
        this.addLightUpdate((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateBlockSkyLight(int x, int y, int z) {
        BaseFullChunk chunk = getChunkIfLoaded(x >> 4, z >> 4);

        if (chunk == null) return;

        int oldHeightMap = chunk.getHeightMap(x & 0xf, z & 0xf);
        int sourceId = getBlockIdAt(x, y, z);

        int yPlusOne = y + 1;

        int newHeightMap;
        if (yPlusOne == oldHeightMap) { // Block changed directly beneath the heightmap. Check if a block was removed or changed to a different light-filter
            newHeightMap = chunk.recalculateHeightMapColumn(x & 0x0f, z & 0x0f);
        } else if (yPlusOne > oldHeightMap) { // Block changed above the heightmap
            if (Block.lightFilter[sourceId] > 1 || Block.diffusesSkyLight[sourceId]) {
                chunk.setHeightMap(x & 0xf, y & 0xf, yPlusOne);
                newHeightMap = yPlusOne;
            } else { // Block changed which has no effect on direct sky light, for example placing or removing glass.
                return;
            }
        } else { // Block changed below heightmap
            newHeightMap = oldHeightMap;
        }

        if (newHeightMap > oldHeightMap) { // Heightmap increase, block placed, remove sky light
            for (int i = y; i >= oldHeightMap; --i) {
                setBlockSkyLightAt(x, i, z, 0);
            }
        } else if (newHeightMap < oldHeightMap) { // Heightmap decrease, block changed or removed, add sky light
            for (int i = y; i >= newHeightMap; --i) {
                setBlockSkyLightAt(x, i, z, 15);
            }
        } else { // No heightmap change, block changed "underground"
            setBlockSkyLightAt(x, y, z, Math.max(0, getHighestAdjacentBlockSkyLight(x, y, z) - Block.lightFilter[sourceId]));
        }
    }

    /**
     * Returns the highest block skylight level available in the positions adjacent to the specified block coordinates.
     */
    public int getHighestAdjacentBlockSkyLight(int x, int y, int z) {
        int[] lightLevels = new int[] {
                getBlockSkyLightAt(x + 1, y, z),
                getBlockSkyLightAt(x - 1, y, z),
                getBlockSkyLightAt(x, y + 1, z),
                getBlockSkyLightAt(x, y - 1, z),
                getBlockSkyLightAt(x, y, z + 1),
                getBlockSkyLightAt(x, y, z - 1),
        };

        int maxValue = lightLevels[0];
        for(int i = 1; i < lightLevels.length; i++) {
            if (lightLevels[i] > maxValue) {
                maxValue = lightLevels[i];
            }
        }

        return maxValue;
    }

    public void updateBlockLight(Map<Long, Map<Character, Object>> map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        Queue<Long> lightPropagationQueue = new ConcurrentLinkedQueue<>();
        Queue<Object[]> lightRemovalQueue = new ConcurrentLinkedQueue<>();
        Long2ObjectOpenHashMap<Object> visited = new Long2ObjectOpenHashMap<>();
        Long2ObjectOpenHashMap<Object> removalVisited = new Long2ObjectOpenHashMap<>();

        Iterator<Map.Entry<Long, Map<Character, Object>>> iter = map.entrySet().iterator();
        while (iter.hasNext() && size-- > 0) {
            Map.Entry<Long, Map<Character, Object>> entry = iter.next();
            iter.remove();
            long index = entry.getKey();
            Map<Character, Object> blocks = entry.getValue();
            int chunkX = Level.getHashX(index);
            int chunkZ = Level.getHashZ(index);
            int bx = chunkX << 4;
            int bz = chunkZ << 4;
            for (char blockHash : blocks.keySet()) {
                int hi = (byte) (blockHash >>> 8);
                int lo = (byte) blockHash;
                int y = lo & 0xFF;
                int x = (hi & 0xF) + bx;
                int z = ((hi >> 4) & 0xF) + bz;
                BaseFullChunk chunk = getChunk(x >> 4, z >> 4, false);
                if (chunk != null) {
                    int lcx = x & 0xF;
                    int lcz = z & 0xF;
                    int oldLevel = chunk.getBlockLight(lcx, y, lcz);
                    int newLevel = chunk.getBlockState(lcx, y, lcz).getBlock(this, x, y, z, 0, true).getLightLevel();
                    if (oldLevel != newLevel) {
                        this.setBlockLightAt(x, y, z, newLevel);
                        if (newLevel < oldLevel) {
                            removalVisited.put(Hash.hashBlock(x, y, z), changeBlocksPresent);
                            lightRemovalQueue.add(new Object[]{Hash.hashBlock(x, y, z), oldLevel});
                        } else {
                            visited.put(Hash.hashBlock(x, y, z), changeBlocksPresent);
                            lightPropagationQueue.add(Hash.hashBlock(x, y, z));
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

            this.computeRemoveBlockLight(x - 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x + 1, y, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y - 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y + 1, z, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z - 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
            this.computeRemoveBlockLight(x, y, z + 1, lightLevel, lightRemovalQueue, lightPropagationQueue,
                    removalVisited, visited);
        }

        while (!lightPropagationQueue.isEmpty()) {
            long node = lightPropagationQueue.poll();

            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);

            int lightLevel = this.getBlockLightAt(x, y, z)
                    - Block.lightFilter[this.getBlockIdAt(x, y, z)];

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
                                         Queue<Long> spreadQueue, Map<Long, Object> visited, Map<Long, Object> spreadVisited) {
        int current = this.getBlockLightAt(x, y, z);
        long index = Hash.hashBlock(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);
            if (current > 1) {
                if (!visited.containsKey(index)) {
                    visited.put(index, changeBlocksPresent);
                    queue.add(new Object[]{Hash.hashBlock(x, y, z), current});
                }
            }
        } else if (current >= currentLight) {
            if (!spreadVisited.containsKey(index)) {
                spreadVisited.put(index, changeBlocksPresent);
                spreadQueue.add(Hash.hashBlock(x, y, z));
            }
        }
    }

    private void computeSpreadBlockLight(int x, int y, int z, int currentLight, Queue<Long> queue,
                                         Map<Long, Object> visited) {
        int current = this.getBlockLightAt(x, y, z);
        long index = Hash.hashBlock(x, y, z);

        if (current < currentLight - 1) {
            this.setBlockLightAt(x, y, z, currentLight);

            if (!visited.containsKey(index)) {
                visited.put(index, changeBlocksPresent);
                if (currentLight > 1) {
                    queue.add(Hash.hashBlock(x, y, z));
                }
            }
        }
    }

    private Map<Long, Map<Character, Object>> lightQueue = new ConcurrentHashMap<>(8, 0.9f, 1);

    public void addLightUpdate(int x, int y, int z) {
        long index = chunkHash(x >> 4, z >> 4);
        Map<Character, Object> currentMap = lightQueue.get(index);
        if (currentMap == null) {
            currentMap = new ConcurrentHashMap<>(8, 0.9f, 1);
            this.lightQueue.put(index, currentMap);
        }
        currentMap.put(Level.localBlockHash(x, y, z), changeBlocksPresent);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized void setBlockFullIdAt(int x, int y, int z, int fullId) {
        setBlockFullIdAt(x, y, z, 0, fullId);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized void setBlockFullIdAt(int x, int y, int z, int layer, int fullId) {
        setBlock(x, y, z, layer, Block.fullList[fullId], false, false);
    }

    public synchronized boolean setBlock(Vector3 pos, Block block) {
        return setBlock(pos, 0, block);
    }

    public synchronized boolean setBlock(Vector3 pos, int layer, Block block) {
        return this.setBlock(pos, layer, block, false);
    }

    public synchronized boolean setBlock(Vector3 pos, Block block, boolean direct) {
        return this.setBlock(pos, 0, block, direct);
    }

    public synchronized boolean setBlock(Vector3 pos, int layer, Block block, boolean direct) {
        return this.setBlock(pos, layer, block, direct, true);
    }

    public synchronized boolean setBlock(Vector3 pos, Block block, boolean direct, boolean update) {
        return setBlock(pos, 0, block, direct, update);
    }

    public synchronized boolean setBlock(Vector3 pos, int layer, Block block, boolean direct, boolean update) {
        return setBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), layer, block, direct, update);
    }

    public synchronized boolean setBlock(int x, int y, int z, Block block, boolean direct, boolean update) {
        return setBlock(x, y, z, 0, block, direct, update);
    }

    public synchronized boolean setBlock(int x, int y, int z, int layer, Block block, boolean direct, boolean update) {
        if (y < 0 || y >= 256 || layer < 0 || layer > this.requireProvider().getMaximumLayer()) {
            return false;
        }
        BlockState state = block.getCurrentState();
        BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        BlockState statePrevious = chunk.getAndSetBlockState(x & 0xF, y, z & 0xF, layer, state);
        if (state.equals(statePrevious)) {
            return false;
        }
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;
        block.layer = layer;
        
        Block blockPrevious = statePrevious.getBlockRepairing(this, x, y, z, layer);
        
        int cx = x >> 4;
        int cz = z >> 4;
        long index = Level.chunkHash(cx, cz);
        if (direct) {
            this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(Player.EMPTY_ARRAY), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY, block.layer);
            //this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block.getLevelBlockAtLayer(0)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);
            //this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
        } else {
            addBlockChange(index, x, y, z);
        }

        for (ChunkLoader loader : this.getChunkLoaders(cx, cz)) {
            loader.onBlockChanged(block);
        }
        if (update) {
            updateAllLight(block);

            /*if (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel()) {
                addLightUpdate(x, y, z);
            }*/
            BlockUpdateEvent ev = new BlockUpdateEvent(block);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                for (Entity entity : this.getNearbyEntities(new SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1))) {
                    entity.scheduleUpdate();
                }
                block = ev.getBlock();
                block.onUpdate(BLOCK_UPDATE_NORMAL);
                block.getLevelBlockAtLayer(layer == 0? 1 : 0).onUpdate(BLOCK_UPDATE_NORMAL);
                this.updateAround(x, y, z);

                if (block.hasComparatorInputOverride()) {
                    this.updateComparatorOutputLevel(block);
                }
            }
        }
        blockPrevious.afterRemoval(block, update);
        return true;
    }

    private void addBlockChange(int x, int y, int z) {
        long index = Level.chunkHash(x >> 4, z >> 4);
        addBlockChange(index, x, y, z);
    }

    private void addBlockChange(long index, int x, int y, int z) {
        synchronized (changedBlocks) {
            SoftReference<Map<Character, Object>> current = changedBlocks.computeIfAbsent(index, k -> new SoftReference<>(new HashMap<>()));
            Map<Character, Object> currentMap = current.get();
            if (currentMap != changeBlocksFullMap && currentMap != null) {
                if (currentMap.size() > MAX_BLOCK_CACHE) {
                    this.changedBlocks.put(index, new SoftReference<>(changeBlocksFullMap));
                } else {
                    currentMap.put(Level.localBlockHash(x, y, z), changeBlocksPresent);
                }
            }
        }
    }

    public void dropItem(Vector3 source, Item item) {
        this.dropItem(source, item, null);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion) {
        this.dropItem(source, item, motion, 10);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, int delay) {
        this.dropItem(source, item, motion, false, delay);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, boolean dropAround, int delay) {
        if (motion == null) {
            if (dropAround) {
                float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                float f1 = ThreadLocalRandom.current().nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
            } else {
                motion = new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                        new java.util.Random().nextDouble() * 0.2 - 0.1);
            }
        }

        if (item.getId() != 0 && item.getCount() > 0) {
            EntityItem itemEntity = (EntityItem) Entity.createEntity("Item",
                    this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
                    Entity.getDefaultNBT(source, motion, new Random().nextFloat() * 360, 0)
                            .putShort("Health", 5)
                            .putCompound("Item", NBTIO.putItemHelper(item))
                            .putShort("PickupDelay", delay));

            if (itemEntity != null) {
                itemEntity.spawnToAll();
            }
        }
    }

    @Since("1.4.0.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@Nonnull Vector3 source, @Nonnull Item item) {
        return this.dropAndGetItem(source, item, null);
    }

    @Since("1.4.0.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@Nonnull Vector3 source, @Nonnull Item item, @Nullable Vector3 motion) {
        return this.dropAndGetItem(source, item, motion, 10);
    }

    @Since("1.4.0.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@Nonnull Vector3 source, @Nonnull Item item, @Nullable Vector3 motion, int delay) {
        return this.dropAndGetItem(source, item, motion, false, delay);
    }

    @Since("1.4.0.0-PN")
    @Nullable
    public EntityItem dropAndGetItem(@Nonnull Vector3 source, @Nonnull Item item, @Nullable Vector3 motion, boolean dropAround, int delay) {
        EntityItem itemEntity = null;

        if (motion == null) {
            if (dropAround) {
                float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                float f1 = ThreadLocalRandom.current().nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
            } else {
                motion = new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                        new java.util.Random().nextDouble() * 0.2 - 0.1);
            }
        }

        CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");

        if (item.getId() != 0 && item.getCount() > 0) {
            itemEntity = (EntityItem) Entity.createEntity("Item",
                    this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
                    new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
                            .add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))

                            .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.x))
                                    .add(new DoubleTag("", motion.y)).add(new DoubleTag("", motion.z)))

                            .putList(new ListTag<FloatTag>("Rotation")
                                    .add(new FloatTag("", ThreadLocalRandom.current().nextFloat() * 360))
                                    .add(new FloatTag("", 0)))

                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

            if (itemEntity != null) {
                itemEntity.spawnToAll();
            }
        }

        return itemEntity;
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
        return useBreakOn(vector, face, item, player, createParticles, false);
    }
    
    public Item useBreakOn(Vector3 vector, BlockFace face, Item item, Player player, boolean createParticles, boolean setBlockDestroy) {
        if (vector instanceof Block) {
            return useBreakOn(vector, ((Block) vector).layer, face, item, player, createParticles, setBlockDestroy);
        } else {
            return useBreakOn(vector, 0, face, item, player, createParticles, setBlockDestroy);
        }
    }

    public Item useBreakOn(Vector3 vector, int layer, BlockFace face, Item item, Player player, boolean createParticles, boolean setBlockDestroy) {
        if (player != null && player.getGamemode() > 2) {
            return null;
        }
        
        Block target = this.getBlock(vector, layer);

        if (player != null && !target.isBlockChangeAllowed(player)) {
            return null;
        }
        
        Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }
        
        if (!target.isBreakable(vector, layer, face, item, player, setBlockDestroy)) {
            return null;
        }
        
        boolean mustDrop = target.mustDrop(vector, layer, face, item, player);
        boolean mustSilkTouch = target.mustSilkTouch(vector, layer, face, item, player);
        boolean isSilkTouch = mustSilkTouch || item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null;

        if (player != null) {
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanDestroy");
                boolean canBreak = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<? extends Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() == target.getId()) {
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

            double breakTime = target.calculateBreakTime(item, player);
            // this in
            // block
            // class

            if ((setBlockDestroy || player.isCreative()) && breakTime > 0.15) {
                breakTime = 0.15;
            }

            breakTime -= 0.15;

            Item[] eventDrops;
            if (!mustDrop && !setBlockDestroy && !player.isSurvival()) {
                eventDrops = Item.EMPTY_ARRAY;
            } else if (mustSilkTouch || isSilkTouch && target.canSilkTouch()) {
                eventDrops = new Item[]{target.toItem()};
            } else {
                eventDrops = target.getDrops(item);
            }

            if (!setBlockDestroy) {
                boolean fastBreak = Long.sum(player.lastBreak, (long)breakTime*1000) > Long.sum(System.currentTimeMillis(), (long)1000);
                BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                        fastBreak);

                if (player.isSurvival() && !target.isBreakable(item)) {
                    ev.setCancelled();
                } else if (!player.isOp() && isInSpawnRadius(target)) {
                    ev.setCancelled();
                } else if (!ev.getInstaBreak() && ev.isFastBreak()) {
                    ev.setCancelled();
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return null;
                }

                if (!ev.getInstaBreak() && ev.isFastBreak()) {
                    return null;
                }

                player.lastBreak = System.currentTimeMillis();

                drops = ev.getDrops();
                dropExp = ev.getDropExp();
            } else {
                drops = eventDrops;
            }
        } else if (!target.isBreakable(item)) {
            return null;
        } else if (isSilkTouch) {
            drops = new Item[]{target.toItem()};
        } else {
            drops = target.getDrops(item);
        }

        Block above = this.getBlock(new Vector3(target.x, target.y + 1, target.z), 0);
        if (above != null) {
            if (above.getId() == Item.FIRE) {
                this.setBlock(above, Block.get(BlockID.AIR), true);
            }
        }

        if (createParticles) {
            Map<Integer, Player> players = this.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);

            this.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());

            if (player != null && !setBlockDestroy) {
                players.remove(player.getLoaderId());
            }
        }

        // Close BlockEntity before we check onBreak
        if (layer == 0) {
            BlockEntity blockEntity = this.getBlockEntity(target);
            if (blockEntity != null) {
                blockEntity.onBreak(isSilkTouch);
                blockEntity.close();
        
                this.updateComparatorOutputLevel(target);
            }
        }

        target.onBreak(item);

        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            if (player != null) {
                addSound(player, Sound.RANDOM_BREAK);
            }
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }

        if (this.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {
            
            if (!isSilkTouch && (mustDrop || player != null && (player.isSurvival() || setBlockDestroy)) && dropExp > 0 && drops.length != 0) {
                this.dropExpOrb(vector.add(0.5, 0.5, 0.5), dropExp);
            }

            if (mustDrop || player == null || setBlockDestroy || player.isSurvival()) {
                for (Item drop : drops) {
                    if (drop.getCount() > 0) {
                        this.dropItem(vector.add(0.5, 0.5, 0.5), drop);
                    }
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
        dropExpOrbAndGetEntities(source, exp, motion, delay);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<EntityXPOrb> dropExpOrbAndGetEntities(Vector3 source, int exp) {
        return dropExpOrbAndGetEntities(source, exp, null);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<EntityXPOrb> dropExpOrbAndGetEntities(Vector3 source, int exp, Vector3 motion) {
        return dropExpOrbAndGetEntities(source, exp, motion, 10);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<EntityXPOrb> dropExpOrbAndGetEntities(Vector3 source, int exp, Vector3 motion, int delay) {
        Random rand = ThreadLocalRandom.current();
        List<Integer> drops = EntityXPOrb.splitIntoOrbSizes(exp);
        List<EntityXPOrb> entities = new ArrayList<>(drops.size());
        for (int split : drops) {
            CompoundTag nbt = Entity.getDefaultNBT(source, motion == null ? new Vector3(
                    (rand.nextDouble() * 0.2 - 0.1) * 2,
                    rand.nextDouble() * 0.4,
                    (rand.nextDouble() * 0.2 - 0.1) * 2) : motion,
                    rand.nextFloat() * 360f, 0);

            nbt.putShort("Value", split);
            nbt.putShort("PickupDelay", delay);

            EntityXPOrb entity = (EntityXPOrb) Entity.createEntity("XpOrb", this.getChunk(source.getChunkX(), source.getChunkZ()), nbt);
            if (entity != null) {
                entities.add(entity);
                entity.spawnToAll();
            }
        }
        return entities;
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz) {
        return this.useItemOn(vector, item, face, fx, fy, fz, null);
    }

    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        return this.useItemOn(vector, item, face, fx, fy, fz, player, true);
    }


    @PowerNukkitDifference(info = "PowerNukkit#403", since = "1.3.1.2-PN")
    @PowerNukkitDifference(info = "Fixed PowerNukkit#716, block stops placing when towering up", since = "1.4.0.0-PN")
    public Item useItemOn(Vector3 vector, Item item, BlockFace face, float fx, float fy, float fz, Player player, boolean playSound) {
        Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (item.getBlock() instanceof BlockScaffolding && face == BlockFace.UP && block.getId() == BlockID.SCAFFOLDING) {
            while (block instanceof BlockScaffolding) {
                block = block.up();
            }
        }

        if (block.y > 255 || block.y < 0) {
            return null;
        }

        if (block.y > 127 && this.getDimension() == DIMENSION_NETHER) {
            return null;
        }
        
        if (target.getId() == Item.AIR) {
            return null;
        }

        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
                    target.getId() == 0 ? Action.RIGHT_CLICK_AIR : Action.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > 2) {
                ev.setCancelled();
            }

            if(!player.isOp() && isInSpawnRadius(target)) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onTouch(player, ev.getAction());
                if ((!player.isSneaking() || player.getInventory().getItemInHand().isNull()) && target.canBeActivated() && target.onActivate(item, player)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                        addSound(player, Sound.RANDOM_BREAK);
                        item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                    }
                    return item;
                }

                if (item.canBeActivated() && item.onActivate(this, player, block, target, face, fx, fy, fz)) {
                    if (item.getCount() <= 0) {
                        item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                        return item;
                    }
                }
            } else {
                if((item instanceof ItemBucket) && ((ItemBucket)item).isWater()) {
                    player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(Block.AIR, 0, target.getLevelBlockAtLayer(1))}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                }
                return null;
            }

            if((item instanceof ItemBucket) && ((ItemBucket)item).isWater()) {
                player.getLevel().sendBlocks(new Player[] {player}, new Block[] {target.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            }
        } else if (target.canBeActivated() && target.onActivate(item, player)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
            }
            return item;
        }
        Block hand;
        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        if (!(block.canBeReplaced() || (hand instanceof BlockSlab && hand.getId() == block.getId()))) {
            return null;
        }

        if (target.canBeReplaced()) {
            block = target;
            hand.position(block);
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null) {
            Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
            int realCount = 0;
            for (Entity e : entities) {
                if (e instanceof EntityArrow 
                        || e instanceof EntityItem
                        || (e instanceof Player && ((Player) e).isSpectator())
                        || player == e
                ) {
                    continue;
                }
                ++realCount;
            }

            if (player != null) {
                Vector3 diff = player.getNextPosition().subtract(player.getPosition());
                //if (diff.lengthSquared() > 0.00001) {
                    AxisAlignedBB bb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
                    if (hand.getBoundingBox().intersectsWith(bb)) {
                        ++realCount;
                    }
                //}
            }

            if (realCount > 0) {
                return null; // Entity in block
            }
        }

        if (player != null) {
            if (!block.isBlockChangeAllowed(player)) {
                return null;
            }
            
            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanPlaceOn");
                boolean canPlace = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() == target.getId()) {
                                canPlace = true;
                                break;
                            }
                        }
                    }
                }
                if (!canPlace) {
                    event.setCancelled();
                }
            }
            if(!player.isOp() && isInSpawnRadius(target)) {
                event.setCancelled();
            }

            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        if(hand.getWaterloggingLevel() == 0 && hand.canBeFlowedInto() && (block instanceof BlockLiquid || block.getLevelBlockAtLayer(1) instanceof BlockLiquid)) {
            return null;
        }

        boolean liquidMoved = false;
        if ((block instanceof BlockLiquid) && ((BlockLiquid) block).usesWaterLogging()) {
            liquidMoved = true;
            this.setBlock(block, 1, block, false, false);
            this.setBlock(block, 0, Block.get(BlockID.AIR), false, false);
            this.scheduleUpdate(block, 1);
        }

        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
            if (liquidMoved) {
                this.setBlock(block, 0, block, false, false);
                this.setBlock(block, 1, Block.get(BlockID.AIR), false, false);
            }
            return null;
        }

        if (player != null) {
            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
        }

        if (playSound) {
            this.addLevelSoundEvent(hand, LevelSoundEventPacket.SOUND_PLACE, GlobalBlockPalette.getOrCreateRuntimeId(hand.getId(), hand.getDamage()));
        }

        if (item.getCount() <= 0) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }
        return item;
    }

    public boolean isInSpawnRadius(Vector3 vector3) {
        int distance = this.server.getSpawnRadius();
        if (distance > -1) {
            Vector2 t = new Vector2(vector3.x, vector3.z);
            Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
            return t.distance(s) <= distance;
        }
        return false;
    }

    public Entity getEntity(long entityId) {
        return this.entities.containsKey(entityId) ? this.entities.get(entityId) : null;
    }

    public Entity[] getEntities() {
        return entities.values().toArray(Entity.EMPTY_ARRAY);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb, Entity entity) {
        int index = 0;

        ArrayList<Entity> overflow = null;

        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (Entity ent : this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || (ent != entity && entity.canCollideWith(ent)))
                                && ent.boundingBox.intersectsWith(bb)) {
                            overflow = addEntityToBuffer(index, overflow, ent);
                            index++;
                        }
                    }
                }
            }
        }

        return getEntitiesFromBuffer(index, overflow);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null);
    }

    private static final Entity[] ENTITY_BUFFER = new Entity[512];

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity) {
        return getNearbyEntities(bb, entity, false);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity, boolean loadChunks) {
        int index = 0;

        int minX = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625);
        int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625);
        int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625);
        int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625);

        ArrayList<Entity> overflow = null;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (Entity ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        overflow = addEntityToBuffer(index, overflow, ent);
                        index++;
                    }
                }
            }
        }

        return getEntitiesFromBuffer(index, overflow);
    }

    private ArrayList<Entity> addEntityToBuffer(int index, ArrayList<Entity> overflow, Entity ent) {
        if (index < ENTITY_BUFFER.length) {
            ENTITY_BUFFER[index] = ent;
        } else {
            if (overflow == null) overflow = new ArrayList<>(1024);
            overflow.add(ent);
        }
        return overflow;
    }

    private Entity[] getEntitiesFromBuffer(int index, ArrayList<Entity> overflow) {
        if (index == 0) return Entity.EMPTY_ARRAY;
        Entity[] copy;
        if (overflow == null) {
            copy = Arrays.copyOfRange(ENTITY_BUFFER, 0, index);
            Arrays.fill(ENTITY_BUFFER, 0, index, null);
        } else {
            copy = new Entity[ENTITY_BUFFER.length + overflow.size()];
            System.arraycopy(ENTITY_BUFFER, 0, copy, 0, ENTITY_BUFFER.length);
            for (int i = 0; i < overflow.size(); i++) {
                copy[ENTITY_BUFFER.length + i] = overflow.get(i);
            }
        }
        return copy;
    }

    public Map<Long, BlockEntity> getBlockEntities() {
        return blockEntities;
    }

    public BlockEntity getBlockEntityById(long blockEntityId) {
        return this.blockEntities.containsKey(blockEntityId) ? this.blockEntities.get(blockEntityId) : null;
    }

    public Map<Long, Player> getPlayers() {
        return players;
    }

    public Map<Integer, ChunkLoader> getLoaders() {
        return loaders;
    }

    public BlockEntity getBlockEntity(Vector3 pos) {
        return getBlockEntity(pos.asBlockVector3());
    }

    public BlockEntity getBlockEntity(BlockVector3 pos) {
        FullChunk chunk = this.getChunk(pos.x >> 4, pos.z >> 4, false);

        if (chunk != null) {
            return chunk.getTile(pos.x & 0x0f, pos.y & 0xff, pos.z & 0x0f);
        }

        return null;
    }

    public BlockEntity getBlockEntityIfLoaded(Vector3 pos) {
        FullChunk chunk = this.getChunkIfLoaded((int) pos.x >> 4, (int) pos.z >> 4);

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
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
    public BlockState getBlockStateAt(int x, int y, int z, int layer) {
        return getChunk(x >> 4, z >> 4, true).getBlockStateAt(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        return getBlockIdAt(x, y, z, 0);
    }

    @Override
    public synchronized int getBlockIdAt(int x, int y, int z,  int layer) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockId(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        setBlockIdAt(x, y, z, 0, id);
    }

    @Override
    public synchronized void setBlockIdAt(int x, int y, int z, int layer, int id) {
        this.getChunk(x >> 4, z >> 4, true).setBlockId(x & 0x0f, y & 0xff, z & 0x0f, layer, id & 0xfff);
        addBlockChange(x, y, z);
        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized void setBlockAt(int x, int y, int z, int id, int data) {
        setBlockAtLayer(x, y, z, 0, id, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized boolean setBlockAtLayer(int x, int y, int z, int layer, int id, int data) {
        return setBlockStateAt(x, y, z, layer, BlockState.of(id, data));
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public synchronized boolean setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        boolean changed = chunk.setBlockStateAtLayer(x & 0x0f, y & 0xff, z & 0x0f, layer, state);
        addBlockChange(x, y, z);
        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
        return changed;
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public int getBlockDataAt(int x, int y, int z) {
        return getBlockDataAt(x, y, z, 0);
    }

    public synchronized int getBlockExtraDataAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockExtraData(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized void setBlockExtraDataAt(int x, int y, int z, int id, int data) {
        this.getChunk(x >> 4, z >> 4, true).setBlockExtraData(x & 0x0f, y & 0xff, z & 0x0f, (data << 8) | id);

        this.sendBlockExtraData(x, y, z, id, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized int getBlockDataAt(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockData(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        setBlockDataAt(x, y, z, 0, data);
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    public synchronized void setBlockDataAt(int x, int y, int z, int layer, int data) {
        this.getChunk(x >> 4, z >> 4, true).setBlockData(x & 0x0f, y & 0xff, z & 0x0f, layer, data);
        addBlockChange(x, y, z);
        temporalVector.setComponents(x, y, z);
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(temporalVector);
        }
    }

    public synchronized int getBlockSkyLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockSkyLight(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized void setBlockSkyLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockSkyLight(x & 0x0f, y & 0xff, z & 0x0f, level & 0x0f);
    }

    public synchronized int getBlockLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockLight(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized void setBlockLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockLight(x & 0x0f, y & 0xff, z & 0x0f, level & 0x0f);
    }

    public int getBiomeId(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 0x0f, z & 0x0f);
    }

    public void setBiomeId(int x, int z, byte biomeId) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, z & 0x0f, biomeId);
    }

    public int getHeightMap(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHeightMap(x & 0x0f, z & 0x0f);
    }

    public void setHeightMap(int x, int z, int value) {
        this.getChunk(x >> 4, z >> 4, true).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
    }

    public Map<Long, ? extends FullChunk> getChunks() {
        return requireProvider().getLoadedChunks();
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        BaseFullChunk chunk = this.requireProvider().getLoadedChunk(index);
        if (chunk == null) {
            chunk = this.forceLoadChunk(index, chunkX, chunkZ, create);
        }
        return chunk;
    }

    public BaseFullChunk getChunkIfLoaded(int chunkX, int chunkZ) {
        long index = Level.chunkHash(chunkX, chunkZ);
        return this.requireProvider().getLoadedChunk(index);
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk) {
        generateChunkCallback(x, z, chunk, true);
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk, boolean isPopulated) {
        Timings.generationCallbackTimer.startTiming();
        long index = Level.chunkHash(x, z);
        LevelProvider levelProvider = this.requireProvider();
        if (this.chunkPopulationQueue.containsKey(index)) {
            FullChunk oldChunk = this.getChunk(x, z, false);
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    this.chunkPopulationLock.remove(Level.chunkHash(x + xx, z + zz));
                }
            }
            this.chunkPopulationQueue.remove(index);
            chunk.setProvider(levelProvider);
            this.setChunk(x, z, chunk, false);
            chunk = this.getChunk(x, z, false);
            if (chunk != null && (oldChunk == null || !isPopulated) && chunk.isPopulated()
                    && chunk.getProvider() != null) {
                this.server.getPluginManager().callEvent(new ChunkPopulateEvent(chunk));

                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkPopulated(chunk);
                }
            }
        } else if (this.chunkGenerationQueue.containsKey(index) || this.chunkPopulationLock.containsKey(index)) {
            this.chunkGenerationQueue.remove(index);
            this.chunkPopulationLock.remove(index);
            chunk.setProvider(levelProvider);
            this.setChunk(x, z, chunk, false);
        } else {
            chunk.setProvider(levelProvider);
            this.setChunk(x, z, chunk, false);
        }
        Timings.generationCallbackTimer.stopTiming();
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

                this.requireProvider().setChunk(chunkX, chunkZ, chunk);
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

                this.requireProvider().setChunk(chunkX, chunkZ, chunk);
            }
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
        return this.getChunk(x >> 4, z >> 4, true).getHighestBlockAt(x & 0x0f, z & 0x0f);
    }

    public BlockColor getMapColorAt(int x, int z) {
        int y = getHighestBlockAt(x, z);
        while (y > 1) {
            Block block = getBlock(new Vector3(x, y, z));
            BlockColor blockColor = block.getColor();
            if (blockColor.getAlpha() == 0x00) {
                y--;
            } else {
                return blockColor;
            }
        }
        return BlockColor.VOID_BLOCK_COLOR;
    }

    public boolean isChunkLoaded(int x, int z) {
        return this.requireProvider().isChunkLoaded(x, z);
    }

    private boolean areNeighboringChunksLoaded(long hash) {
        LevelProvider levelProvider = this.requireProvider();
        return levelProvider.isChunkLoaded(hash + 1) &&
                levelProvider.isChunkLoaded(hash - 1) &&
                levelProvider.isChunkLoaded(hash + (1L << 32)) &&
                levelProvider.isChunkLoaded(hash - (1L << 32));
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
        return Position.fromObject(this.requireProvider().getSpawn(), this);
    }

    public Position getFuzzySpawnLocation() {
        Position spawn = getSpawnLocation();
        int radius = gameRules.getInteger(GameRule.SPAWN_RADIUS);
        if (radius > 0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int negativeFlags = random.nextInt(4);
            spawn = spawn.add(
                    radius * random.nextDouble() * ((negativeFlags & 1) > 0? -1 : 1),
                    0,
                    radius * random.nextDouble() * ((negativeFlags & 2) > 0? -1 : 1)
            );
        }
        return spawn;
    }

    public void setSpawnLocation(Vector3 pos) {
        Position previousSpawn = this.getSpawnLocation();
        this.requireProvider().setSpawn(pos);
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        pk.dimension = getDimension();
        for (Player p : getPlayers().values()) {
            p.dataPacket(pk);
        }
    }

    public void requestChunk(int x, int z, Player player) {
        Preconditions.checkState(player.getLoaderId() > 0, player.getName() + " has no chunk loader");
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
        this.timings.syncChunkSendTimer.startTiming();
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
            this.timings.syncChunkSendPrepareTimer.startTiming();
            AsyncTask task = this.requireProvider().requestChunkTask(x, z);
            if (task != null) {
                this.server.getScheduler().scheduleAsyncTask(task);
            }
            this.timings.syncChunkSendPrepareTimer.stopTiming();
        }
        this.timings.syncChunkSendTimer.stopTiming();
    }

    public void chunkRequestCallback(long timestamp, int x, int z, int subChunkCount, byte[] payload) {
        this.timings.syncChunkSendTimer.startTiming();
        long index = Level.chunkHash(x, z);

        if (this.cacheChunks) {
            BatchPacket data = Player.getChunkCacheFromData(x, z, subChunkCount, payload);
            BaseFullChunk chunk = getChunk(x, z, false);
            if (chunk != null && chunk.getChanges() <= timestamp) {
                chunk.setChunkPacket(data);
            }
            this.sendChunk(x, z, index, data);
            this.timings.syncChunkSendTimer.stopTiming();
            return;
        }

        if (this.chunkSendTasks.contains(index)) {
            for (Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, subChunkCount, payload);
                }
            }

            this.chunkSendQueue.remove(index);
            this.chunkSendTasks.remove(index);
        }
        this.timings.syncChunkSendTimer.stopTiming();
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
            throw new LevelException("Invalid Block Entity level");
        }
        blockEntities.put(blockEntity.getId(), blockEntity);
    }

    public void scheduleBlockEntityUpdate(BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        if (!updateBlockEntities.contains(entity)) {
            updateBlockEntities.add(entity);
        }
    }

    public void removeBlockEntity(BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        blockEntities.remove(entity.getId());
        updateBlockEntities.remove(entity);
    }

    public boolean isChunkInUse(int x, int z) {
        return isChunkInUse(Level.chunkHash(x, z));
    }

    public boolean isChunkInUse(long hash) {
        return this.chunkLoaders.containsKey(hash) && !this.chunkLoaders.get(hash).isEmpty();
    }

    public boolean loadChunk(int x, int z) {
        return this.loadChunk(x, z, true);
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        long index = Level.chunkHash(x, z);
        if (this.requireProvider().isChunkLoaded(index)) {
            return true;
        }
        return forceLoadChunk(index, x, z, generate) != null;
    }

    private synchronized BaseFullChunk forceLoadChunk(long index, int x, int z, boolean generate) {
        this.timings.syncChunkLoadTimer.startTiming();
        BaseFullChunk chunk = this.requireProvider().getChunk(x, z, generate);
        if (chunk == null) {
            if (generate) {
                throw new IllegalStateException("Could not create new Chunk");
            }
            this.timings.syncChunkLoadTimer.stopTiming();
            return chunk;
        }

        if (chunk.getProvider() != null) {
            this.server.getPluginManager().callEvent(new ChunkLoadEvent(chunk, !chunk.isGenerated()));
        } else {
            this.unloadChunk(x, z, false);
            this.timings.syncChunkLoadTimer.stopTiming();
            return chunk;
        }
        
        chunk.backwardCompatibilityUpdate(this);
        chunk.initChunk();

        if (!chunk.isLightPopulated() && chunk.isPopulated()
                && this.getServer().getConfig("chunk-ticking.light-updates", false)) {
            this.getServer().getScheduler().scheduleAsyncTask(new LightPopulationTask(this, chunk));
        }

        if (this.isChunkInUse(index)) {
            this.unloadQueue.remove(index);
            for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                loader.onChunkLoaded(chunk);
            }
        } else {
            this.unloadQueue.put(index, System.currentTimeMillis());
        }
        this.timings.syncChunkLoadTimer.stopTiming();
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

        this.timings.doChunkUnload.startTiming();

        BaseFullChunk chunk = this.getChunk(x, z);

        if (chunk != null && chunk.getProvider() != null) {
            ChunkUnloadEvent ev = new ChunkUnloadEvent(chunk);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.timings.doChunkUnload.stopTiming();
                return false;
            }
        }

        try {
            LevelProvider levelProvider = this.requireProvider();
            if (chunk != null) {
                if (trySave && this.getAutoSave()) {
                    int entities = 0;
                    for (Entity e : chunk.getEntities().values()) {
                        if (e instanceof Player) {
                            continue;
                        }
                        ++entities;
                    }

                    if (chunk.hasChanged() || !chunk.getBlockEntities().isEmpty() || entities > 0) {
                        levelProvider.setChunk(x, z, chunk);
                        levelProvider.saveChunk(x, z);
                    }
                }
                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkUnloaded(chunk);
                }
            }
            levelProvider.unloadChunk(x, z, safe);
        } catch (Exception e) {
            log.error(this.server.getLanguage().translateString("nukkit.level.chunkUnloadError", e.toString()), e);
        }

        this.timings.doChunkUnload.stopTiming();

        return true;
    }

    public boolean isSpawnChunk(int X, int Z) {
        Vector3 spawn = this.requireProvider().getSpawn();
        return Math.abs(X - (spawn.getFloorX() >> 4)) <= 1 && Math.abs(Z - (spawn.getFloorZ() >> 4)) <= 1;
    }

    public Position getSafeSpawn() {
        return this.getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3 spawn) {
        if (spawn == null || spawn.y < 1) {
            spawn = this.getFuzzySpawnLocation();
        }

        if (spawn != null) {
            Vector3 v = spawn.floor();
            FullChunk chunk = this.getChunk((int) v.x >> 4, (int) v.z >> 4, false);
            int x = (int) v.x & 0x0f;
            int z = (int) v.z & 0x0f;
            if (chunk != null && chunk.isGenerated()) {
                int y = (int) NukkitMath.clamp(v.y, 1, 254);
                boolean wasAir = chunk.getBlockId(x, y - 1, z) == 0;
                for (; y > 0; --y) {
                    BlockState state = chunk.getBlockState(x, y, z);
                    Block block = state.getBlockRepairing(this, x, y, z);
                    if (this.isFullBlock(block)) {
                        if (wasAir) {
                            y++;
                            break;
                        }
                    } else {
                        wasAir = true;
                    }
                }

                for (; y >= 0 && y < 255; y++) {
                    BlockState state = chunk.getBlockState(x, y + 1, z);
                    Block block = state.getBlockRepairing(this, x, y + 1, z);
                    if (!this.isFullBlock(block)) {
                        state = chunk.getBlockState(x, y, z);
                        block = state.getBlockRepairing(this, x, y, z);
                        if (!this.isFullBlock(block)) {
                            return new Position(spawn.x, y == (int) spawn.y ? spawn.y : y, spawn.z, this);
                        }
                    } else {
                        ++y;
                    }
                }

                v.y = y;
            }

            return new Position(spawn.x, v.y, spawn.z, this);
        }

        return null;
    }

    public int getTime() {
        return (int) time;
    }

    public boolean isDaytime() {
        return this.skyLightSubtracted < 4;
    }

    public long getCurrentTick() {
        return this.levelCurrentTick;
    }

    public String getName() {
        return this.requireProvider().getName();
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
        return this.requireProvider().getSeed();
    }

    public void setSeed(int seed) {
        this.requireProvider().setSeed(seed);
    }

    public boolean populateChunk(int x, int z) {
        return this.populateChunk(x, z, false);
    }

    public boolean populateChunk(int x, int z, boolean force) {
        long index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.containsKey(index) || this.chunkPopulationQueue.size() >= this.chunkPopulationQueueSize && !force) {
            return false;
        }

        BaseFullChunk chunk = this.getChunk(x, z, true);
        boolean populate;
        if (!chunk.isPopulated()) {
            Timings.populationTimer.startTiming();
            populate = true;
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (this.chunkPopulationLock.containsKey(Level.chunkHash(x + xx, z + zz))) {

                        populate = false;
                        break;
                    }
                }
            }

            if (populate) {
                if (!this.chunkPopulationQueue.containsKey(index)) {
                    this.chunkPopulationQueue.put(index, Boolean.TRUE);
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            this.chunkPopulationLock.put(Level.chunkHash(x + xx, z + zz), Boolean.TRUE);
                        }
                    }

                    PopulationTask task = new PopulationTask(this, chunk);
                    this.server.getScheduler().scheduleAsyncTask(task);
                }
            }
            Timings.populationTimer.stopTiming();
            return false;
        }

        return true;
    }

    public void generateChunk(int x, int z) {
        this.generateChunk(x, z, false);
    }

    public void generateChunk(int x, int z, boolean force) {
        if (this.chunkGenerationQueue.size() >= this.chunkGenerationQueueSize && !force) {
            return;
        }

        long index = Level.chunkHash(x, z);
        if (!this.chunkGenerationQueue.containsKey(index)) {
            Timings.generationTimer.startTiming();
            this.chunkGenerationQueue.put(index, Boolean.TRUE);
            GenerationTask task = new GenerationTask(this, this.getChunk(x, z, true));
            this.server.getScheduler().scheduleAsyncTask(task);
            Timings.generationTimer.stopTiming();
        }
    }

    public void regenerateChunk(int x, int z) {
        this.unloadChunk(x, z, false);

        this.cancelUnloadChunkRequest(x, z);

        LevelProvider levelProvider = requireProvider();
        BaseFullChunk chunk = levelProvider.getEmptyChunk(x, z);
        levelProvider.setChunk(x, z, chunk);

        this.generateChunk(x, z);
    }

    public void doChunkGarbageCollection() {
        this.timings.doChunkGC.startTiming();
        // remove all invaild block entities.
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

        for (Map.Entry<Long, ? extends FullChunk> entry : requireProvider().getLoadedChunks().entrySet()) {
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

        this.requireProvider().doGarbageCollection();
        this.timings.doChunkGC.stopTiming();
    }

    public void doGarbageCollection(long allocatedTime) {
        long start = System.currentTimeMillis();
        if (unloadChunks(start, allocatedTime, false)) {
            allocatedTime = allocatedTime - (System.currentTimeMillis() - start);
            requireProvider().doGarbageCollection(allocatedTime);
        }
    }

    public void unloadChunks() {
        this.unloadChunks(false);
    }

    public void unloadChunks(boolean force) {
        unloadChunks(96, force);
    }

    public void unloadChunks(int maxUnload, boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            long now = System.currentTimeMillis();

            LongList toRemove = null;
            for (Long2LongMap.Entry entry : unloadQueue.long2LongEntrySet()) {
                long index = entry.getLongKey();

                if (isChunkInUse(index)) {
                    continue;
                }

                if (!force) {
                    long time = entry.getLongValue();
                    if (maxUnload <= 0) {
                        break;
                    } else if (time > (now - 30000)) {
                        continue;
                    }
                }

                if (toRemove == null) toRemove = new LongArrayList();
                toRemove.add(index);
            }

            if (toRemove != null) {
                int size = toRemove.size();
                for (int i = 0; i < size; i++) {
                    long index = toRemove.getLong(i);
                    int X = getHashX(index);
                    int Z = getHashZ(index);

                    if (this.unloadChunk(X, Z, true)) {
                        this.unloadQueue.remove(index);
                        --maxUnload;
                    }
                }
            }
        }
    }

    private int lastUnloadIndex;

    /**
     * @param now
     * @param allocatedTime
     * @param force
     * @return true if there is allocated time remaining
     */
    private boolean unloadChunks(long now, long allocatedTime, boolean force) {
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

                long index = entry.getLongKey();

                if (isChunkInUse(index)) {
                    continue;
                }

                if (!force) {
                    long time = entry.getLongValue();
                    if (time > (now - 30000)) {
                        continue;
                    }
                }

                if (toUnload == null) toUnload = new LongArrayList();
                toUnload.add(index);
            }

            if (toUnload != null) {
                long[] arr = toUnload.toLongArray();
                for (long index : arr) {
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

        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public void addEntityMovement(Entity entity, double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = entity.getId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        pk.onGround = entity.onGround;

        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public boolean isRaining() {
        return this.raining;
    }

    public boolean setRaining(boolean raining) {
        WeatherChangeEvent ev = new WeatherChangeEvent(this, raining);
        this.getServer().getPluginManager().callEvent(ev);

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
        return isRaining() && this.thundering;
    }

    public boolean setThundering(boolean thundering) {
        ThunderChangeEvent ev = new ThunderChangeEvent(this, thundering);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        if (thundering && !isRaining()) {
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
            players = this.getPlayers().values().toArray(Player.EMPTY_ARRAY);
        }

        LevelEventPacket pk = new LevelEventPacket();

        if (this.isRaining()) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = this.rainTime;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
        }

        Server.broadcastPacket(players, pk);

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
        this.sendWeather(players.toArray(Player.EMPTY_ARRAY));
    }

    public int getDimension() {
        return dimension;
    }

    public boolean canBlockSeeSky(Vector3 pos) {
        return this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY();
    }

    public int getStrongPower(Vector3 pos, BlockFace direction) {
        return this.getBlock(pos).getStrongPower(direction);
    }

    @PowerNukkitDifference(info = "Check if the block to check is a piston, then return 0.", since = "1.4.0.0-PN")
    public int getStrongPower(Vector3 pos) {
        if (pos instanceof BlockPistonBase || this.getBlock(pos) instanceof BlockPistonBase) return 0;

        int i = 0;
        for (BlockFace face : BlockFace.values()) {
            i = Math.max(i, this.getStrongPower(temporalVector.setComponentsAdding(pos, face), face));

            if (i >= 15) {
                return i;
            }
        }

        return i;
//        i = Math.max(i, this.getStrongPower(pos.down(), BlockFace.DOWN));
//
//        if (i >= 15) {
//            return i;
//        } else {
//            i = Math.max(i, this.getStrongPower(pos.up(), BlockFace.UP));
//
//            if (i >= 15) {
//                return i;
//            } else {
//                i = Math.max(i, this.getStrongPower(pos.north(), BlockFace.NORTH));
//
//                if (i >= 15) {
//                    return i;
//                } else {
//                    i = Math.max(i, this.getStrongPower(pos.south(), BlockFace.SOUTH));
//
//                    if (i >= 15) {
//                        return i;
//                    } else {
//                        i = Math.max(i, this.getStrongPower(pos.west(), BlockFace.WEST));
//
//                        if (i >= 15) {
//                            return i;
//                        } else {
//                            i = Math.max(i, this.getStrongPower(pos.east(), BlockFace.EAST));
//                            return i >= 15 ? i : i;
//                        }
//                    }
//                }
//            }
//        }
    }

    public boolean isSidePowered(Vector3 pos, BlockFace face) {
        return this.getRedstonePower(pos, face) > 0;
    }

    public int getRedstonePower(Vector3 pos, BlockFace face) {
        Block block;

        if (pos instanceof Block) {
            block = (Block) pos;
            pos = pos.add(0);
        } else {
            block = this.getBlock(pos);
        }

        return block.isNormalBlock() ? this.getStrongPower(pos) : block.getWeakPower(face);
    }

    public boolean isBlockPowered(Vector3 pos) {
        for (BlockFace face : BlockFace.values()) {
            if (this.getRedstonePower(temporalVector.setComponentsAdding(pos, face), face) > 0) {
                return true;
            }
        }

        return false;
    }

    public int isBlockIndirectlyGettingPowered(Vector3 pos) {
        int power = 0;

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getRedstonePower(temporalVector.setComponentsAdding(pos, face), face);

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
        if (bb.getMaxY() < 0 || bb.getMinY() >= 256) {
            return false;
        }
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

    public int getUpdateLCG() {
        return (this.updateLCG = (this.updateLCG * 3) ^ LCG_CONSTANT);
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    public boolean createPortal(Block target) {
        int maxPortalSize = 23;
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
            int scanZ = targZ;
            for (int i = 0; i < sizePosX + 1; i++) {
                //this must be air
                if (this.getBlockIdAt(scanX + i, scanY, scanZ) != BlockID.AIR) {
                    return false;
                }
                if (this.getBlockIdAt(scanX + i + 1, scanY, scanZ) == BlockID.OBSIDIAN) {
                    scanX += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(scanX + 1, scanY, scanZ) != BlockID.OBSIDIAN) {
                return false;
            }

            int innerWidth = 0;
            LOOP: for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(scanX - i, scanY, scanZ);
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
            LOOP: for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(scanX, scanY + i, scanZ);
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
            if (!(innerWidth <= maxPortalSize - 2
                    && innerWidth >= 2
                    && innerHeight <= maxPortalSize - 2
                    && innerHeight >= 3))   {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++)    {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(scanX - width, scanY + height, scanZ) != BlockID.OBSIDIAN) {
                            return false;
                        }
                    }
                } else {
                    if (this.getBlockIdAt(scanX + 1, scanY + height, scanZ) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(scanX - innerWidth, scanY + height, scanZ) != BlockID.OBSIDIAN) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(scanX - width, scanY + height, scanZ) != BlockID.AIR) {
                            return false;
                        }
                    }
                }
            }

            for (int height = 0; height < innerHeight; height++)    {
                for (int width = 0; width < innerWidth; width++)    {
                    this.setBlock(new Vector3(scanX - width, scanY + height, scanZ), Block.get(BlockID.NETHER_PORTAL));
                }
            }

            this.addSound(target, Sound.FIRE_IGNITE);
            return true;
        } else if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            int scanY = targY + 1;
            int scanZ = targZ;
            for (int i = 0; i < sizePosZ + 1; i++) {
                //this must be air
                if (this.getBlockIdAt(scanX, scanY, scanZ + i) != BlockID.AIR) {
                    return false;
                }
                if (this.getBlockIdAt(scanX, scanY, scanZ + i + 1) == BlockID.OBSIDIAN) {
                    scanZ += i;
                    break;
                }
            }
            //make sure that the above loop finished
            if (this.getBlockIdAt(scanX, scanY, scanZ + 1) != BlockID.OBSIDIAN) {
                return false;
            }

            int innerWidth = 0;
            LOOP: for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(scanX, scanY, scanZ - i);
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
            LOOP: for (int i = 0; i < maxPortalSize - 2; i++) {
                int id = this.getBlockIdAt(scanX, scanY + i, scanZ);
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
            if (!(innerWidth <= maxPortalSize - 2
                    && innerWidth >= 2
                    && innerHeight <= maxPortalSize - 2
                    && innerHeight >= 3))   {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++)    {
                if (height == innerHeight) {
                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(scanX, scanY + height, scanZ - width) != BlockID.OBSIDIAN) {
                            return false;
                        }
                    }
                } else {
                    if (this.getBlockIdAt(scanX, scanY + height, scanZ + 1) != BlockID.OBSIDIAN
                            || this.getBlockIdAt(scanX, scanY + height, scanZ - innerWidth) != BlockID.OBSIDIAN) {
                        return false;
                    }

                    for (int width = 0; width < innerWidth; width++) {
                        if (this.getBlockIdAt(scanX, scanY + height, scanZ - width) != BlockID.AIR) {
                            return false;
                        }
                    }
                }
            }

            for (int height = 0; height < innerHeight; height++)    {
                for (int width = 0; width < innerWidth; width++)    {
                    this.setBlock(new Vector3(scanX, scanY + height, scanZ - width), Block.get(BlockID.NETHER_PORTAL));
                }
            }

            this.addSound(target, Sound.FIRE_IGNITE);
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Level{" +
                "folderName='" + folderName + '\'' +
                ", dimension=" + dimension +
                '}';
    }

    @AllArgsConstructor
    @Data
    private static class QueuedUpdate {
        @Nonnull
        private Block block;
        private BlockFace neighbor;
    }
    
//    private static void orderGetRidings(Entity entity, LongSet set) {
//        if (entity.riding != null) {
//            if(!set.add(entity.riding.getId())) {
//                throw new RuntimeException("Circular entity link detected (id = "+entity.riding.getId()+")");
//            }
//            orderGetRidings(entity.riding, set);
//        }
//    }
//
//    public List<Entity> orderChunkEntitiesForSpawn(int chunkX, int chunkZ) {
//        return orderChunkEntitiesForSpawn(getChunk(chunkX, chunkZ, false));
//    }
//
//    public List<Entity> orderChunkEntitiesForSpawn(BaseFullChunk chunk) {
//        Comparator<Entity> comparator = (o1, o2) -> {
//            if (o1.riding == null) {
//                if(o2 == null) {
//                    return 0;
//                }
//
//                return -1;
//            }
//
//            if (o2.riding == null) {
//                return 1;
//            }
//
//            LongSet ridings = new LongOpenHashSet();
//            orderGetRidings(o1, ridings);
//
//            if(ridings.contains(o2.getId())) {
//                return 1;
//            }
//
//            ridings.clear();
//            orderGetRidings(o2, ridings);
//
//            if(ridings.contains(o1.getId())) {
//                return -1;
//            }
//
//            return 0;
//        };
//
//        List<Entity> sorted = new ArrayList<>(chunk.getEntities().values());
//        sorted.sort(comparator);
//
//        return sorted;
//    }
}
