package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockRedstoneDiode;
import cn.nukkit.blockentity.BlockEntity;
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
import cn.nukkit.event.weather.LightningStrikeEvent;
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
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.format.wool.WoolFormat;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.PopChunkManager;
import cn.nukkit.level.generator.task.GenerationTask;
import cn.nukkit.level.generator.task.LightPopulationTask;
import cn.nukkit.level.generator.task.PopulationTask;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.*;
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
import cn.nukkit.timings.LevelTimings;
import cn.nukkit.utils.*;
import co.aikar.timings.Timings;
import co.aikar.timings.TimingsHistory;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.*;

/**
 * author: MagicDroidX Nukkit Project
 */
public class Level implements ChunkManager, Metadatable {

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

    // Lower values use less memory
    public static final int MAX_BLOCK_CACHE = 512;

    // The blocks that can randomly tick
    private static final boolean[] randomTickBlocks = new boolean[256];

    private static final int LCG_CONSTANT = 1013904223;

    private static final Entity[] EMPTY_ENTITY_ARR = new Entity[0];

    private static final Entity[] ENTITY_BUFFER = new Entity[512];

    public static int COMPRESSION_LEVEL = 8;

    private static int levelIdCounter = 1;

    private static int chunkLoaderCounter = 1;

    static {
        Level.randomTickBlocks[BlockID.GRASS] = true;
        Level.randomTickBlocks[BlockID.FARMLAND] = true;
        Level.randomTickBlocks[BlockID.MYCELIUM] = true;
        Level.randomTickBlocks[BlockID.SAPLING] = true;
        Level.randomTickBlocks[BlockID.LEAVES] = true;
        Level.randomTickBlocks[BlockID.LEAVES2] = true;
        Level.randomTickBlocks[BlockID.SNOW_LAYER] = true;
        Level.randomTickBlocks[BlockID.ICE] = true;
        Level.randomTickBlocks[BlockID.LAVA] = true;
        Level.randomTickBlocks[BlockID.STILL_LAVA] = true;
        Level.randomTickBlocks[BlockID.CACTUS] = true;
        Level.randomTickBlocks[BlockID.BEETROOT_BLOCK] = true;
        Level.randomTickBlocks[BlockID.CARROT_BLOCK] = true;
        Level.randomTickBlocks[BlockID.POTATO_BLOCK] = true;
        Level.randomTickBlocks[BlockID.MELON_STEM] = true;
        Level.randomTickBlocks[BlockID.PUMPKIN_STEM] = true;
        Level.randomTickBlocks[BlockID.WHEAT_BLOCK] = true;
        Level.randomTickBlocks[BlockID.SUGARCANE_BLOCK] = true;
        Level.randomTickBlocks[BlockID.RED_MUSHROOM] = true;
        Level.randomTickBlocks[BlockID.BROWN_MUSHROOM] = true;
        Level.randomTickBlocks[BlockID.NETHER_WART_BLOCK] = true;
        Level.randomTickBlocks[BlockID.FIRE] = true;
        Level.randomTickBlocks[BlockID.GLOWING_REDSTONE_ORE] = true;
        Level.randomTickBlocks[BlockID.COCOA_BLOCK] = true;
    }

    public final Long2ObjectOpenHashMap<Entity> updateEntities = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Player> players = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Entity> entities = new Long2ObjectOpenHashMap<>();

    private final ConcurrentLinkedQueue<BlockEntity> updateBlockEntities = new ConcurrentLinkedQueue<>();

    private final Server server;

    private final int levelId;

    private final Int2ObjectOpenHashMap<ChunkLoader> loaders = new Int2ObjectOpenHashMap<>();

    private final Int2IntMap loaderCounter = new Int2IntOpenHashMap();

    private final Long2ObjectOpenHashMap<Map<Integer, ChunkLoader>> chunkLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Map<Integer, Player>> playerLoaders = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Deque<DataPacket>> chunkPackets = new Long2ObjectOpenHashMap<>();

    private final Long2LongMap unloadQueue = Long2LongMaps.synchronize(new Long2LongOpenHashMap());

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

    private final Queue<Block> normalUpdateQueue = new ConcurrentLinkedDeque<>();

    private final ConcurrentMap<Long, Int2ObjectMap<Player>> chunkSendQueue = new ConcurrentHashMap<>();

    private final LongSet chunkSendTasks = new LongOpenHashSet();

    private final Long2ObjectOpenHashMap<Boolean> chunkPopulationQueue = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Boolean> chunkPopulationLock = new Long2ObjectOpenHashMap<>();
//    private final TreeSet<BlockUpdateEntry> updateQueue = new TreeSet<>();
//    private final List<BlockUpdateEntry> nextTickUpdates = Lists.newArrayList();
    //private final Map<BlockVector3, Integer> updateQueueIndex = new HashMap<>();

    private final Long2ObjectOpenHashMap<Boolean> chunkGenerationQueue = new Long2ObjectOpenHashMap<>();

    private final Long2IntMap chunkTickList = new Long2IntOpenHashMap();

    private final String folderName;

    private final boolean useSections;

    private final Vector3 temporalVector;

    private final int chunkTickRadius;

    private final int chunksPerTicks;

    private final boolean clearChunksOnTick;

    private final Class<? extends Generator> generatorClass;

    private final IterableThreadLocal<Generator> generators = new IterableThreadLocal<Generator>() {
        @Override
        public Generator init() {
            try {
                final Generator generator = Level.this.generatorClass.getConstructor(Map.class)
                    .newInstance(Level.this.provider.getGeneratorOptions());
                final NukkitRandom rand = new NukkitRandom(Level.this.getSeed());
                if (Server.getInstance().isPrimaryThread()) {
                    generator.init(Level.this, rand);
                }
                generator.init(new PopChunkManager(Level.this.getSeed()), rand);
                return generator;
            } catch (final Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    private final Map<Long, Map<Character, Object>> lightQueue = new ConcurrentHashMap<>(8, 0.9f, 1);

    public boolean stopTime;

    public float skyLightSubtracted;

    public int sleepTicks = 0;

    public LevelTimings timings;

    public int tickRateTime = 0;

    public int tickRateCounter = 0;

    public GameRules gameRules;

    private boolean cacheChunks = false;

    private LevelProvider provider;

    private float time;

    private Vector3 mutableBlock;

    private int chunkGenerationQueueSize = 8;

    private int chunkPopulationQueueSize = 2;

    private boolean autoSave;

    private BlockMetadataStore blockMetadata;

    private Position temporalPosition;

    private int updateLCG = ThreadLocalRandom.current().nextInt();

    private int tickRate;

    private boolean raining = false;

    private int rainTime = 0;

    private boolean thundering = false;

    private int thunderTime = 0;

    private long levelCurrentTick = 0;

    private int dimension;

    private int lastUnloadIndex;

    public Level(final Server server, final String name, final String path, final Class<? extends LevelProvider> provider) {
        this.levelId = Level.levelIdCounter++;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();

        final boolean convert = provider == McRegion.class || provider == LevelDB.class;
        try {
            if (convert) {
                final String newPath = new File(path).getParent() + "/" + name + ".old/";
                new File(path).renameTo(new File(newPath));
                this.provider = provider.getConstructor(Level.class, String.class, String.class).newInstance(this, newPath, name);
            } else {
                this.provider = provider.getConstructor(Level.class, String.class, String.class).newInstance(this, path, name);
            }
        } catch (final Exception e) {
            throw new LevelException("Caused by " + Utils.getExceptionMessage(e));
        }

        this.timings = new LevelTimings(this);

        if (convert) {
            this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.updating",
                TextFormat.GREEN + this.provider.getName() + TextFormat.WHITE));
            final LevelProvider old = this.provider;
            try {
                this.provider = new LevelProviderConverter(this, path)
                    .from(old)
                    .to(Anvil.class)
                    .perform();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            old.close();
        }

        this.provider.updateLevelName(name);

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.preparing",
            TextFormat.GREEN + this.provider.getName() + TextFormat.WHITE));

        this.generatorClass = Generator.getGenerator(this.provider.getGenerator());

        try {
            this.useSections = (boolean) provider.getMethod("usesChunkSection").invoke(null);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        this.folderName = name;
        this.time = this.provider.getTime();

        this.raining = this.provider.isRaining();
        this.rainTime = this.provider.getRainTime();
        if (this.rainTime <= 0) {
            this.setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.thundering = this.provider.isThundering();
        this.thunderTime = this.provider.getThunderTime();
        if (this.thunderTime <= 0) {
            this.setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.levelCurrentTick = this.provider.getCurrentTick();
        this.updateQueue = new BlockUpdateScheduler(this, this.levelCurrentTick);

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

    public Level(final Server server, final String name, final byte[] serializedWorld) {
        this.levelId = Level.levelIdCounter++;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();

        try {
            this.provider = WoolFormat.class.getConstructor(Level.class, String.class, byte[].class).newInstance(this, name, serializedWorld);
        } catch (final Exception e) {
            throw new LevelException("Caused by " + Utils.getExceptionMessage(e));
        }

        this.timings = new LevelTimings(this);


        this.provider.updateLevelName(name);

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.preparing",
                TextFormat.GREEN + this.provider.getName() + TextFormat.WHITE));

        this.generatorClass = Generator.getGenerator(this.provider.getGenerator());

        try {
            this.useSections = (boolean) this.provider.getClass().getMethod("usesChunkSection").invoke(null);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        this.folderName = name;
        this.time = this.provider.getTime();

        this.raining = this.provider.isRaining();
        this.rainTime = this.provider.getRainTime();
        if (this.rainTime <= 0) {
            this.setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.thundering = this.provider.isThundering();
        this.thunderTime = this.provider.getThunderTime();
        if (this.thunderTime <= 0) {
            this.setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.levelCurrentTick = this.provider.getCurrentTick();
        this.updateQueue = new BlockUpdateScheduler(this, this.levelCurrentTick);

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

    public static long chunkHash(final int x, final int z) {
        return (long) x << 32 | z & 0xffffffffL;
    }

    public static long blockHash(final int x, final int y, final int z) {
        if (y < 0 || y >= 256) {
            throw new IllegalArgumentException("Y coordinate y is out of range!");
        }
        return ((long) x & (long) 0xFFFFFFF) << 36 | ((long) y & (long) 0xFF) << 28 | (long) z & (long) 0xFFFFFFF;
    }

    public static char localBlockHash(final double x, final double y, final double z) {
        final byte hi = (byte) (((int) x & 15) + (((int) z & 15) << 4));
        final byte lo = (byte) y;
        return (char) ((hi & 0xFF) << 8 | lo & 0xFF);
    }

    public static Vector3 getBlockXYZ(final long chunkHash, final char blockHash) {
        final int hi = (byte) (blockHash >>> 8);
        final int lo = (byte) blockHash;
        final int y = lo & 0xFF;
        final int x = (hi & 0xF) + (Level.getHashX(chunkHash) << 4);
        final int z = (hi >> 4 & 0xF) + (Level.getHashZ(chunkHash) << 4);
        return new Vector3(x, y, z);
    }

    public static int chunkBlockHash(final int x, final int y, final int z) {
        return x << 12 | z << 8 | y;
    }

    public static int getHashX(final long hash) {
        return (int) (hash >> 32);
    }

    public static int getHashZ(final long hash) {
        return (int) hash;
    }

    public static Vector3 getBlockXYZ(final BlockVector3 hash) {
        return new Vector3(hash.x, hash.y, hash.z);
    }

    public static Chunk.Entry getChunkXZ(final long hash) {
        return new Chunk.Entry(Level.getHashX(hash), Level.getHashZ(hash));
    }

    public static int generateChunkLoaderId(final ChunkLoader loader) {
        if (loader.getLoaderId() == 0) {
            return Level.chunkLoaderCounter++;
        } else {
            throw new IllegalStateException("ChunkLoader has a loader id already assigned: " + loader.getLoaderId());
        }
    }

    public int getTickRate() {
        return this.tickRate;
    }

    public void setTickRate(final int tickRate) {
        this.tickRate = tickRate;
    }

    public int getTickRateTime() {
        return this.tickRateTime;
    }

    public void initLevel() {
        final Generator generator = this.generators.get();
        this.dimension = generator.getDimension();
        this.gameRules = this.provider.getGamerules();
    }

    public Generator getGenerator() {
        return this.generators.get();
    }

    public BlockMetadataStore getBlockMetadata() {
        return this.blockMetadata;
    }

    public Server getServer() {
        return this.server;
    }

    public final LevelProvider getProvider() {
        return this.provider;
    }

    public final int getId() {
        return this.levelId;
    }

    public void close() {
        if (this.getAutoSave()) {
            this.save(true);
        }

        this.provider.close();
        this.provider = null;
        this.blockMetadata = null;
        this.temporalPosition = null;
        this.server.getLevels().remove(this.levelId);
        this.generators.clean();
    }

    public void addSound(final Vector3 pos, final Sound sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(final Vector3 pos, final Sound sound, final float volume, final float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(final Vector3 pos, final Sound sound, final float volume, final float pitch, final Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(new Player[0]));
    }

    public void addSound(final Vector3 pos, final Sound sound, final float volume, final float pitch, final Player... players) {
        Preconditions.checkArgument(volume >= 0 && volume <= 1, "Sound volume must be between 0 and 1");
        Preconditions.checkArgument(pitch >= 0, "Sound pitch must be higher than 0");

        final PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = volume;
        packet.pitch = pitch;
        packet.x = pos.getFloorX();
        packet.y = pos.getFloorY();
        packet.z = pos.getFloorZ();

        if (players == null || players.length == 0) {
            this.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, packet);
        } else {
            Server.broadcastPacket(players, packet);
        }
    }

    public void addLevelEvent(final Vector3 pos, final int event) {
        final LevelEventPacket pk = new LevelEventPacket();
        pk.evid = event;
        pk.x = (float) pos.x;
        pk.y = (float) pos.y;
        pk.z = (float) pos.z;

        this.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
    }

    public void addLevelSoundEvent(final Vector3 pos, final int type, final int data, final int entityType) {
        this.addLevelSoundEvent(pos, type, data, entityType, false, false);
    }

    public void addLevelSoundEvent(final Vector3 pos, final int type, final int data, final int entityType, final boolean isBaby, final boolean isGlobal) {
        final String identifier = AddEntityPacket.LEGACY_IDS.getOrDefault(entityType, ":");
        this.addLevelSoundEvent(pos, type, data, identifier, isBaby, isGlobal);
    }

    public void addLevelSoundEvent(final Vector3 pos, final int type) {
        this.addLevelSoundEvent(pos, type, -1);
    }

    /**
     * Broadcasts sound to players
     *
     * @param pos position where sound should be played
     * @param type ID of the sound from {@link LevelSoundEventPacket}
     * @param data generic data that can affect sound
     */
    public void addLevelSoundEvent(final Vector3 pos, final int type, final int data) {
        this.addLevelSoundEvent(pos, type, data, ":", false, false);
    }

    public void addLevelSoundEvent(final Vector3 pos, final int type, final int data, final String identifier, final boolean isBaby, final boolean isGlobal) {
        final LevelSoundEventPacket pk = new LevelSoundEventPacket();
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

    public void addParticle(final Particle particle) {
        this.addParticle(particle, (Player[]) null);
    }

    public void addParticle(final Particle particle, final Player player) {
        this.addParticle(particle, new Player[]{player});
    }

    public void addParticle(final Particle particle, final Player[] players) {
        final DataPacket[] packets = particle.encode();

        if (players == null) {
            if (packets != null) {
                for (final DataPacket packet : packets) {
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

    public void addParticle(final Particle particle, final Collection<Player> players) {
        this.addParticle(particle, players.toArray(new Player[0]));
    }

    public void addParticleEffect(final Vector3 pos, final ParticleEffect particleEffect) {
        this.addParticleEffect(pos, particleEffect, -1, this.dimension, (Player[]) null);
    }

    public void addParticleEffect(final Vector3 pos, final ParticleEffect particleEffect, final long uniqueEntityId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, this.dimension, (Player[]) null);
    }

    public void addParticleEffect(final Vector3 pos, final ParticleEffect particleEffect, final long uniqueEntityId, final int dimensionId) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, (Player[]) null);
    }

    public void addParticleEffect(final Vector3 pos, final ParticleEffect particleEffect, final long uniqueEntityId, final int dimensionId, final Collection<Player> players) {
        this.addParticleEffect(pos, particleEffect, uniqueEntityId, dimensionId, players.toArray(new Player[0]));
    }

    public void addParticleEffect(final Vector3 pos, final ParticleEffect particleEffect, final long uniqueEntityId, final int dimensionId, final Player... players) {
        this.addParticleEffect(pos.asVector3f(), particleEffect.getIdentifier(), uniqueEntityId, dimensionId, players);
    }

    public void addParticleEffect(final Vector3f pos, final String identifier, final long uniqueEntityId, final int dimensionId, final Player... players) {
        final SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
        pk.identifier = identifier;
        pk.uniqueEntityId = uniqueEntityId;
        pk.dimensionId = dimensionId;
        pk.position = pos;

        if (players == null || players.length == 0) {
            this.addChunkPacket(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, pk);
        } else {
            Server.broadcastPacket(players, pk);
        }
    }

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(final boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean unload() {
        return this.unload(false);
    }

    public boolean unload(final boolean force) {
        final LevelUnloadEvent ev = new LevelUnloadEvent(this);

        if (this == this.server.getDefaultLevel() && !force) {
            ev.setCancelled();
        }

        this.server.getPluginManager().callEvent(ev);

        if (!force && ev.isCancelled()) {
            return false;
        }

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.unloading",
            TextFormat.GREEN + this.getName() + TextFormat.WHITE));
        final Level defaultLevel = this.server.getDefaultLevel();

        for (final Player player : new ArrayList<>(this.getPlayers().values())) {
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

    public Map<Integer, Player> getChunkPlayers(final int chunkX, final int chunkZ) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        if (this.playerLoaders.containsKey(index)) {
            return new HashMap<>(this.playerLoaders.get(index));
        } else {
            return new HashMap<>();
        }
    }

    public ChunkLoader[] getChunkLoaders(final int chunkX, final int chunkZ) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunkLoaders.containsKey(index)) {
            return this.chunkLoaders.get(index).values().toArray(new ChunkLoader[0]);
        } else {
            return new ChunkLoader[0];
        }
    }

    public void addChunkPacket(final int chunkX, final int chunkZ, final DataPacket packet) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunkPackets) {
            final Deque<DataPacket> packets = this.chunkPackets.computeIfAbsent(index, i -> new ArrayDeque<>());
            packets.add(packet);
        }
    }

    public void registerChunkLoader(final ChunkLoader loader, final int chunkX, final int chunkZ) {
        this.registerChunkLoader(loader, chunkX, chunkZ, true);
    }

    public void registerChunkLoader(final ChunkLoader loader, final int chunkX, final int chunkZ, final boolean autoLoad) {
        final int hash = loader.getLoaderId();
        final long index = Level.chunkHash(chunkX, chunkZ);
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

    public void unregisterChunkLoader(final ChunkLoader loader, final int chunkX, final int chunkZ) {
        final int hash = loader.getLoaderId();
        final long index = Level.chunkHash(chunkX, chunkZ);
        final Map<Integer, ChunkLoader> chunkLoadersIndex = this.chunkLoaders.get(index);
        if (chunkLoadersIndex != null) {
            final ChunkLoader oldLoader = chunkLoadersIndex.remove(hash);
            if (oldLoader != null) {
                if (chunkLoadersIndex.isEmpty()) {
                    this.chunkLoaders.remove(index);
                    this.playerLoaders.remove(index);
                    this.unloadChunkRequest(chunkX, chunkZ, true);
                } else {
                    final Map<Integer, Player> playerLoadersIndex = this.playerLoaders.get(index);
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
            this.time += this.tickRate;
        }
    }

    public void sendTime(final Player... players) {
        final SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;

        Server.broadcastPacket(players, pk);
    }

    public void sendTime() {
        this.sendTime(this.players.values().toArray(new Player[0]));
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    public void doTick(final int currentTick) {
        this.timings.doTick.startTiming();

        this.updateBlockLight(this.lightQueue);
        this.checkTime();

        if (currentTick % 1200 == 0) { // Send time to client every 60 seconds to make sure it stay in sync
            this.sendTime();
        }

        // Tick Weather
        if (this.gameRules.getBoolean(GameRule.DO_WEATHER_CYCLE)) {
            this.rainTime--;
            if (this.rainTime <= 0) {
                if (!this.setRaining(!this.raining)) {
                    if (this.raining) {
                        this.setRainTime(ThreadLocalRandom.current().nextInt(12000) + 12000);
                    } else {
                        this.setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                    }
                }
            }

            this.thunderTime--;
            if (this.thunderTime <= 0) {
                if (!this.setThundering(!this.thundering)) {
                    if (this.thundering) {
                        this.setThunderTime(ThreadLocalRandom.current().nextInt(12000) + 3600);
                    } else {
                        this.setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                    }
                }
            }

            if (this.isThundering()) {
                final Map<Long, ? extends FullChunk> chunks = this.getChunks();
                if (chunks instanceof Long2ObjectOpenHashMap) {
                    final Long2ObjectOpenHashMap<? extends FullChunk> fastChunks = (Long2ObjectOpenHashMap) chunks;
                    final ObjectIterator<? extends Long2ObjectMap.Entry<? extends FullChunk>> iter = fastChunks.long2ObjectEntrySet().fastIterator();
                    while (iter.hasNext()) {
                        final Long2ObjectMap.Entry<? extends FullChunk> entry = iter.next();
                        this.performThunder(entry.getLongKey(), entry.getValue());
                    }
                } else {
                    for (final Map.Entry<Long, ? extends FullChunk> entry : this.getChunks().entrySet()) {
                        this.performThunder(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

        this.levelCurrentTick++;

        this.unloadChunks();
        this.timings.doTickPending.startTiming();

        final int polled = 0;

        this.updateQueue.tick(this.getCurrentTick());
        this.timings.doTickPending.stopTiming();

        Block block;
        while ((block = this.normalUpdateQueue.poll()) != null) {
            block.onUpdate(Level.BLOCK_UPDATE_NORMAL);
        }

        TimingsHistory.entityTicks += this.updateEntities.size();
        this.timings.entityTick.startTiming();

        if (!this.updateEntities.isEmpty()) {
            for (final long id : new ArrayList<>(this.updateEntities.keySet())) {
                final Entity entity = this.updateEntities.get(id);
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

        synchronized (this.changedBlocks) {
            if (!this.changedBlocks.isEmpty()) {
                if (!this.players.isEmpty()) {
                    final ObjectIterator<Long2ObjectMap.Entry<SoftReference<Map<Character, Object>>>> iter = this.changedBlocks.long2ObjectEntrySet().fastIterator();
                    while (iter.hasNext()) {
                        final Long2ObjectMap.Entry<SoftReference<Map<Character, Object>>> entry = iter.next();
                        final long index = entry.getLongKey();
                        final Map<Character, Object> blocks = entry.getValue().get();
                        final int chunkX = Level.getHashX(index);
                        final int chunkZ = Level.getHashZ(index);
                        if (blocks == null || blocks.size() > Level.MAX_BLOCK_CACHE) {
                            final FullChunk chunk = this.getChunk(chunkX, chunkZ);
                            for (final Player p : this.getChunkPlayers(chunkX, chunkZ).values()) {
                                p.onChunkChanged(chunk);
                            }
                        } else {
                            final Collection<Player> toSend = this.getChunkPlayers(chunkX, chunkZ).values();
                            final Player[] playerArray = toSend.toArray(new Player[0]);
                            final Vector3[] blocksArray = new Vector3[blocks.size()];
                            int i = 0;
                            for (final char blockHash : blocks.keySet()) {
                                final Vector3 hash = Level.getBlockXYZ(index, blockHash);
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

        synchronized (this.chunkPackets) {
            for (final long index : this.chunkPackets.keySet()) {
                final int chunkX = Level.getHashX(index);
                final int chunkZ = Level.getHashZ(index);
                final Player[] chunkPlayers = this.getChunkPlayers(chunkX, chunkZ).values().toArray(new Player[0]);
                if (chunkPlayers.length > 0) {
                    for (final DataPacket pk : this.chunkPackets.get(index)) {
                        Server.broadcastPacket(chunkPlayers, pk);
                    }
                }
            }
            this.chunkPackets.clear();
        }

        if (this.gameRules.isStale()) {
            final GameRulesChangedPacket packet = new GameRulesChangedPacket();
            packet.gameRules = this.gameRules;
            Server.broadcastPacket(this.players.values().toArray(new Player[0]), packet);
            this.gameRules.refresh();
        }

        this.timings.doTick.stopTiming();
    }

    public Vector3 adjustPosToNearbyEntity(Vector3 pos) {
        pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
        final AxisAlignedBB axisalignedbb = new SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), 255, pos.getZ()).expand(3, 3, 3);
        final List<Entity> list = new ArrayList<>();

        for (final Entity entity : this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && this.canBlockSeeSky(entity)) {
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
        for (final Player p : this.getPlayers().values()) {
            if (!p.isSleeping()) {
                resetTime = false;
                break;
            }
        }

        if (resetTime) {
            final int time = this.getTime() % Level.TIME_FULL;

            if (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE) {
                this.setTime(this.getTime() + Level.TIME_FULL - time);

                for (final Player p : this.getPlayers().values()) {
                    p.stopSleep();
                }
            }
        }
    }

    public void sendBlockExtraData(final int x, final int y, final int z, final int id, final int data) {
        this.sendBlockExtraData(x, y, z, id, data, this.getChunkPlayers(x >> 4, z >> 4).values());
    }

    public void sendBlockExtraData(final int x, final int y, final int z, final int id, final int data, final Collection<Player> players) {
        this.sendBlockExtraData(x, y, z, id, data, players.toArray(new Player[0]));
    }

    public void sendBlockExtraData(final int x, final int y, final int z, final int id, final int data, final Player[] players) {
        final LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_SET_DATA;
        pk.x = x + 0.5f;
        pk.y = y + 0.5f;
        pk.z = z + 0.5f;
        pk.data = data << 8 | id;

        Server.broadcastPacket(players, pk);
    }

    public void sendBlocks(final Player[] target, final Vector3[] blocks) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE);
    }

    public void sendBlocks(final Player[] target, final Vector3[] blocks, final int flags) {
        this.sendBlocks(target, blocks, flags, 0);
    }

    public void sendBlocks(final Player[] target, final Vector3[] blocks, final int flags, final boolean optimizeRebuilds) {
        this.sendBlocks(target, blocks, flags, 0, optimizeRebuilds);
    }

    public void sendBlocks(final Player[] target, final Vector3[] blocks, final int flags, final int dataLayer) {
        this.sendBlocks(target, blocks, flags, dataLayer, false);
    }

    public void sendBlocks(final Player[] target, final Vector3[] blocks, final int flags, final int dataLayer, final boolean optimizeRebuilds) {
        int size = 0;
        for (final Vector3 block : blocks) {
            if (block != null) {
                size++;
            }
        }
        int packetIndex = 0;
        final UpdateBlockPacket[] packets = new UpdateBlockPacket[size];
        LongSet chunks = null;
        if (optimizeRebuilds) {
            chunks = new LongOpenHashSet();
        }
        for (final Vector3 b : blocks) {
            if (b == null) {
                continue;
            }
            boolean first = !optimizeRebuilds;

            if (optimizeRebuilds) {
                final long index = Level.chunkHash((int) b.x >> 4, (int) b.z >> 4);
                if (!chunks.contains(index)) {
                    chunks.add(index);
                    first = true;
                }
            }

            final UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.x = (int) b.x;
            updateBlockPacket.y = (int) b.y;
            updateBlockPacket.z = (int) b.z;
            updateBlockPacket.flags = first ? flags : UpdateBlockPacket.FLAG_NONE;
            updateBlockPacket.dataLayer = dataLayer;
            final int fullId;
            if (b instanceof Block) {
                fullId = ((Block) b).getFullId();
            } else {
                fullId = this.getFullBlock((int) b.x, (int) b.y, (int) b.z);
            }
            try {
                updateBlockPacket.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(fullId);
            } catch (final NoSuchElementException e) {
                throw new IllegalStateException("Unable to create BlockUpdatePacket at (" +
                    b.x + ", " + b.y + ", " + b.z + ") in " + this.getName(), e);
            }
            packets[packetIndex++] = updateBlockPacket;
        }
        this.server.batchPackets(target, packets);
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(final boolean force) {
        if (!this.getAutoSave() && !force) {
            return false;
        }

        this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

        this.provider.setTime((int) this.time);
        this.provider.setRaining(this.raining);
        this.provider.setRainTime(this.rainTime);
        this.provider.setThundering(this.thundering);
        this.provider.setThunderTime(this.thunderTime);
        this.provider.setCurrentTick(this.levelCurrentTick);
        this.provider.setGameRules(this.gameRules);
        this.saveChunks();
        if (this.provider instanceof BaseLevelProvider) {
            this.provider.saveLevelData();
        }

        return true;
    }

    public void saveChunks() {
        this.provider.saveChunks();
    }

    public void updateAroundRedstone(final Vector3 pos, final BlockFace face) {
        for (final BlockFace side : BlockFace.values()) {
            if (face != null && side == face) {
                continue;
            }

            this.getBlock(pos.getSide(side)).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
        }
    }

    public void updateComparatorOutputLevel(final Vector3 v) {
        for (final BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Vector3 pos = v.getSide(face);

            if (this.isChunkLoaded((int) pos.x >> 4, (int) pos.z >> 4)) {
                Block block1 = this.getBlock(pos);

                if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                } else if (block1.isNormalBlock()) {
                    pos = pos.getSide(face);
                    block1 = this.getBlock(pos);

                    if (BlockRedstoneDiode.isDiode(block1)) {
                        block1.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                    }
                }
            }
        }
    }

    public void updateAround(final Vector3 pos) {
        this.updateAround((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateAround(final int x, final int y, final int z) {
        BlockUpdateEvent ev;
        this.server.getPluginManager().callEvent(
            ev = new BlockUpdateEvent(this.getBlock(x, y - 1, z)));
        if (!ev.isCancelled()) {
            this.normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
            ev = new BlockUpdateEvent(this.getBlock(x, y + 1, z)));
        if (!ev.isCancelled()) {
            this.normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
            ev = new BlockUpdateEvent(this.getBlock(x - 1, y, z)));
        if (!ev.isCancelled()) {
            this.normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
            ev = new BlockUpdateEvent(this.getBlock(x + 1, y, z)));
        if (!ev.isCancelled()) {
            this.normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
            ev = new BlockUpdateEvent(this.getBlock(x, y, z - 1)));
        if (!ev.isCancelled()) {
            this.normalUpdateQueue.add(ev.getBlock());
        }

        this.server.getPluginManager().callEvent(
            ev = new BlockUpdateEvent(this.getBlock(x, y, z + 1)));
        if (!ev.isCancelled()) {
            this.normalUpdateQueue.add(ev.getBlock());
        }
    }

    public void scheduleUpdate(final Block pos, final int delay) {
        this.scheduleUpdate(pos, pos, delay, 0, true);
    }

    public void scheduleUpdate(final Block block, final Vector3 pos, final int delay) {
        this.scheduleUpdate(block, pos, delay, 0, true);
    }

    public void scheduleUpdate(final Block block, final Vector3 pos, final int delay, final int priority) {
        this.scheduleUpdate(block, pos, delay, priority, true);
    }

    public void scheduleUpdate(final Block block, final Vector3 pos, final int delay, final int priority, final boolean checkArea) {
        if (block.getId() == 0 || checkArea && !this.isChunkLoaded(block.getFloorX() >> 4, block.getFloorZ() >> 4)) {
            return;
        }

        final BlockUpdateEntry entry = new BlockUpdateEntry(pos.floor(), block, (long) delay + this.getCurrentTick(), priority);

        if (!this.updateQueue.contains(entry)) {
            this.updateQueue.add(entry);
        }
    }

    public boolean cancelSheduledUpdate(final Vector3 pos, final Block block) {
        return this.updateQueue.remove(new BlockUpdateEntry(pos, block));
    }

    public boolean isUpdateScheduled(final Vector3 pos, final Block block) {
        return this.updateQueue.contains(new BlockUpdateEntry(pos, block));
    }

    public boolean isBlockTickPending(final Vector3 pos, final Block block) {
        return this.updateQueue.isBlockTickPending(pos, block);
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(final FullChunk chunk) {
        final int minX = (chunk.getX() << 4) - 2;
        final int maxX = minX + 16 + 2;
        final int minZ = (chunk.getZ() << 4) - 2;
        final int maxZ = minZ + 16 + 2;

        return this.getPendingBlockUpdates(new SimpleAxisAlignedBB(minX, 0, minZ, maxX, 256, maxZ));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(final AxisAlignedBB boundingBox) {
        return this.updateQueue.getPendingBlockUpdates(boundingBox);
    }

    public Block[] getCollisionBlocks(final AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }

    public Block[] getCollisionBlocks(final AxisAlignedBB bb, final boolean targetFirst) {
        final int minX = NukkitMath.floorDouble(bb.getMinX());
        final int minY = NukkitMath.floorDouble(bb.getMinY());
        final int minZ = NukkitMath.floorDouble(bb.getMinZ());
        final int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        final int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        final int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        final List<Block> collides = new ArrayList<>();

        if (targetFirst) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        final Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
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
                        final Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                        if (block != null && block.getId() != 0 && block.collidesWithBB(bb)) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.toArray(new Block[0]);
    }

    public boolean isFullBlock(final Vector3 pos) {
        final AxisAlignedBB bb;
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

    public AxisAlignedBB[] getCollisionCubes(final Entity entity, final AxisAlignedBB bb) {
        return this.getCollisionCubes(entity, bb, true);
    }

    public AxisAlignedBB[] getCollisionCubes(final Entity entity, final AxisAlignedBB bb, final boolean entities) {
        return this.getCollisionCubes(entity, bb, entities, false);
    }

    public AxisAlignedBB[] getCollisionCubes(final Entity entity, final AxisAlignedBB bb, final boolean entities, final boolean solidEntities) {
        final int minX = NukkitMath.floorDouble(bb.getMinX());
        final int minY = NukkitMath.floorDouble(bb.getMinY());
        final int minZ = NukkitMath.floorDouble(bb.getMinZ());
        final int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        final int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        final int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        final List<AxisAlignedBB> collides = new ArrayList<>();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    final Block block = this.getBlock(this.temporalVector.setComponents(x, y, z), false);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            for (final Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities && !ent.canPassThrough()) {
                    collides.add(ent.boundingBox.clone());
                }
            }
        }

        return collides.toArray(new AxisAlignedBB[0]);
    }

    public boolean hasCollision(final Entity entity, final AxisAlignedBB bb, final boolean entities) {
        final int minX = NukkitMath.floorDouble(bb.getMinX());
        final int minY = NukkitMath.floorDouble(bb.getMinY());
        final int minZ = NukkitMath.floorDouble(bb.getMinZ());
        final int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        final int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        final int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    final Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
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

    public int getFullLight(final Vector3 pos) {
        final FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
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

    public int calculateSkylightSubtracted(final float tickDiff) {
        final float angle = this.calculateCelestialAngle(this.getTime(), tickDiff);
        float light = 1 - (MathHelper.cos(angle * ((float) Math.PI * 2F)) * 2 + 0.5f);
        light = light < 0 ? 0 : light > 1 ? 1 : light;
        light = 1 - light;
        light = (float) ((double) light * ((this.isRaining() ? 1 : 0) - (double) 5f / 16d));
        light = (float) ((double) light * ((this.isThundering() ? 1 : 0) - (double) 5f / 16d));
        light = 1 - light;
        return (int) (light * 11f);
    }

    public float calculateCelestialAngle(final int time, final float tickDiff) {
        float angle = ((float) time + tickDiff) / 24000f - 0.25f;

        if (angle < 0) {
            ++angle;
        }

        if (angle > 1) {
            --angle;
        }

        final float i = 1 - (float) ((Math.cos((double) angle * Math.PI) + 1) / 2d);
        angle = angle + (i - angle) / 3;
        return angle;
    }

    public int getMoonPhase(final long worldTime) {
        return (int) (worldTime / 24000 % 8 + 8) % 8;
    }

    public int getFullBlock(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4, false).getFullBlock(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized Block getBlock(final Vector3 pos) {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public synchronized Block getBlock(final Vector3 pos, final boolean load) {
        return this.getBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), load);
    }

    public synchronized Block getBlock(final int x, final int y, final int z) {
        return this.getBlock(x, y, z, true);
    }

    public synchronized Block getBlock(final int x, final int y, final int z, final boolean load) {
        final int fullState;
        if (y >= 0 && y < 256) {
            final int cx = x >> 4;
            final int cz = z >> 4;
            final BaseFullChunk chunk;
            if (load) {
                chunk = this.getChunk(cx, cz);
            } else {
                chunk = this.getChunkIfLoaded(cx, cz);
            }
            if (chunk != null) {
                fullState = chunk.getFullBlock(x & 0xF, y, z & 0xF);
            } else {
                fullState = 0;
            }
        } else {
            fullState = 0;
        }
        final Block block = Block.fullList[fullState & 0xFFF].clone();
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;
        return block;
    }

    public void updateAllLight(final Vector3 pos) {
        this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z);
        this.addLightUpdate((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateBlockSkyLight(final int x, final int y, final int z) {
        // todo
    }

    public void updateBlockLight(final Map<Long, Map<Character, Object>> map) {
        int size = map.size();
        if (size == 0) {
            return;
        }
        final Queue<Long> lightPropagationQueue = new ConcurrentLinkedQueue<>();
        final Queue<Object[]> lightRemovalQueue = new ConcurrentLinkedQueue<>();
        final Long2ObjectOpenHashMap<Object> visited = new Long2ObjectOpenHashMap<>();
        final Long2ObjectOpenHashMap<Object> removalVisited = new Long2ObjectOpenHashMap<>();

        final Iterator<Map.Entry<Long, Map<Character, Object>>> iter = map.entrySet().iterator();
        while (iter.hasNext() && size-- > 0) {
            final Map.Entry<Long, Map<Character, Object>> entry = iter.next();
            iter.remove();
            final long index = entry.getKey();
            final Map<Character, Object> blocks = entry.getValue();
            final int chunkX = Level.getHashX(index);
            final int chunkZ = Level.getHashZ(index);
            final int bx = chunkX << 4;
            final int bz = chunkZ << 4;
            for (final char blockHash : blocks.keySet()) {
                final int hi = (byte) (blockHash >>> 8);
                final int lo = (byte) blockHash;
                final int y = lo & 0xFF;
                final int x = (hi & 0xF) + bx;
                final int z = (hi >> 4 & 0xF) + bz;
                final BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, false);
                if (chunk != null) {
                    final int lcx = x & 0xF;
                    final int lcz = z & 0xF;
                    final int oldLevel = chunk.getBlockLight(lcx, y, lcz);
                    final int newLevel = Block.light[chunk.getBlockId(lcx, y, lcz)];
                    if (oldLevel != newLevel) {
                        this.setBlockLightAt(x, y, z, newLevel);
                        if (newLevel < oldLevel) {
                            removalVisited.put(Hash.hashBlock(x, y, z), this.changeBlocksPresent);
                            lightRemovalQueue.add(new Object[]{Hash.hashBlock(x, y, z), oldLevel});
                        } else {
                            visited.put(Hash.hashBlock(x, y, z), this.changeBlocksPresent);
                            lightPropagationQueue.add(Hash.hashBlock(x, y, z));
                        }
                    }
                }
            }
        }

        while (!lightRemovalQueue.isEmpty()) {
            final Object[] val = lightRemovalQueue.poll();
            final long node = (long) val[0];
            final int x = Hash.hashBlockX(node);
            final int y = Hash.hashBlockY(node);
            final int z = Hash.hashBlockZ(node);

            final int lightLevel = (int) val[1];

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
            final long node = lightPropagationQueue.poll();

            final int x = Hash.hashBlockX(node);
            final int y = Hash.hashBlockY(node);
            final int z = Hash.hashBlockZ(node);

            final int lightLevel = this.getBlockLightAt(x, y, z)
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

    public void addLightUpdate(final int x, final int y, final int z) {
        final long index = Level.chunkHash(x >> 4, z >> 4);
        Map<Character, Object> currentMap = this.lightQueue.get(index);
        if (currentMap == null) {
            currentMap = new ConcurrentHashMap<>(8, 0.9f, 1);
            this.lightQueue.put(index, currentMap);
        }
        currentMap.put(Level.localBlockHash(x, y, z), this.changeBlocksPresent);
    }

    public synchronized boolean setBlock(final Vector3 pos, final Block block) {
        return this.setBlock(pos, block, false);
    }

    public synchronized boolean setBlock(final Vector3 pos, final Block block, final boolean direct) {
        return this.setBlock(pos, block, direct, true);
    }

    public synchronized boolean setBlock(final Vector3 pos, final Block block, final boolean direct, final boolean update) {
        return this.setBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), block, direct, update);
    }

    public synchronized boolean setBlock(final int x, final int y, final int z, Block block, final boolean direct, final boolean update) {
        if (y < 0 || y >= 256) {
            return false;
        }
        final BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        chunk.initChunk();
        chunk.setGenerated();
        chunk.setPopulated();
        final Block blockPrevious;
//        synchronized (chunk) {
        blockPrevious = chunk.getAndSetBlock(x & 0xF, y, z & 0xF, block);
        if (blockPrevious.getFullId() == block.getFullId()) {
            return false;
        }
//        }
        block.x = x;
        block.y = y;
        block.z = z;
        block.level = this;
        final int cx = x >> 4;
        final int cz = z >> 4;
        final long index = Level.chunkHash(cx, cz);
        if (direct) {
            this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
            this.sendBlocks(this.getChunkPlayers(cx, cz).values().toArray(new Player[0]), new Block[]{Block.get(BlockID.AIR, 0, block)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
        } else {
            this.addBlockChange(index, x, y, z);
        }

        for (final ChunkLoader loader : this.getChunkLoaders(cx, cz)) {
            loader.onBlockChanged(block);
        }
        if (update) {
            if (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel()) {
                this.addLightUpdate(x, y, z);
            }
            final BlockUpdateEvent ev = new BlockUpdateEvent(block);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                for (final Entity entity : this.getNearbyEntities(new SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1))) {
                    entity.scheduleUpdate();
                }
                block = ev.getBlock();
                block.onUpdate(Level.BLOCK_UPDATE_NORMAL);
                this.updateAround(x, y, z);
            }
        }
        return true;
    }

    public void dropItem(final Vector3 source, final Item item) {
        this.dropItem(source, item, null);
    }

    public void dropItem(final Vector3 source, final Item item, final Vector3 motion) {
        this.dropItem(source, item, motion, 10);
    }

    public void dropItem(final Vector3 source, final Item item, final Vector3 motion, final int delay) {
        this.dropItem(source, item, motion, false, delay);
    }

    public void dropItem(final Vector3 source, final Item item, Vector3 motion, final boolean dropAround, final int delay) {
        if (motion == null) {
            if (dropAround) {
                final float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                final float f1 = ThreadLocalRandom.current().nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3(-MathHelper.sin(f1) * f, 0.20000000298023224, MathHelper.cos(f1) * f);
            } else {
                motion = new Vector3(new Random().nextDouble() * 0.2 - 0.1, 0.2,
                    new Random().nextDouble() * 0.2 - 0.1);
            }
        }

        final CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");

        if (item.getId() > 0 && item.getCount() > 0) {
            final EntityItem itemEntity = (EntityItem) Entity.createEntity("Item",
                this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
                new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
                    .add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))

                    .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.x))
                        .add(new DoubleTag("", motion.y)).add(new DoubleTag("", motion.z)))

                    .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", new Random().nextFloat() * 360))
                        .add(new FloatTag("", 0)))

                    .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

            if (itemEntity != null) {
                itemEntity.spawnToAll();
            }
        }
    }

    public Item useBreakOn(final Vector3 vector) {
        return this.useBreakOn(vector, null);
    }

    public Item useBreakOn(final Vector3 vector, final Item item) {
        return this.useBreakOn(vector, item, null);
    }

    public Item useBreakOn(final Vector3 vector, final Item item, final Player player) {
        return this.useBreakOn(vector, item, player, false);
    }

    public Item useBreakOn(final Vector3 vector, final Item item, final Player player, final boolean createParticles) {
        return this.useBreakOn(vector, null, item, player, createParticles);
    }

    public Item useBreakOn(final Vector3 vector, final BlockFace face, Item item, final Player player, final boolean createParticles) {
        if (player != null && player.getGamemode() > 2) {
            return null;
        }
        final Block target = this.getBlock(vector);
        final Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }

        final boolean isSilkTouch = item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null;

        if (player != null) {
            if (player.getGamemode() == 2) {
                final Tag tag = item.getNamedTagEntry("CanDestroy");
                boolean canBreak = false;
                if (tag instanceof ListTag) {
                    for (final Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            final Item entry = Item.fromString(((StringTag) v).data);
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

            double breakTime = target.getBreakTime(item, player);
            // this in
            // block
            // class

            if (player.isCreative() && breakTime > 0.15) {
                breakTime = 0.15;
            }

            if (player.hasEffect(Effect.SWIFTNESS)) {
                breakTime *= 1 - 0.2 * (player.getEffect(Effect.SWIFTNESS).getAmplifier() + 1);
            }

            if (player.hasEffect(Effect.MINING_FATIGUE)) {
                breakTime *= 1 - 0.3 * (player.getEffect(Effect.MINING_FATIGUE).getAmplifier() + 1);
            }

            final Enchantment eff = item.getEnchantment(Enchantment.ID_EFFICIENCY);

            if (eff != null && eff.getLevel() > 0) {
                breakTime *= 1 - 0.3 * eff.getLevel();
            }

            breakTime -= 0.15;

            final Item[] eventDrops;
            if (!player.isSurvival()) {
                eventDrops = new Item[0];
            } else if (isSilkTouch && target.canSilkTouch()) {
                eventDrops = new Item[]{target.toItem()};
            } else {
                eventDrops = target.getDrops(item);
            }

            final BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                player.lastBreak + breakTime * 1000 > System.currentTimeMillis());

            if (player.isSurvival() && !target.isBreakable(item)) {
                ev.setCancelled();
            } else if (!player.isOp() && this.isInSpawnRadius(target)) {
                ev.setCancelled();
            } else if (!ev.getInstaBreak() && ev.isFastBreak()) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return null;
            }

            player.lastBreak = System.currentTimeMillis();

            drops = ev.getDrops();
            dropExp = ev.getDropExp();
        } else if (!target.isBreakable(item)) {
            return null;
        } else if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            drops = new Item[]{target.toItem()};
        } else {
            drops = target.getDrops(item);
        }

        final Block above = this.getBlock(new Vector3(target.x, target.y + 1, target.z));
        if (above != null) {
            if (above.getId() == BlockID.FIRE) {
                this.setBlock(above, Block.get(BlockID.AIR), true);
            }
        }

        if (createParticles) {
            final Map<Integer, Player> players = this.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);

            this.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());

            if (player != null) {
                players.remove(player.getLoaderId());
            }
        }

        // Close BlockEntity before we check onBreak
        final BlockEntity blockEntity = this.getBlockEntity(target);
        if (blockEntity != null) {
            blockEntity.onBreak();
            blockEntity.close();

            this.updateComparatorOutputLevel(target);
        }

        target.onBreak(item);

        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        }

        if (this.gameRules.getBoolean(GameRule.DO_TILE_DROPS)) {

            if (!isSilkTouch && player != null && player.isSurvival() && dropExp > 0 && drops.length != 0) {
                this.dropExpOrb(vector.add(0.5, 0.5, 0.5), dropExp);
            }

            if (player == null || player.isSurvival()) {
                for (final Item drop : drops) {
                    if (drop.getCount() > 0) {
                        this.dropItem(vector.add(0.5, 0.5, 0.5), drop);
                    }
                }
            }
        }

        return item;
    }

    public void dropExpOrb(final Vector3 source, final int exp) {
        this.dropExpOrb(source, exp, null);
    }

    public void dropExpOrb(final Vector3 source, final int exp, final Vector3 motion) {
        this.dropExpOrb(source, exp, motion, 10);
    }

    public void dropExpOrb(final Vector3 source, final int exp, final Vector3 motion, final int delay) {
        final Random rand = ThreadLocalRandom.current();
        for (final int split : EntityXPOrb.splitIntoOrbSizes(exp)) {
            final CompoundTag nbt = Entity.getDefaultNBT(source, motion == null ? new Vector3(
                    (rand.nextDouble() * 0.2 - 0.1) * 2,
                    rand.nextDouble() * 0.4,
                    (rand.nextDouble() * 0.2 - 0.1) * 2) : motion,
                rand.nextFloat() * 360f, 0);

            nbt.putShort("Value", split);
            nbt.putShort("PickupDelay", delay);

            final Entity entity = Entity.createEntity("XpOrb", this.getChunk(source.getChunkX(), source.getChunkZ()), nbt);
            if (entity != null) {
                entity.spawnToAll();
            }
        }
    }

    public Item useItemOn(final Vector3 vector, final Item item, final BlockFace face, final float fx, final float fy, final float fz) {
        return this.useItemOn(vector, item, face, fx, fy, fz, null);
    }

    public Item useItemOn(final Vector3 vector, final Item item, final BlockFace face, final float fx, final float fy, final float fz, final Player player) {
        return this.useItemOn(vector, item, face, fx, fy, fz, player, true);
    }

    public Item useItemOn(final Vector3 vector, Item item, final BlockFace face, final float fx, final float fy, final float fz, final Player player, final boolean playSound) {
        final Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (block.y > 255 || block.y < 0) {
            return null;
        }

        if (block.y > 127 && this.getDimension() == Level.DIMENSION_NETHER) {
            return null;
        }

        if (target.getId() == BlockID.AIR) {
            return null;
        }

        if (player != null) {
            final PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
                target.getId() == 0 ? PlayerInteractEvent.Action.RIGHT_CLICK_AIR : PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > 2) {
                ev.setCancelled();
            }

            if (!player.isOp() && this.isInSpawnRadius(target)) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onUpdate(Level.BLOCK_UPDATE_TOUCH);
                if ((!player.isSneaking() || player.getInventory().getItemInHand().isNull()) && target.canBeActivated() && target.onActivate(item, player)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
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
                if (item.getId() == ItemID.BUCKET && ItemBucket.getDamageByTarget(item.getDamage()) == BlockID.WATER) {
                    player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(BlockID.AIR, 0, target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                }
                return null;
            }

            if (item.getId() == ItemID.BUCKET && ItemBucket.getDamageByTarget(item.getDamage()) == BlockID.WATER) {
                player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(BlockID.AIR, 0, target)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
            }
        } else if (target.canBeActivated() && target.onActivate(item, player)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
            }
            return item;
        }
        final Block hand;
        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        if (!(block.canBeReplaced() || hand.getId() == BlockID.SLAB && block.getId() == BlockID.SLAB)) {
            return null;
        }

        if (target.canBeReplaced()) {
            block = target;
            hand.position(block);
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null) {
            final Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
            int realCount = 0;
            for (final Entity e : entities) {
                if (e instanceof EntityArrow || e instanceof EntityItem || e instanceof Player && ((Player) e).isSpectator()) {
                    continue;
                }
                ++realCount;
            }

            if (player != null) {
                final Vector3 diff = player.getNextPosition().subtract(player.getPosition());
                if (diff.lengthSquared() > 0.00001) {
                    final AxisAlignedBB bb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
                    if (hand.getBoundingBox().intersectsWith(bb)) {
                        ++realCount;
                    }
                }
            }

            if (realCount > 0) {
                return null; // Entity in block
            }
        }

        if (player != null) {
            final BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            if (player.getGamemode() == 2) {
                final Tag tag = item.getNamedTagEntry("CanPlaceOn");
                boolean canPlace = false;
                if (tag instanceof ListTag) {
                    for (final Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            final Item entry = Item.fromString(((StringTag) v).data);
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
            if (!player.isOp() && this.isInSpawnRadius(target)) {
                event.setCancelled();
            }

            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
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

    public boolean isInSpawnRadius(final Vector3 vector3) {
        final int distance = this.server.getSpawnRadius();
        if (distance > -1) {
            final Vector2 t = new Vector2(vector3.x, vector3.z);
            final Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
            return t.distance(s) <= distance;
        }
        return false;
    }

    public Entity getEntity(final long entityId) {
        return this.entities.containsKey(entityId) ? this.entities.get(entityId) : null;
    }

    public Entity[] getEntities() {
        return this.entities.values().toArray(new Entity[0]);
    }

    public Entity[] getCollidingEntities(final AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Entity[] getCollidingEntities(final AxisAlignedBB bb, final Entity entity) {
        final List<Entity> nearby = new ArrayList<>();

        if (entity == null || entity.canCollide()) {
            final int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            final int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            final int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            final int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (final Entity ent : this.getChunkEntities(x, z, false).values()) {
                        if ((entity == null || ent != entity && entity.canCollideWith(ent))
                            && ent.boundingBox.intersectsWith(bb)) {
                            nearby.add(ent);
                        }
                    }
                }
            }
        }

        return nearby.toArray(new Entity[0]);
    }

    public Entity[] getNearbyEntities(final AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null);
    }

    public Entity[] getNearbyEntities(final AxisAlignedBB bb, final Entity entity) {
        return this.getNearbyEntities(bb, entity, false);
    }

    public Entity[] getNearbyEntities(final AxisAlignedBB bb, final Entity entity, final boolean loadChunks) {
        int index = 0;

        final int minX = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625);
        final int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625);
        final int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625);
        final int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625);

        ArrayList<Entity> overflow = null;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (final Entity ent : this.getChunkEntities(x, z, loadChunks).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        if (index < Level.ENTITY_BUFFER.length) {
                            Level.ENTITY_BUFFER[index] = ent;
                        } else {
                            if (overflow == null) {
                                overflow = new ArrayList<>(1024);
                            }
                            overflow.add(ent);
                        }
                        index++;
                    }
                }
            }
        }

        if (index == 0) {
            return Level.EMPTY_ENTITY_ARR;
        }
        final Entity[] copy;
        if (overflow == null) {
            copy = Arrays.copyOfRange(Level.ENTITY_BUFFER, 0, index);
            Arrays.fill(Level.ENTITY_BUFFER, 0, index, null);
        } else {
            copy = new Entity[Level.ENTITY_BUFFER.length + overflow.size()];
            System.arraycopy(Level.ENTITY_BUFFER, 0, copy, 0, Level.ENTITY_BUFFER.length);
            for (int i = 0; i < overflow.size(); i++) {
                copy[Level.ENTITY_BUFFER.length + i] = overflow.get(i);
            }
        }
        return copy;
    }

    public Map<Long, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public BlockEntity getBlockEntityById(final long blockEntityId) {
        return this.blockEntities.containsKey(blockEntityId) ? this.blockEntities.get(blockEntityId) : null;
    }

    public Map<Long, Player> getPlayers() {
        return this.players;
    }

    public Map<Integer, ChunkLoader> getLoaders() {
        return this.loaders;
    }

    public BlockEntity getBlockEntity(final Vector3 pos) {
        final FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
        }

        return null;
    }

    public BlockEntity getBlockEntityIfLoaded(final Vector3 pos) {
        final FullChunk chunk = this.getChunkIfLoaded((int) pos.x >> 4, (int) pos.z >> 4);

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
        }

        return null;
    }

    public Map<Long, Entity> getChunkEntities(final int X, final int Z) {
        return this.getChunkEntities(X, Z, true);
    }

    public Map<Long, Entity> getChunkEntities(final int X, final int Z, final boolean loadChunks) {
        final FullChunk chunk = loadChunks ? this.getChunk(X, Z) : this.getChunkIfLoaded(X, Z);
        return chunk != null ? chunk.getEntities() : Collections.emptyMap();
    }

    public Map<Long, BlockEntity> getChunkBlockEntities(final int X, final int Z) {
        final FullChunk chunk;
        return (chunk = this.getChunk(X, Z)) != null ? chunk.getBlockEntities() : Collections.emptyMap();
    }

    @Override
    public synchronized int getBlockIdAt(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockId(x & 0x0f, y & 0xff, z & 0x0f);
    }

    @Override
    public synchronized void setBlockFullIdAt(final int x, final int y, final int z, final int fullId) {
        this.setBlock(x, y, z, Block.fullList[fullId], false, false);
    }

    @Override
    public synchronized void setBlockIdAt(final int x, final int y, final int z, final int id) {
        this.getChunk(x >> 4, z >> 4, true).setBlockId(x & 0x0f, y & 0xff, z & 0x0f, id & 0xff);
        this.addBlockChange(x, y, z);
        this.temporalVector.setComponents(x, y, z);
        for (final ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(this.temporalVector);
        }
    }

    @Override
    public synchronized void setBlockAt(final int x, final int y, final int z, final int id, final int data) {
        final BaseFullChunk chunk = this.getChunk(x >> 4, z >> 4, true);
        chunk.setBlockId(x & 0x0f, y & 0xff, z & 0x0f, id & 0xff);
        chunk.setBlockData(x & 0x0f, y & 0xff, z & 0x0f, data & 0xf);
        this.addBlockChange(x, y, z);
        this.temporalVector.setComponents(x, y, z);
        for (final ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(this.temporalVector);
        }
    }

    @Override
    public synchronized int getBlockDataAt(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockData(x & 0x0f, y & 0xff, z & 0x0f);
    }

    @Override
    public synchronized void setBlockDataAt(final int x, final int y, final int z, final int data) {
        this.getChunk(x >> 4, z >> 4, true).setBlockData(x & 0x0f, y & 0xff, z & 0x0f, data & 0x0f);
        this.addBlockChange(x, y, z);
        this.temporalVector.setComponents(x, y, z);
        for (final ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(this.temporalVector);
        }
    }

    @Override
    public BaseFullChunk getChunk(final int chunkX, final int chunkZ) {
        return this.getChunk(chunkX, chunkZ, true);
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ, final BaseFullChunk chunk) {
        this.setChunk(chunkX, chunkZ, chunk, true);
    }

    @Override
    public long getSeed() {
        return this.provider.getSeed();
    }

    public void setSeed(final int seed) {
        this.provider.setSeed(seed);
    }

    public synchronized int getBlockExtraDataAt(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockExtraData(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized void setBlockExtraDataAt(final int x, final int y, final int z, final int id, final int data) {
        this.getChunk(x >> 4, z >> 4, true).setBlockExtraData(x & 0x0f, y & 0xff, z & 0x0f, data << 8 | id);

        this.sendBlockExtraData(x, y, z, id, data);
    }

    public synchronized int getBlockSkyLightAt(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockSkyLight(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized void setBlockSkyLightAt(final int x, final int y, final int z, final int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockSkyLight(x & 0x0f, y & 0xff, z & 0x0f, level & 0x0f);
    }

    public synchronized int getBlockLightAt(final int x, final int y, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockLight(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public synchronized void setBlockLightAt(final int x, final int y, final int z, final int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockLight(x & 0x0f, y & 0xff, z & 0x0f, level & 0x0f);
    }

    public int getBiomeId(final int x, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 0x0f, z & 0x0f);
    }

    public void setBiomeId(final int x, final int z, final byte biomeId) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, z & 0x0f, biomeId);
    }

    public int getHeightMap(final int x, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHeightMap(x & 0x0f, z & 0x0f);
    }

    public void setHeightMap(final int x, final int z, final int value) {
        this.getChunk(x >> 4, z >> 4, true).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
    }

    public Map<Long, ? extends FullChunk> getChunks() {
        return this.provider.getLoadedChunks();
    }

    public BaseFullChunk getChunk(final int chunkX, final int chunkZ, final boolean create) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        BaseFullChunk chunk = this.provider.getLoadedChunk(index);
        if (chunk == null) {
            chunk = this.forceLoadChunk(index, chunkX, chunkZ, create);
        }
        return chunk;
    }

    public BaseFullChunk getChunkIfLoaded(final int chunkX, final int chunkZ) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        return this.provider.getLoadedChunk(index);
    }

    public void generateChunkCallback(final int x, final int z, final BaseFullChunk chunk) {
        this.generateChunkCallback(x, z, chunk, true);
    }

    public void generateChunkCallback(final int x, final int z, BaseFullChunk chunk, final boolean isPopulated) {
        Timings.generationCallbackTimer.startTiming();
        final long index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.containsKey(index)) {
            final FullChunk oldChunk = this.getChunk(x, z, false);
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

                for (final ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkPopulated(chunk);
                }
            }
        } else if (this.chunkGenerationQueue.containsKey(index) || this.chunkPopulationLock.containsKey(index)) {
            this.chunkGenerationQueue.remove(index);
            this.chunkPopulationLock.remove(index);
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
        } else {
            chunk.setProvider(this.provider);
            this.setChunk(x, z, chunk, false);
        }
        Timings.generationCallbackTimer.stopTiming();
    }

    public void setChunk(final int chunkX, final int chunkZ, final BaseFullChunk chunk, final boolean unload) {
        if (chunk == null) {
            return;
        }

        final long index = Level.chunkHash(chunkX, chunkZ);
        final FullChunk oldChunk = this.getChunk(chunkX, chunkZ, false);

        if (oldChunk != chunk) {
            if (unload && oldChunk != null) {
                this.unloadChunk(chunkX, chunkZ, false, false);

                this.provider.setChunk(chunkX, chunkZ, chunk);
            } else {
                final Map<Long, Entity> oldEntities = oldChunk != null ? oldChunk.getEntities() : Collections.emptyMap();

                final Map<Long, BlockEntity> oldBlockEntities = oldChunk != null ? oldChunk.getBlockEntities() : Collections.emptyMap();

                if (!oldEntities.isEmpty()) {
                    final Iterator<Map.Entry<Long, Entity>> iter = oldEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        final Map.Entry<Long, Entity> entry = iter.next();
                        final Entity entity = entry.getValue();
                        chunk.addEntity(entity);
                        if (oldChunk != null) {
                            iter.remove();
                            oldChunk.removeEntity(entity);
                            entity.chunk = chunk;
                        }
                    }
                }

                if (!oldBlockEntities.isEmpty()) {
                    final Iterator<Map.Entry<Long, BlockEntity>> iter = oldBlockEntities.entrySet().iterator();
                    while (iter.hasNext()) {
                        final Map.Entry<Long, BlockEntity> entry = iter.next();
                        final BlockEntity blockEntity = entry.getValue();
                        chunk.addBlockEntity(blockEntity);
                        if (oldChunk != null) {
                            iter.remove();
                            oldChunk.removeBlockEntity(blockEntity);
                            blockEntity.chunk = chunk;
                        }
                    }
                }

                this.provider.setChunk(chunkX, chunkZ, chunk);
            }
        }

        chunk.setChanged();

        if (!this.isChunkInUse(index)) {
            this.unloadChunkRequest(chunkX, chunkZ);
        } else {
            for (final ChunkLoader loader : this.getChunkLoaders(chunkX, chunkZ)) {
                loader.onChunkChanged(chunk);
            }
        }
    }

    public int getHighestBlockAt(final int x, final int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHighestBlockAt(x & 0x0f, z & 0x0f);
    }

    public BlockColor getMapColorAt(final int x, final int z) {
        int y = this.getHighestBlockAt(x, z);
        while (y > 1) {
            final Block block = this.getBlock(new Vector3(x, y, z));
            final BlockColor blockColor = block.getColor();
            if (blockColor.getAlpha() == 0x00) {
                y--;
            } else {
                return blockColor;
            }
        }
        return BlockColor.VOID_BLOCK_COLOR;
    }

    public boolean isChunkLoaded(final int x, final int z) {
        return this.provider.isChunkLoaded(x, z);
    }

    public boolean isChunkGenerated(final int x, final int z) {
        final FullChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isGenerated();
    }

    public boolean isChunkPopulated(final int x, final int z) {
        final FullChunk chunk = this.getChunk(x, z);
        return chunk != null && chunk.isPopulated();
    }

    public Position getSpawnLocation() {
        return Position.fromObject(this.provider.getSpawn(), this);
    }

    public void setSpawnLocation(final Vector3 pos) {
        final Position previousSpawn = this.getSpawnLocation();
        this.provider.setSpawn(pos);
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
        final SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        for (final Player p : this.getPlayers().values()) {
            p.dataPacket(pk);
        }
    }

    public void requestChunk(final int x, final int z, final Player player) {
        Preconditions.checkState(player.getLoaderId() > 0, player.getName() + " has no chunk loader");
        final long index = Level.chunkHash(x, z);

        this.chunkSendQueue.putIfAbsent(index, new Int2ObjectOpenHashMap<>());

        this.chunkSendQueue.get(index).put(player.getLoaderId(), player);
    }

    public void chunkRequestCallback(final long timestamp, final int x, final int z, final int subChunkCount, final byte[] payload) {
        this.timings.syncChunkSendTimer.startTiming();
        final long index = Level.chunkHash(x, z);
        if (this.cacheChunks) {
            final BatchPacket data = Player.getChunkCacheFromData(x, z, subChunkCount, payload);
            final BaseFullChunk chunk = this.getChunk(x, z, true);
            if (chunk != null && chunk.getChanges() <= timestamp) {
                chunk.setChunkPacket(data);
            }
            this.sendChunk(x, z, index, data);
            this.timings.syncChunkSendTimer.stopTiming();
            return;
        }
        if (this.chunkSendTasks.contains(index)) {
            for (final Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, subChunkCount, payload);
                }
            }
            this.chunkSendQueue.remove(index);
            this.chunkSendTasks.remove(index);
        }
        this.timings.syncChunkSendTimer.stopTiming();
    }

    public void removeEntity(final Entity entity) {
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

    public void addEntity(final Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.put(entity.getId(), (Player) entity);
        }
        this.entities.put(entity.getId(), entity);
    }

    public void addBlockEntity(final BlockEntity blockEntity) {
        if (blockEntity.getLevel() != this) {
            throw new LevelException("Invalid Block Entity level");
        }
        this.blockEntities.put(blockEntity.getId(), blockEntity);
    }

    public void scheduleBlockEntityUpdate(final BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        if (!this.updateBlockEntities.contains(entity)) {
            this.updateBlockEntities.add(entity);
        }
    }

    public void removeBlockEntity(final BlockEntity entity) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        this.blockEntities.remove(entity.getId());
        this.updateBlockEntities.remove(entity);
    }

    public boolean isChunkInUse(final int x, final int z) {
        return this.isChunkInUse(Level.chunkHash(x, z));
    }

    public boolean isChunkInUse(final long hash) {
        return this.chunkLoaders.containsKey(hash) && !this.chunkLoaders.get(hash).isEmpty();
    }

    public boolean loadChunk(final int x, final int z) {
        return this.loadChunk(x, z, true);
    }

    public boolean loadChunk(final int x, final int z, final boolean generate) {
        final long index = Level.chunkHash(x, z);
        if (this.provider.isChunkLoaded(index)) {
            return true;
        }
        return this.forceLoadChunk(index, x, z, generate) != null;
    }

    public boolean unloadChunkRequest(final int x, final int z) {
        return this.unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(final int x, final int z, final boolean safe) {
        if (safe && this.isChunkInUse(x, z) || this.isSpawnChunk(x, z)) {
            return false;
        }

        this.queueUnloadChunk(x, z);

        return true;
    }

    public void cancelUnloadChunkRequest(final int x, final int z) {
        this.cancelUnloadChunkRequest(Level.chunkHash(x, z));
    }

    public void cancelUnloadChunkRequest(final long hash) {
        this.unloadQueue.remove(hash);
    }

    public boolean unloadChunk(final int x, final int z) {
        return this.unloadChunk(x, z, true);
    }

    public boolean unloadChunk(final int x, final int z, final boolean safe) {
        return this.unloadChunk(x, z, safe, true);
    }

    public synchronized boolean unloadChunk(final int x, final int z, final boolean safe, final boolean trySave) {
        if (safe && this.isChunkInUse(x, z)) {
            return false;
        }

        if (!this.isChunkLoaded(x, z)) {
            return true;
        }

        this.timings.doChunkUnload.startTiming();

        final BaseFullChunk chunk = this.getChunk(x, z);

        if (chunk != null && chunk.getProvider() != null) {
            final ChunkUnloadEvent ev = new ChunkUnloadEvent(chunk);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                this.timings.doChunkUnload.stopTiming();
                return false;
            }
        }

        try {
            if (chunk != null) {
                if (trySave && this.getAutoSave()) {
                    int entities = 0;
                    for (final Entity e : chunk.getEntities().values()) {
                        if (e instanceof Player) {
                            continue;
                        }
                        ++entities;
                    }

                    if (chunk.hasChanged() || !chunk.getBlockEntities().isEmpty() || entities > 0) {
                        this.provider.setChunk(x, z, chunk);
                        this.provider.saveChunk(x, z);
                    }
                }
                for (final ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkUnloaded(chunk);
                }
            }
            this.provider.unloadChunk(x, z, safe);
        } catch (final Exception e) {
            final MainLogger logger = this.server.getLogger();
            logger.error(this.server.getLanguage().translateString("nukkit.level.chunkUnloadError", e.toString()));
            logger.logException(e);
        }

        this.timings.doChunkUnload.stopTiming();

        return true;
    }

    public boolean isSpawnChunk(final int X, final int Z) {
        final Vector3 spawn = this.provider.getSpawn();
        return Math.abs(X - (spawn.getFloorX() >> 4)) <= 1 && Math.abs(Z - (spawn.getFloorZ() >> 4)) <= 1;
    }

    public Position getSafeSpawn() {
        return this.getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3 spawn) {
        if (spawn == null || spawn.y < 1) {
            spawn = this.getSpawnLocation();
        }

        if (spawn != null) {
            final Vector3 v = spawn.floor();
            final FullChunk chunk = this.getChunk((int) v.x >> 4, (int) v.z >> 4, true);
            final int x = (int) v.x & 0x0f;
            final int z = (int) v.z & 0x0f;
            if (chunk != null) {
                int y = (int) NukkitMath.clamp(v.y, 0, 254);
                boolean wasAir = chunk.getBlockId(x, y - 1, z) == 0;
                for (; y > 0; --y) {
                    final int b = chunk.getFullBlock(x, y, z);
                    final Block block = Block.get(b >> 4, b & 0x0f);
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
                    int b = chunk.getFullBlock(x, y + 1, z);
                    Block block = Block.get(b >> 4, b & 0x0f);
                    if (!this.isFullBlock(block)) {
                        b = chunk.getFullBlock(x, y, z);
                        block = Block.get(b >> 4, b & 0x0f);
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
        return (int) this.time;
    }

    public void setTime(final int time) {
        this.time = time;
        this.sendTime();
    }

    public boolean isDaytime() {
        return this.skyLightSubtracted < 4;
    }

    public long getCurrentTick() {
        return this.levelCurrentTick;
    }

    public String getName() {
        return this.provider.getName();
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void stopTime() {
        this.stopTime = true;
        this.sendTime();
    }

    public void startTime() {
        this.stopTime = false;
        this.sendTime();
    }

    public boolean populateChunk(final int x, final int z) {
        return this.populateChunk(x, z, false);
    }

    public boolean populateChunk(final int x, final int z, final boolean force) {
        final long index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.containsKey(index) || this.chunkPopulationQueue.size() >= this.chunkPopulationQueueSize && !force) {
            return false;
        }

        final BaseFullChunk chunk = this.getChunk(x, z, true);
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

                    final PopulationTask task = new PopulationTask(this, chunk);
                    this.server.getScheduler().scheduleAsyncTask(task);
                }
            }
            Timings.populationTimer.stopTiming();
            return false;
        }

        return true;
    }

    public void generateChunk(final int x, final int z) {
        this.generateChunk(x, z, false);
    }

    public void generateChunk(final int x, final int z, final boolean force) {
        if (this.chunkGenerationQueue.size() >= this.chunkGenerationQueueSize && !force) {
            return;
        }

        final long index = Level.chunkHash(x, z);
        if (!this.chunkGenerationQueue.containsKey(index)) {
            Timings.generationTimer.startTiming();
            this.chunkGenerationQueue.put(index, Boolean.TRUE);
            final GenerationTask task = new GenerationTask(this, this.getChunk(x, z, true));
            this.server.getScheduler().scheduleAsyncTask(task);
            Timings.generationTimer.stopTiming();
        }
    }

    public void regenerateChunk(final int x, final int z) {
        this.unloadChunk(x, z, false);

        this.cancelUnloadChunkRequest(x, z);

        final BaseFullChunk chunk = this.provider.getEmptyChunk(x, z);
        this.provider.setChunk(x, z, chunk);

        this.generateChunk(x, z);
    }

    public void doChunkGarbageCollection() {
        this.timings.doChunkGC.startTiming();
        // remove all invaild block entities.
        if (!this.blockEntities.isEmpty()) {
            final ObjectIterator<BlockEntity> iter = this.blockEntities.values().iterator();
            while (iter.hasNext()) {
                final BlockEntity blockEntity = iter.next();
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

        for (final Map.Entry<Long, ? extends FullChunk> entry : this.provider.getLoadedChunks().entrySet()) {
            final long index = entry.getKey();
            if (!this.unloadQueue.containsKey(index)) {
                final FullChunk chunk = entry.getValue();
                final int X = chunk.getX();
                final int Z = chunk.getZ();
                if (!this.isSpawnChunk(X, Z)) {
                    this.unloadChunkRequest(X, Z, true);
                }
            }
        }

        this.provider.doGarbageCollection();
        this.timings.doChunkGC.stopTiming();
    }

    public void doGarbageCollection(long allocatedTime) {
        final long start = System.currentTimeMillis();
        if (this.unloadChunks(start, allocatedTime, false)) {
            allocatedTime = allocatedTime - (System.currentTimeMillis() - start);
            this.provider.doGarbageCollection(allocatedTime);
        }
    }

    public void unloadChunks() {
        this.unloadChunks(false);
    }

    public void unloadChunks(final boolean force) {
        this.unloadChunks(96, force);
    }

    public void unloadChunks(int maxUnload, final boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            final long now = System.currentTimeMillis();

            LongList toRemove = null;
            for (final Long2LongMap.Entry entry : this.unloadQueue.long2LongEntrySet()) {
                final long index = entry.getLongKey();

                if (this.isChunkInUse(index)) {
                    continue;
                }

                if (!force) {
                    final long time = entry.getLongValue();
                    if (maxUnload <= 0) {
                        break;
                    } else if (time > now - 30000) {
                        continue;
                    }
                }

                if (toRemove == null) {
                    toRemove = new LongArrayList();
                }
                toRemove.add(index);
            }

            if (toRemove != null) {
                final int size = toRemove.size();
                for (int i = 0; i < size; i++) {
                    final long index = toRemove.getLong(i);
                    final int X = Level.getHashX(index);
                    final int Z = Level.getHashZ(index);

                    if (this.unloadChunk(X, Z, true)) {
                        this.unloadQueue.remove(index);
                        --maxUnload;
                    }
                }
            }
        }
    }

    @Override
    public void setMetadata(final String metadataKey, final MetadataValue newMetadataValue) {
        this.server.getLevelMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(final String metadataKey) {
        return this.server.getLevelMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(final String metadataKey) {
        return this.server.getLevelMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(final String metadataKey, final Plugin owningPlugin) {
        this.server.getLevelMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public void addPlayerMovement(final Entity entity, final double x, final double y, final double z, final double yaw, final double pitch, final double headYaw) {
        final MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = entity.getId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;

        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    public void addEntityMovement(final Entity entity, final double x, final double y, final double z, final double yaw, final double pitch, final double headYaw) {
        final MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
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

    public boolean setRaining(final boolean raining) {
        final WeatherChangeEvent ev = new WeatherChangeEvent(this, raining);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        this.raining = raining;

        final LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft

        if (raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            final int time = ThreadLocalRandom.current().nextInt(12000) + 12000;
            pk.data = time;
            this.setRainTime(time);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
            this.setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(final int rainTime) {
        this.rainTime = rainTime;
    }

    public boolean isThundering() {
        return this.isRaining() && this.thundering;
    }

    public boolean setThundering(final boolean thundering) {
        final ThunderChangeEvent ev = new ThunderChangeEvent(this, thundering);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        if (thundering && !this.isRaining()) {
            this.setRaining(true);
        }

        this.thundering = thundering;

        final LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft
        if (thundering) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            final int time = ThreadLocalRandom.current().nextInt(12000) + 3600;
            pk.data = time;
            this.setThunderTime(time);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
            this.setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(final int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public void sendWeather(Player[] players) {
        if (players == null) {
            players = this.getPlayers().values().toArray(new Player[0]);
        }

        final LevelEventPacket pk = new LevelEventPacket();

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

    public void sendWeather(final Player player) {
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
        return this.dimension;
    }

    public boolean canBlockSeeSky(final Vector3 pos) {
        return this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY();
    }

    public int getStrongPower(final Vector3 pos, final BlockFace direction) {
        return this.getBlock(pos).getStrongPower(direction);
    }

    public int getStrongPower(final Vector3 pos) {
        int i = 0;

        for (final BlockFace face : BlockFace.values()) {
            i = Math.max(i, this.getStrongPower(pos.getSide(face), face));

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

    public boolean isSidePowered(final Vector3 pos, final BlockFace face) {
        return this.getRedstonePower(pos, face) > 0;
    }

    public int getRedstonePower(final Vector3 pos, final BlockFace face) {
        final Block block = this.getBlock(pos);
        return block.isNormalBlock() ? this.getStrongPower(pos) : block.getWeakPower(face);
    }

    public boolean isBlockPowered(final Vector3 pos) {
        for (final BlockFace face : BlockFace.values()) {
            if (this.getRedstonePower(pos.getSide(face), face) > 0) {
                return true;
            }
        }

        return false;
    }

    public int isBlockIndirectlyGettingPowered(final Vector3 pos) {
        int power = 0;

        for (final BlockFace face : BlockFace.values()) {
            final int blockPower = this.getRedstonePower(pos.getSide(face), face);

            if (blockPower >= 15) {
                return 15;
            }

            if (blockPower > power) {
                power = blockPower;
            }
        }

        return power;
    }

    public boolean isAreaLoaded(final AxisAlignedBB bb) {
        if (bb.getMaxY() < 0 || bb.getMinY() >= 256) {
            return false;
        }
        final int minX = NukkitMath.floorDouble(bb.getMinX()) >> 4;
        final int minZ = NukkitMath.floorDouble(bb.getMinZ()) >> 4;
        final int maxX = NukkitMath.floorDouble(bb.getMaxX()) >> 4;
        final int maxZ = NukkitMath.floorDouble(bb.getMaxZ()) >> 4;

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
        return this.updateLCG = this.updateLCG * 3 ^ Level.LCG_CONSTANT;
    }

    public boolean createPortal(final Block target) {
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
        final int sizeX = sizePosX + sizeNegX + 1;
        final int sizeZ = sizePosZ + sizeNegZ + 1;
        if (sizeX >= 2 && sizeX <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            int scanX = targX;
            final int scanY = targY + 1;
            final int scanZ = targZ;
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
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                final int id = this.getBlockIdAt(scanX - i, scanY, scanZ);
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
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                final int id = this.getBlockIdAt(scanX, scanY + i, scanZ);
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
                && innerHeight >= 3)) {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++) {
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

            for (int height = 0; height < innerHeight; height++) {
                for (int width = 0; width < innerWidth; width++) {
                    this.setBlock(new Vector3(scanX - width, scanY + height, scanZ), Block.get(BlockID.NETHER_PORTAL));
                }
            }

            this.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_IGNITE);
            return true;
        } else if (sizeZ >= 2 && sizeZ <= maxPortalSize) {
            //start scan from 1 block above base
            //find pillar or end of portal to start scan
            final int scanX = targX;
            final int scanY = targY + 1;
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
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                final int id = this.getBlockIdAt(scanX, scanY, scanZ - i);
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
            LOOP:
            for (int i = 0; i < maxPortalSize - 2; i++) {
                final int id = this.getBlockIdAt(scanX, scanY + i, scanZ);
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
                && innerHeight >= 3)) {
                return false;
            }

            for (int height = 0; height < innerHeight + 1; height++) {
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

            for (int height = 0; height < innerHeight; height++) {
                for (int width = 0; width < innerWidth; width++) {
                    this.setBlock(new Vector3(scanX, scanY + height, scanZ - width), Block.get(BlockID.NETHER_PORTAL));
                }
            }

            this.addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_IGNITE);
            return true;
        }

        return false;
    }

    private void performThunder(final long index, final FullChunk chunk) {
        if (this.areNeighboringChunksLoaded(index)) {
            return;
        }
        if (ThreadLocalRandom.current().nextInt(10000) == 0) {
            final int LCG = this.getUpdateLCG() >> 2;

            final int chunkX = chunk.getX() * 16;
            final int chunkZ = chunk.getZ() * 16;
            final Vector3 vector = this.adjustPosToNearbyEntity(new Vector3(chunkX + (LCG & 0xf), 0, chunkZ + (LCG >> 8 & 0xf)));

            final Biome biome = Biome.getBiome(this.getBiomeId(vector.getFloorX(), vector.getFloorZ()));
            if (!biome.canRain()) {
                return;
            }

            final int bId = this.getBlockIdAt(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
            if (bId != BlockID.TALL_GRASS && bId != BlockID.WATER) {
                vector.y += 1;
            }
            final CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", vector.x))
                    .add(new DoubleTag("", vector.y)).add(new DoubleTag("", vector.z)))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                    .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
                    .add(new FloatTag("", 0)));

            final EntityLightning bolt = (EntityLightning) Entity.createEntity("Lightning", chunk, nbt);
            if (bolt == null) {
                return;
            }
            final LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
            this.getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                bolt.spawnToAll();
            } else {
                bolt.setEffect(false);
            }

            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_THUNDER, -1, EntityLightning.NETWORK_ID);
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_EXPLODE, -1, EntityLightning.NETWORK_ID);
        }
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || this.loaders.isEmpty()) {
            this.chunkTickList.clear();
            return;
        }

        final int chunksPerLoader = Math.min(200, Math.max(1, (int) ((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5)));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = randRange > this.chunkTickRadius ? this.chunkTickRadius : randRange;

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        if (!this.loaders.isEmpty()) {
            for (final ChunkLoader loader : this.loaders.values()) {
                final int chunkX = (int) loader.getX() >> 4;
                final int chunkZ = (int) loader.getZ() >> 4;

                final long index = Level.chunkHash(chunkX, chunkZ);
                final int existingLoaders = Math.max(0, this.chunkTickList.getOrDefault(index, 0));
                this.chunkTickList.put(index, existingLoaders + 1);
                for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                    final int dx = random.nextInt(2 * randRange) - randRange;
                    final int dz = random.nextInt(2 * randRange) - randRange;
                    final long hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
                    if (!this.chunkTickList.containsKey(hash) && this.provider.isChunkLoaded(hash)) {
                        this.chunkTickList.put(hash, -1);
                    }
                }
            }
        }

        int blockTest = 0;

        if (!this.chunkTickList.isEmpty()) {
            final ObjectIterator<Long2IntMap.Entry> iter = this.chunkTickList.long2IntEntrySet().iterator();
            while (iter.hasNext()) {
                final Long2IntMap.Entry entry = iter.next();
                final long index = entry.getLongKey();
                if (!this.areNeighboringChunksLoaded(index)) {
                    iter.remove();
                    continue;
                }

                final int loaders = entry.getIntValue();

                final int chunkX = Level.getHashX(index);
                final int chunkZ = Level.getHashZ(index);

                final FullChunk chunk;
                if ((chunk = this.getChunk(chunkX, chunkZ, false)) == null) {
                    iter.remove();
                    continue;
                } else if (loaders <= 0) {
                    iter.remove();
                }

                for (final Entity entity : chunk.getEntities().values()) {
                    entity.scheduleUpdate();
                }
                final int tickSpeed = this.gameRules.getInteger(GameRule.RANDOM_TICK_SPEED);

                if (tickSpeed > 0) {
                    if (this.useSections) {
                        for (final ChunkSection section : ((Chunk) chunk).getSections()) {
                            if (!(section instanceof EmptyChunkSection)) {
                                final int Y = section.getY();
                                for (int i = 0; i < tickSpeed; ++i) {
                                    final int lcg = this.getUpdateLCG();
                                    final int x = lcg & 0x0f;
                                    final int y = lcg >>> 8 & 0x0f;
                                    final int z = lcg >>> 16 & 0x0f;

                                    final int fullId = section.getFullBlock(x, y, z);
                                    final int blockId = fullId >> 4;
                                    if (Level.randomTickBlocks[blockId]) {
                                        final Block block = Block.get(fullId, this, chunkX * 16 + x, (Y << 4) + y, chunkZ * 16 + z);
                                        block.onUpdate(Level.BLOCK_UPDATE_RANDOM);
                                    }
                                }
                            }
                        }
                    } else {
                        for (int Y = 0; Y < 8 && (Y < 3 || blockTest != 0); ++Y) {
                            blockTest = 0;
                            for (int i = 0; i < tickSpeed; ++i) {
                                final int lcg = this.getUpdateLCG();
                                final int x = lcg & 0x0f;
                                final int y = lcg >>> 8 & 0x0f;
                                final int z = lcg >>> 16 & 0x0f;

                                final int fullId = chunk.getFullBlock(x, y + (Y << 4), z);
                                final int blockId = fullId >> 4;
                                blockTest |= fullId;
                                if (Level.randomTickBlocks[blockId]) {
                                    final Block block = Block.get(fullId, this, x, y + (Y << 4), z);
                                    block.onUpdate(Level.BLOCK_UPDATE_RANDOM);
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

    private void computeRemoveBlockLight(final int x, final int y, final int z, final int currentLight, final Queue<Object[]> queue,
                                         final Queue<Long> spreadQueue, final Map<Long, Object> visited, final Map<Long, Object> spreadVisited) {
        final int current = this.getBlockLightAt(x, y, z);
        final long index = Hash.hashBlock(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);
            if (current > 1) {
                if (!visited.containsKey(index)) {
                    visited.put(index, this.changeBlocksPresent);
                    queue.add(new Object[]{Hash.hashBlock(x, y, z), current});
                }
            }
        } else if (current >= currentLight) {
            if (!spreadVisited.containsKey(index)) {
                spreadVisited.put(index, this.changeBlocksPresent);
                spreadQueue.add(Hash.hashBlock(x, y, z));
            }
        }
    }

    private void computeSpreadBlockLight(final int x, final int y, final int z, final int currentLight, final Queue<Long> queue,
                                         final Map<Long, Object> visited) {
        final int current = this.getBlockLightAt(x, y, z);
        final long index = Hash.hashBlock(x, y, z);

        if (current < currentLight - 1) {
            this.setBlockLightAt(x, y, z, currentLight);

            if (!visited.containsKey(index)) {
                visited.put(index, this.changeBlocksPresent);
                if (currentLight > 1) {
                    queue.add(Hash.hashBlock(x, y, z));
                }
            }
        }
    }

    private void addBlockChange(final int x, final int y, final int z) {
        final long index = Level.chunkHash(x >> 4, z >> 4);
        this.addBlockChange(index, x, y, z);
    }

    private void addBlockChange(final long index, final int x, final int y, final int z) {
        synchronized (this.changedBlocks) {
            final SoftReference<Map<Character, Object>> current = this.changedBlocks.computeIfAbsent(index, k -> new SoftReference<>(new HashMap<>()));
            final Map<Character, Object> currentMap = current.get();
            if (currentMap != this.changeBlocksFullMap && currentMap != null) {
                if (currentMap.size() > Level.MAX_BLOCK_CACHE) {
                    this.changedBlocks.put(index, new SoftReference<>(this.changeBlocksFullMap));
                } else {
                    currentMap.put(Level.localBlockHash(x, y, z), this.changeBlocksPresent);
                }
            }
        }
    }

    private boolean areNeighboringChunksLoaded(final long hash) {
        return this.provider.isChunkLoaded(hash + 1) &&
            this.provider.isChunkLoaded(hash - 1) &&
            this.provider.isChunkLoaded(hash + (1L << 32)) &&
            this.provider.isChunkLoaded(hash - (1L << 32));
    }

    private void sendChunk(final int x, final int z, final long index, final DataPacket packet) {
        if (this.chunkSendTasks.contains(index)) {
            for (final Player player : this.chunkSendQueue.get(index).values()) {
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
        for (final long index : this.chunkSendQueue.keySet()) {
            if (this.chunkSendTasks.contains(index)) {
                continue;
            }
            final int x = Level.getHashX(index);
            final int z = Level.getHashZ(index);
            this.chunkSendTasks.add(index);
            final BaseFullChunk chunk = this.getChunk(x, z);
            if (chunk != null) {
                final BatchPacket packet = chunk.getChunkPacket();
                if (packet != null) {
                    this.sendChunk(x, z, index, packet);
                    continue;
                }
            }
            this.timings.syncChunkSendPrepareTimer.startTiming();
            final AsyncTask task = this.provider.requestChunkTask(x, z);
            if (task != null) {
                this.server.getScheduler().scheduleAsyncTask(task);
            }
            this.timings.syncChunkSendPrepareTimer.stopTiming();
        }
        this.timings.syncChunkSendTimer.stopTiming();
    }

    private synchronized BaseFullChunk forceLoadChunk(final long index, final int x, final int z, final boolean generate) {
        this.timings.syncChunkLoadTimer.startTiming();
        final BaseFullChunk chunk = this.provider.getChunk(x, z, generate);
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

        chunk.initChunk();

        if (!chunk.isLightPopulated() && chunk.isPopulated()
            && this.getServer().getConfig("chunk-ticking.light-updates", false)) {
            this.getServer().getScheduler().scheduleAsyncTask(new LightPopulationTask(this, chunk));
        }

        if (this.isChunkInUse(index)) {
            this.unloadQueue.remove(index);
            for (final ChunkLoader loader : this.getChunkLoaders(x, z)) {
                loader.onChunkLoaded(chunk);
            }
        } else {
            this.unloadQueue.put(index, System.currentTimeMillis());
        }
        this.timings.syncChunkLoadTimer.stopTiming();
        return chunk;
    }

    private void queueUnloadChunk(final int x, final int z) {
        final long index = Level.chunkHash(x, z);
        this.unloadQueue.put(index, System.currentTimeMillis());
    }

    /**
     * @param now
     * @param allocatedTime
     * @param force
     * @return true if there is allocated time remaining
     */
    private boolean unloadChunks(final long now, final long allocatedTime, final boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            boolean result = true;
            final int maxIterations = this.unloadQueue.size();

            if (this.lastUnloadIndex > maxIterations) {
                this.lastUnloadIndex = 0;
            }
            ObjectIterator<Long2LongMap.Entry> iter = this.unloadQueue.long2LongEntrySet().iterator();
            if (this.lastUnloadIndex != 0) {
                iter.skip(this.lastUnloadIndex);
            }

            LongList toUnload = null;

            for (int i = 0; i < maxIterations; i++) {
                if (!iter.hasNext()) {
                    iter = this.unloadQueue.long2LongEntrySet().iterator();
                }
                final Long2LongMap.Entry entry = iter.next();

                final long index = entry.getLongKey();

                if (this.isChunkInUse(index)) {
                    continue;
                }

                if (!force) {
                    final long time = entry.getLongValue();
                    if (time > now - 30000) {
                        continue;
                    }
                }

                if (toUnload == null) {
                    toUnload = new LongArrayList();
                }
                toUnload.add(index);
            }

            if (toUnload != null) {
                final long[] arr = toUnload.toLongArray();
                for (final long index : arr) {
                    final int X = Level.getHashX(index);
                    final int Z = Level.getHashZ(index);
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
//    public List<Entity> orderChunkEntitiesForSpawn(WoolFullChunk chunk) {
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
