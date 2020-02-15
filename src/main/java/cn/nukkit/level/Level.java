package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockRedstoneDiode;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.impl.projectile.EntityArrow;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.entity.misc.XpOrb;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkSection;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.GeneratorFactory;
import cn.nukkit.level.manager.LevelChunkManager;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.math.*;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.registry.GeneratorRegistry;
import cn.nukkit.registry.RegistryException;
import cn.nukkit.scheduler.BlockUpdateScheduler;
import cn.nukkit.timings.LevelTimings;
import cn.nukkit.utils.*;
import co.aikar.timings.Timing;
import co.aikar.timings.TimingsHistory;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static cn.nukkit.block.BlockIds.*;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * author: MagicDroidX Nukkit Project
 */
@Log4j2
public class Level implements ChunkManager, Metadatable {

    private static int levelIdCounter = 1;
    private static int chunkLoaderCounter = 1;
    public static int COMPRESSION_LEVEL = 8;

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
    private static final Set<Identifier> randomTickBlocks = Collections.newSetFromMap(new IdentityHashMap<>());

    private static final ThreadLocal<Vector3f> threadLocalVector = ThreadLocal.withInitial(Vector3f::new);

    static {
        randomTickBlocks.add(GRASS);
        randomTickBlocks.add(FARMLAND);
        randomTickBlocks.add(MYCELIUM);
        randomTickBlocks.add(SAPLING);
        randomTickBlocks.add(LEAVES);
        randomTickBlocks.add(LEAVES2);
        randomTickBlocks.add(SNOW_LAYER);
        randomTickBlocks.add(ICE);
        randomTickBlocks.add(FLOWING_LAVA);
        randomTickBlocks.add(LAVA);
        randomTickBlocks.add(CACTUS);
        randomTickBlocks.add(BEETROOT);
        randomTickBlocks.add(CARROTS);
        randomTickBlocks.add(POTATOES);
        randomTickBlocks.add(MELON_STEM);
        randomTickBlocks.add(PUMPKIN_STEM);
        randomTickBlocks.add(WHEAT);
        randomTickBlocks.add(REEDS);
        randomTickBlocks.add(RED_MUSHROOM);
        randomTickBlocks.add(BROWN_MUSHROOM);
        randomTickBlocks.add(NETHER_WART_BLOCK);
        randomTickBlocks.add(FIRE);
        randomTickBlocks.add(LIT_REDSTONE_ORE);
        randomTickBlocks.add(COCOA);
    }

    private final Long2ObjectOpenHashMap<BlockEntity> blockEntities = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Player> players = new Long2ObjectOpenHashMap<>();

    private final Long2ObjectOpenHashMap<Entity> entities = new Long2ObjectOpenHashMap<>();
    private static final RemovalListener<Long, ByteBuf> cacheRemover = notification -> notification.getValue().release();

    private final ConcurrentLinkedQueue<BlockEntity> updateBlockEntities = new ConcurrentLinkedQueue<>();

    private final Server server;
    public final LevelTimings timings;

    private LevelProvider provider;

    private final Int2ObjectOpenHashMap<ChunkLoader> loaders = new Int2ObjectOpenHashMap<>();

    private final Int2IntMap loaderCounter = new Int2IntOpenHashMap();
    private final Set<Entity> updateEntities = ConcurrentHashMap.newKeySet();

    private final Long2ObjectOpenHashMap<Deque<DataPacket>> chunkPackets = new Long2ObjectOpenHashMap<>();

    public float skyLightSubtracted;
    // Avoid OOM, gc'd references result in whole chunk being sent (possibly higher cpu)
    private final Cache<Long, IntSet> changedBlocks = CacheBuilder.newBuilder().softValues().build();
    //    private final Long2ObjectOpenHashMap<SoftReference<Map<Character, Object>>> changedBlocks = new Long2ObjectOpenHashMap<>();
    // Storing the vector is redundant
    private final Object changeBlocksPresent = new Object();
    private final Long2ObjectMap<ShortSet> lightQueue = new Long2ObjectOpenHashMap<>();
    // Storing extra blocks past 512 is redundant
    private final Map<Character, Object> changeBlocksFullMap = new HashMap<Character, Object>() {
        @Override
        public int size() {
            return Character.MAX_VALUE;
        }
    };


    private final BlockUpdateScheduler updateQueue;
    private final Queue<Block> normalUpdateQueue = new ConcurrentLinkedDeque<>();
//    private final TreeSet<BlockUpdateEntry> updateQueue = new TreeSet<>();
//    private final List<BlockUpdateEntry> nextTickUpdates = Lists.newArrayList();
    //private final Map<BlockVector3, Integer> updateQueueIndex = new HashMap<>();

    private final ConcurrentMap<Long, Int2ObjectMap<Player>> chunkSendQueue = new ConcurrentHashMap<>();
    private final LongSet chunkSendTasks = new LongOpenHashSet();

    private boolean autoSave;

    private BlockMetadataStore blockMetadata;

    private Position temporalPosition;

    public int sleepTicks = 0;

    private int chunkTickRadius;
    private final Long2IntMap chunkTickList = new Long2IntOpenHashMap();
    private int chunksPerTicks;
    private boolean clearChunksOnTick;

    private int updateLCG = ThreadLocalRandom.current().nextInt();

    private static final int LCG_CONSTANT = 1013904223;
    private final String id;

    private int tickRate;
    public int tickRateTime = 0;
    public int tickRateCounter = 0;

    private GeneratorFactory generatorFactory;
    private final Long2ObjectOpenHashMap<Set<Player>> chunkPlayers = new Long2ObjectOpenHashMap<>();
    private final Cache<Long, ByteBuf> chunkCache = CacheBuilder.newBuilder()
            .softValues()
            .removalListener(cacheRemover)
            .build();
    private final LevelChunkManager chunkManager;
    private final LevelData levelData;
    private ThreadLocal<Generator> generators = new ThreadLocal<Generator>() {
        @Override
        protected Generator initialValue() {
            try {
                BedrockRandom random = new BedrockRandom((int) getSeed());
                return generatorFactory.create(random, levelData.getGeneratorOptions());
            } catch (Throwable e) {
                throw new IllegalStateException("Unable to initialize generator", e);
            }
        }
    };

    Level(Server server, String id, LevelProvider levelProvider, LevelData levelData) {
        this.id = id;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();
        this.provider = levelProvider;
        this.levelData = levelData;
        this.timings = new LevelTimings(this);

//        try {
//            if (fullConvert) {
//                String newPath = new File(path).getParent() + "/" + name + ".old/";
//                new File(path).renameTo(new File(newPath));
//                this.chunkProvider = chunkProvider.getConstructor(Level.class, String.class).newInstance(this, newPath);
//            } else {
//                this.chunkProvider = chunkProvider.getConstructor(Level.class, String.class).newInstance(this, path);
//            }
//        } catch (Exception e) {
//            throw new LevelException("Caused by " + Utils.getExceptionMessage(e));
//        }
//
//        this.timings = new LevelTimings(this);
//
//        if (fullConvert) {
//            this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.updating",
//                    TextFormat.GREEN + this.chunkProvider.getName() + TextFormat.WHITE));
//            LevelChunkProvider old = this.chunkProvider;
//            try {
//                this.chunkProvider = new LevelProviderConverter(this, path)
//                        .from(old)
//                        .to(AnvilChunkProvider.class)
//                        .perform();
//                old.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        log.info(this.server.getLanguage().translateString("nukkit.level.preparing",
                TextFormat.GREEN + getId() + TextFormat.WHITE));

        this.generatorFactory = GeneratorRegistry.get().getGeneratorFactory(this.levelData.getGenerator());

        if (this.levelData.getRainTime() <= 0) {
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        if (this.levelData.getLightningTime() <= 0) {
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        this.updateQueue = new BlockUpdateScheduler(this, this.levelData.getCurrentTick());

        this.chunkTickRadius = Math.min(this.server.getViewDistance(),
                Math.max(1, this.server.getConfig("chunk-ticking.tick-radius", 4)));
        this.chunksPerTicks = this.server.getConfig("chunk-ticking.per-tick", 40);
        this.chunkTickList.clear();
        this.clearChunksOnTick = this.server.getConfig("chunk-ticking.clear-tick-list", true);
        this.temporalPosition = new Position(0, 0, 0, this);
        this.tickRate = 1;
        this.chunkManager = new LevelChunkManager(this);

        this.skyLightSubtracted = this.calculateSkylightSubtracted(1);
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

    public void init() {
        Generator generator = generators.get();
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

    public final String getId() {
        return this.id;
    }

    public void close() {
        if (this.getAutoSave()) {
            this.save(true, true);
        }

        try {
            this.provider.close();
        } catch (IOException e) {
            throw new LevelException("Error occurred whilst closing level", e);
        }
        this.provider = null;
        this.blockMetadata = null;
        this.temporalPosition = null;
    }

    private Vector3f mutableBlock;

    public void addSound(Vector3f pos, Sound sound) {
        this.addSound(pos, sound, 1, 1, (Player[]) null);
    }

    public void addSound(Vector3f pos, Sound sound, float volume, float pitch) {
        this.addSound(pos, sound, volume, pitch, (Player[]) null);
    }

    public void addSound(Vector3f pos, Sound sound, float volume, float pitch, Collection<Player> players) {
        this.addSound(pos, sound, volume, pitch, players.toArray(new Player[0]));
    }

    public void addSound(Vector3f pos, Sound sound, float volume, float pitch, Player... players) {
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
            addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
        } else {
            Server.broadcastPacket(players, packet);
        }
    }

    public void addLevelSoundEvent(Vector3f pos, int event, int data, EntityType<?> type) {
        addLevelSoundEvent(pos, event, data, type, false, false);
    }

    public void addLevelSoundEvent(Vector3f pos, int event, int data, EntityType<?> type, boolean isBaby, boolean isGlobal) {
        addLevelSoundEvent(pos, event, data, type.getIdentifier(), isBaby, isGlobal);
    }

    public void addLevelSoundEvent(Vector3i pos, int type) {
        this.addLevelSoundEvent(pos.add(0.5, 0.5, 0.5), type);
    }

    public void addLevelSoundEvent(Vector3f pos, int type) {
        this.addLevelSoundEvent(pos, type, -1);
    }

    /**
     * Broadcasts sound to players
     *
     * @param pos  position where sound should be played
     * @param type ID of the sound from {@link cn.nukkit.network.protocol.LevelSoundEventPacket}
     * @param data generic data that can affect sound
     */
    public void addLevelSoundEvent(Vector3f pos, int type, int data) {
        this.addLevelSoundEvent(pos, type, data, Identifier.EMPTY, false, false);
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
                Server.broadcastPackets(players, packets);
            }
        }
    }

    public void addParticle(Particle particle, Collection<Player> players) {
        this.addParticle(particle, players.toArray(new Player[0]));
    }

    public void addParticleEffect(Vector3f pos, Identifier identifier) {
        this.addParticleEffect(pos, identifier, -1, this.levelData.getDimension(), (Player[]) null);
    }

    public void addParticleEffect(Vector3f pos, Identifier identifier, long uniqueEntityId) {
        this.addParticleEffect(pos, identifier, uniqueEntityId, this.levelData.getDimension(), (Player[]) null);
    }

    public void addParticleEffect(Vector3f pos, Identifier identifier, long uniqueEntityId, int dimensionId) {
        this.addParticleEffect(pos, identifier, uniqueEntityId, dimensionId, (Player[]) null);
    }

    public void addParticleEffect(Vector3f pos, Identifier identifier, long uniqueEntityId, int dimensionId, Collection<Player> players) {
        this.addParticleEffect(pos, identifier, uniqueEntityId, dimensionId, players.toArray(new Player[0]));
    }

    public void addParticleEffect(Vector3f pos, Identifier identifier, long uniqueEntityId, int dimensionId, Player... players) {
        SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
        pk.identifier = identifier.toString();
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

    public Set<Player> getChunkPlayers(int chunkX, int chunkZ) {
        Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
        return chunk == null ? ImmutableSet.of() : chunk.getPlayerLoaders();
    }

    public Set<ChunkLoader> getChunkLoaders(int chunkX, int chunkZ) {
        Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
        return chunk == null ? ImmutableSet.of() : chunk.getLoaders();
    }

    public void addLevelSoundEvent(Vector3f pos, int event, int data, Identifier identifier, boolean isBaby, boolean isGlobal) {
        LevelSoundEventPacket packet = new LevelSoundEventPacket();
        packet.event = event;
        packet.data = data;
        packet.identifier = identifier;
        packet.x = (float) pos.x;
        packet.y = (float) pos.y;
        packet.z = (float) pos.z;
        packet.isGlobal = isGlobal;
        packet.isBabyMob = isBaby;

        this.addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
    }

    public void checkTime() {
        this.levelData.checkTime(this.tickRate);
    }

    private boolean doDaylightCycle() {
        return this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
    }

    private boolean doWeatherCycle() {
        return this.getGameRules().get(GameRules.DO_WEATHER_CYCLE);
    }

    public void sendTime(Player... players) {
        /*if (this.stopTime) { //TODO
            SetTimePacket pk0 = new SetTimePacket();
            pk0.time = (int) this.time;
            player.dataPacket(pk0);
        }*/

        SetTimePacket pk = new SetTimePacket();
        pk.time = this.getTime();

        Server.broadcastPacket(players, pk);
    }

    public void sendTime() {
        sendTime(this.players.values().toArray(new Player[0]));
    }

    public GameRuleMap getGameRules() {
        return this.levelData.getGameRules();
    }

    public void addChunkPacket(Vector3f pos, DataPacket packet) {
        addChunkPacket(pos.getChunkX(), pos.getChunkZ(), packet);
    }

    public void addChunkPacket(int chunkX, int chunkZ, DataPacket packet) {
        long index = Chunk.key(chunkX, chunkZ);
        synchronized (chunkPackets) {
            Deque<DataPacket> packets = chunkPackets.computeIfAbsent(index, i -> new ArrayDeque<>());
            packets.add(packet);
        }
    }

    public void doTick(int currentTick) {
        try (Timing ignored = this.timings.doTick.startTiming()) {
            synchronized (lightQueue) {
                updateBlockLight(lightQueue);
            }
            this.checkTime();

            if (currentTick % 600 == 0 && doDaylightCycle()) {
                this.sendTime();
            }

            // Tick Weather
            if (this.doWeatherCycle()) {
                this.levelData.setRainTime(getRainTime() - 1);
                if (this.levelData.getRainTime() <= 0) {
                    if (!this.setRaining(this.levelData.getRainLevel() <= 0)) {
                        if (this.levelData.getRainLevel() > 0) {
                            setRainTime(ThreadLocalRandom.current().nextInt(12000) + 12000);
                        } else {
                            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                        }
                    }
                }

                this.levelData.setLightningTime(this.levelData.getLightningTime() - 1);
                if (this.levelData.getLightningTime() <= 0) {
                    if (!this.setThundering(this.levelData.getLightningLevel() <= 0)) {
                        if (this.levelData.getLightningLevel() > 0) {
                            setThunderTime(ThreadLocalRandom.current().nextInt(12000) + 3600);
                        } else {
                            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
                        }
                    }
                }

                if (this.isThundering()) {
                    for (Chunk chunk : this.getChunks()) {
                        this.performThunder(chunk);
                    }
                }
            }

            this.skyLightSubtracted = this.calculateSkylightSubtracted(1);

            this.levelData.tick();

            int polled = 0;

            try (Timing ignored2 = timings.doTickPending.startTiming()) {
                this.updateQueue.tick(this.getCurrentTick());
            }

            Block block;
            while ((block = this.normalUpdateQueue.poll()) != null) {
                block.onUpdate(BLOCK_UPDATE_NORMAL);
            }

            TimingsHistory.entityTicks += this.updateEntities.size();


            try (Timing ignored2 = this.timings.entityTick.startTiming()) {
                if (!this.updateEntities.isEmpty()) {
                    this.updateEntities.removeIf(entity -> entity.isClosed() || !entity.onUpdate(currentTick));
                }
            }

            TimingsHistory.tileEntityTicks += this.updateBlockEntities.size();
            try (Timing ignored2 = this.timings.blockEntityTick.startTiming()) {
                this.updateBlockEntities.removeIf(blockEntity -> !blockEntity.isValid() || !blockEntity.onUpdate());
            }

            try (Timing ignored2 = this.timings.tickChunks.startTiming()) {
                try (Timing ignored3 = this.timings.tickChunks.startTiming()) {
                    this.tickChunks();
                }

                synchronized (changedBlocks) {
                    ConcurrentMap<Long, IntSet> changedBlocks = this.changedBlocks.asMap();
                    if (!changedBlocks.isEmpty()) {
                        if (!this.players.isEmpty()) {
                            Iterator<Map.Entry<Long, IntSet>> iter = changedBlocks.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry<Long, IntSet> entry = iter.next();
                                long chunkKey = entry.getKey();
                                IntSet blocks = entry.getValue();
                                int chunkX = Chunk.fromKeyX(chunkKey);
                                int chunkZ = Chunk.fromKeyZ(chunkKey);
                                if (blocks.size() > MAX_BLOCK_CACHE) {
                                    Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
                                    if (chunk != null) {
                                        for (Player p : this.getChunkPlayers(chunkX, chunkZ)) {
                                            p.onChunkChanged(chunk);
                                        }
                                    }
                                } else {
                                    Collection<Player> toSend = this.getChunkPlayers(chunkX, chunkZ);
                                    Player[] playerArray = toSend.toArray(new Player[0]);
                                    BlockPosition[] blocksArray = new BlockPosition[blocks.size()];
                                    int i = 0;
                                    for (int blockKey : blocks) {
                                        blocksArray[i++] = Chunk.fromKey(chunkKey, blockKey);
                                    }
                                    this.sendBlocks(playerArray, blocksArray, UpdateBlockPacket.FLAG_ALL);
                                }
                                iter.remove();
                            }
                        }
                    }
                }

                //this.processChunkRequest();

                if (this.sleepTicks > 0 && --this.sleepTicks <= 0) {
                    this.checkSleep();
                }

                synchronized (chunkPackets) {
                    for (long index : this.chunkPackets.keySet()) {
                        int chunkX = Chunk.fromKeyX(index);
                        int chunkZ = Chunk.fromKeyZ(index);
                        Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);

                        Set<Player> playerLoaders;
                        if (chunk == null || (playerLoaders = chunk.getPlayerLoaders()).isEmpty()) {
                            // Chunk is unloaded.
                            continue;
                        }

                        for (DataPacket packet : this.chunkPackets.get(index)) {
                            Server.broadcastPacket(playerLoaders, packet);
                        }
                    }
                    this.chunkPackets.clear();
                }

                if (this.levelData.getGameRules().isDirty()) {
                    GameRulesChangedPacket packet = new GameRulesChangedPacket();
                    packet.gameRules = this.levelData.getGameRules();
                    Server.broadcastPacket(players.values().toArray(new Player[0]), packet);
                    this.levelData.getGameRules().refresh();
                }
            }
        }
    }

    private void performThunder(Chunk chunk) {
        if (areNeighboringChunksLoaded(Chunk.key(chunk.getX(), chunk.getZ()))) return;
        if (ThreadLocalRandom.current().nextInt(10000) == 0) {
            int LCG = this.getUpdateLCG() >> 2;

            int chunkX = chunk.getX() * 16;
            int chunkZ = chunk.getZ() * 16;
            Vector3f vector = this.adjustPosToNearbyEntity(new Vector3f(chunkX + (LCG & 0xf), 0, chunkZ + (LCG >> 8 & 0xf)));

            Identifier blockId = chunk.getBlockId(vector.getFloorX() & 0xf, vector.getFloorY(), vector.getFloorZ() & 0xf);
            if (blockId != TALL_GRASS && blockId != FLOWING_WATER)
                vector.y += 1;
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", vector.x))
                            .add(new DoubleTag("", vector.y)).add(new DoubleTag("", vector.z)))
                    .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)));

            LightningBolt bolt = EntityRegistry.get().newEntity(EntityTypes.LIGHTNING_BOLT, chunk, nbt);
            LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
            getServer().getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                bolt.spawnToAll();
            } else {
                bolt.setEffect(false);
            }

            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_THUNDER, -1, EntityTypes.LIGHTNING_BOLT);
            this.addLevelSoundEvent(vector, LevelSoundEventPacket.SOUND_EXPLODE, -1, EntityTypes.LIGHTNING_BOLT);
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
        this.sendBlockExtraData(x, y, z, id, data, this.getChunkPlayers(x >> 4, z >> 4));
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Collection<Player> players) {
        sendBlockExtraData(x, y, z, id, data, players.toArray(new Player[0]));
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

    public Vector3f adjustPosToNearbyEntity(Vector3f pos) {
        pos.y = this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ());
        AxisAlignedBB axisalignedbb = new SimpleAxisAlignedBB(pos.x, pos.y, pos.z, pos.getX(), 255, pos.getZ()).expand(3, 3, 3);
        List<Entity> list = new ArrayList<>();

        for (Entity entity : this.getCollidingEntities(axisalignedbb)) {
            if (entity.isAlive() && canBlockSeeSky(entity.getPosition().asVector3i())) {
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

    public void sendBlocks(Player[] target, BlockPosition[] blocks) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE);
    }

    public void sendBlocks(Player[] target, BlockPosition[] blocks, int flags) {
        this.sendBlocks(target, blocks, flags, false);
    }

    public void sendBlocks(Player[] target, BlockPosition[] positions, int flags, boolean optimizeRebuilds) {
        for (BlockPosition position : positions) {
            if (position == null) throw new NullPointerException("Null position is update array");
        }
        UpdateBlockPacket[] packets = new UpdateBlockPacket[positions.length];
        LongSet chunks = null;
        if (optimizeRebuilds) {
            chunks = new LongOpenHashSet();
        }
        for (int i = 0; i < positions.length; i++) {
            boolean first = !optimizeRebuilds;

            BlockPosition position = positions[i];
            int chunkX = position.getChunkX();
            int chunkZ = position.getChunkZ();

            if (optimizeRebuilds) {
                long index = Chunk.key(chunkX, chunkZ);
                if (!chunks.contains(index)) {
                    chunks.add(index);
                    first = true;
                }
            }

            UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
            updateBlockPacket.x = position.x;
            updateBlockPacket.y = position.y;
            updateBlockPacket.z = position.z;
            updateBlockPacket.flags = first ? flags : UpdateBlockPacket.FLAG_NONE;
            updateBlockPacket.dataLayer = position.getLayer();

            Block block = position instanceof Block ? (Block) position : getBlock(position.x, position.y, position.z, position.getLayer());

            try {
                updateBlockPacket.blockRuntimeId = BlockRegistry.get().getRuntimeId(block);
            } catch (RegistryException e) {
                throw new IllegalStateException("Unable to create BlockUpdatePacket at (" +
                        position.x + ", " + position.y + ", " + position.z + ") in " + getName(), e);
            }
            packets[i] = updateBlockPacket;
        }
        Server.broadcastPackets(target, packets);
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(boolean force) {
        return this.save(force, false);
    }

    private boolean save(boolean force, boolean sync) {
        if (!this.getAutoSave() && !force) {
            return false;
        }

        this.server.getPluginManager().callEvent(new LevelSaveEvent(this));

        CompletableFuture<Void> chunksFuture = this.saveChunks();
        CompletableFuture<Void> dataFuture = this.provider.saveLevelData(this.levelData);

        if (sync) {
            chunksFuture.join();
            dataFuture.join();
        }

        return true;
    }

    public CompletableFuture<Void> saveChunks() {
        return this.chunkManager.saveChunks();
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || this.players.isEmpty()) {
            this.chunkTickList.clear();
            return;
        }

        int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.players.size()) / this.players.size() + 0.5))));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = Math.min(randRange, this.chunkTickRadius);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (!this.loaders.isEmpty()) {
            for (ChunkLoader loader : this.loaders.values()) {
                int chunkX = (int) loader.getX() >> 4;
                int chunkZ = (int) loader.getZ() >> 4;

                long index = Chunk.key(chunkX, chunkZ);
                int existingLoaders = Math.max(0, this.chunkTickList.getOrDefault(index, 0));
                this.chunkTickList.put(index, existingLoaders + 1);
                for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                    int dx = random.nextInt(2 * randRange) - randRange;
                    int dz = random.nextInt(2 * randRange) - randRange;
                    long hash = Chunk.key(dx + chunkX, dz + chunkZ);
                    if (!this.chunkTickList.containsKey(hash) && this.chunkManager.isChunkLoaded(hash)) {
                        this.chunkTickList.put(hash, -1);
                    }
                }
            }
        }

        int blockTest = 0;

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

                int chunkX = Chunk.fromKeyX(index);
                int chunkZ = Chunk.fromKeyZ(index);

                Chunk chunk;
                if ((chunk = this.getLoadedChunk(chunkX, chunkZ)) == null) {
                    iter.remove();
                    continue;
                } else if (loaders <= 0) {
                    iter.remove();
                }

                chunk.getEntities().forEach(this::scheduleEntityUpdate);

                int tickSpeed = getGameRules().get(GameRules.RANDOM_TICK_SPEED);

                if (tickSpeed > 0) {
                    ChunkSection[] sections = chunk.getSections();
                    for (int sectionY = 0; sectionY < sections.length; sectionY++) {
                        ChunkSection section = sections[sectionY];
                        if (section != null) {
                            for (int i = 0; i < tickSpeed; ++i) {
                                int lcg = this.getUpdateLCG();
                                int x = lcg & 0x0f;
                                int y = lcg >>> 8 & 0x0f;
                                int z = lcg >>> 16 & 0x0f;

                                Identifier blockId = section.getBlockId(x, y, z, 0);
                                int blockData = section.getBlockData(x, y, z, 0);
                                if (randomTickBlocks.contains(blockId)) {
                                    Block block = Block.get(blockId, blockData, this, chunkX * 16 + x,
                                            (sectionY << 4) + y, chunkZ * 16 + z);
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

    public void updateAroundRedstone(Vector3i pos, BlockFace face) {
        for (BlockFace side : BlockFace.values()) {
            if (face != null && side == face) {
                continue;
            }

            this.getBlock(pos.getSide(side)).onUpdate(BLOCK_UPDATE_REDSTONE);
        }
    }

    public void updateComparatorOutputLevel(BlockPosition v) {
        for (BlockFace face : Plane.HORIZONTAL) {
            BlockPosition pos = v.getSide(face);

            if (this.isChunkLoaded(pos.getChunkX(), pos.getChunkZ())) {
                Block block1 = this.getBlock(pos);

                if (BlockRedstoneDiode.isDiode(block1)) {
                    block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                } else if (block1.isNormalBlock()) {
                    pos = pos.getSide(face);
                    block1 = this.getBlock(pos);

                    if (BlockRedstoneDiode.isDiode(block1)) {
                        block1.onUpdate(BLOCK_UPDATE_REDSTONE);
                    }
                }
            }
        }
    }

    public void updateAround(int x, int y, int z) {
        updateAround(x, y, z, 0);
    }

    public void updateAround(int posX, int posY, int posZ, int layer) {
        BlockUpdateEvent ev;
        Block block;

        for (int x = posX - 1; x <= posX + 1; x++) {
            for (int y = posY - 1; y <= posY + 1; y++) {
                for (int z = posZ - 1; z <= posZ + 1; z++) {
                    for (int l = 0; l < 2; l++) {
                        if (x == posX && y == posY && z == posZ && l == layer) continue;
                        block = this.getBlock(x, y, z, l);
                        if (block.getId() != AIR) {
                            this.getServer().getPluginManager().callEvent(
                                    ev = new BlockUpdateEvent(block));
                            if (!ev.isCancelled()) {
                                normalUpdateQueue.add(block);
                            }
                        }
                    }
                }
            }
        }
    }

    public void scheduleUpdate(Block pos, int delay) {
        this.scheduleUpdate(pos, pos, delay, 0, true);
    }

    public void updateAround(Vector3i pos) {
        updateAround(pos.x, pos.y, pos.z);
    }

    public void scheduleUpdate(Block block, BlockPosition pos, int delay) {
        this.scheduleUpdate(block, pos, delay, 0, true);
    }

    public void scheduleUpdate(BlockUpdate blockUpdate) {
        this.scheduleUpdate(blockUpdate.getBlock(), blockUpdate.getPos(), blockUpdate.getDelay(),
                blockUpdate.getPriority(), blockUpdate.shouldCheckArea());
    }

    public void scheduleUpdate(Block block, BlockPosition pos, int delay, int priority) {
        this.scheduleUpdate(block, pos, delay, priority, true);
    }

    public void scheduleUpdate(Block block, BlockPosition pos, int delay, int priority, boolean checkArea) {
        if (block.getId() == AIR || (checkArea && !this.isChunkLoaded(block.getChunkX(), block.getChunkZ()))) {
            return;
        }

        BlockUpdateEntry entry = new BlockUpdateEntry(pos, block, ((long) delay) + getCurrentTick(), priority);

        if (!this.updateQueue.contains(entry)) {
            this.updateQueue.add(entry);
        }
    }

    public boolean cancelSheduledUpdate(BlockPosition pos, Block block) {
        return this.updateQueue.remove(new BlockUpdateEntry(pos, block));
    }

    public boolean isUpdateScheduled(BlockPosition pos, Block block) {
        return this.updateQueue.contains(new BlockUpdateEntry(pos, block));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(Chunk chunk) {
        int minX = (chunk.getX() << 4) - 2;
        int maxX = minX + 16 + 2;
        int minZ = (chunk.getZ() << 4) - 2;
        int maxZ = minZ + 16 + 2;

        return this.getPendingBlockUpdates(new SimpleAxisAlignedBB(minX, 0, minZ, maxX, 256, maxZ));
    }

    public Set<BlockUpdateEntry> getPendingBlockUpdates(AxisAlignedBB boundingBox) {
        return updateQueue.getPendingBlockUpdates(boundingBox);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
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
                        Block block = this.getLoadedBlock(x, y, z);
                        if (block != null && block.getId() != AIR && block.collidesWithBB(bb)) {
                            return new Block[]{block};
                        }
                    }
                }
            }
        } else {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getLoadedBlock(x, y, z);
                        if (block != null && block.getId() != AIR && block.collidesWithBB(bb)) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.toArray(new Block[0]);
    }

    public boolean isBlockTickPending(BlockPosition pos, Block block) {
        return this.updateQueue.isBlockTickPending(pos, block);
    }

    public AxisAlignedBB[] getCollisionCubes(BaseEntity entity, AxisAlignedBB bb) {
        return this.getCollisionCubes(entity, bb, true);
    }

    public AxisAlignedBB[] getCollisionCubes(BaseEntity entity, AxisAlignedBB bb, boolean entities) {
        return getCollisionCubes(entity, bb, entities, false);
    }

    public AxisAlignedBB[] getCollisionCubes(BaseEntity entity, AxisAlignedBB bb, boolean entities, boolean solidEntities) {
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
                    Block block = this.getLoadedBlock(x, y, z);
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities || solidEntities) {
            for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                if (solidEntities && !ent.canPassThrough()) {
                    collides.add(ent.getBoundingBox().clone());
                }
            }
        }

        return collides.toArray(new AxisAlignedBB[0]);
    }

    public boolean isFullBlock(BlockPosition pos) {
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

    public boolean hasCollision(BaseEntity entity, AxisAlignedBB bb, boolean entities) {
        int minX = NukkitMath.floorDouble(bb.getMinX());
        int minY = NukkitMath.floorDouble(bb.getMinY());
        int minZ = NukkitMath.floorDouble(bb.getMinZ());
        int maxX = NukkitMath.ceilDouble(bb.getMaxX());
        int maxY = NukkitMath.ceilDouble(bb.getMaxY());
        int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());

        Vector3f vector3 = threadLocalVector.get();
        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getLoadedBlock(vector3.setComponents(x, y, z));
                    // Shouldn't walk into unloaded chunks.
                    if (block == null || !block.canPassThrough() && block.collidesWithBB(bb)) {
                        return true;
                    }
                }
            }
        }

        if (entities) {
            return !this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity).isEmpty();
        }
        return false;
    }

    public int calculateSkylightSubtracted(float tickDiff) {
        float angle = this.calculateCelestialAngle(getTime(), tickDiff);
        float light = 1 - (MathHelper.cos(angle * ((float) Math.PI * 2F)) * 2 + 0.5f);
        light = light < 0 ? 0 : light > 1 ? 1 : light;
        light = 1 - light;
        light = (float) ((double) light * ((isRaining() ? 1 : 0) - (double) 5f / 16d));
        light = (float) ((double) light * ((isThundering() ? 1 : 0) - (double) 5f / 16d));
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

    public int getFullLight(Vector3i pos) {
        Chunk chunk = this.getChunk(pos.getChunkX(), pos.getChunkZ());
        int level = 0;
        if (chunk != null) {
            level = chunk.getSkyLight(pos.x & 0x0f, pos.y & 0xff, pos.z & 0x0f);
            level -= this.skyLightSubtracted;

            if (level < 15) {
                level = Math.max(chunk.getBlockLight(pos.x & 0x0f, pos.y & 0xff, pos.z & 0x0f),
                        level);
            }
        }

        return level;
    }

    @Nullable
    public Block getLoadedBlock(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (y < 0 || y > 255) {
            return Block.get(BlockIds.AIR, 0, this, x, y, z);
        }

        Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
        if (chunk == null) {
            return null;
        }
        return chunk.getBlock(x & 0xf, y, z & 0xf);
    }

    @Nullable
    public Block getLoadedBlock(Vector3f pos) {
        return this.getLoadedBlock(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    @Nonnull
    public Block getBlock(Vector3i vector) {
        return this.getBlock(vector.x, vector.y, vector.z);
    }

    @Nonnull
    public Block getBlock(BlockPosition pos) {
        return this.getBlock(pos.getX(), pos.getY(), pos.getZ(), pos.getLayer());
    }

    @Nonnull
    public Block getBlock(int x, int y, int z) {
        return getBlock(x, y, z, 0);
    }

    @Nonnull
    public Block getBlock(int x, int y, int z, int layer) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (y < 0 || y > 255) {
            return Block.get(BlockIds.AIR, 0, this, x, y, z);
        }

        return this.getChunk(chunkX, chunkZ).getBlock(x & 0xf, y, z & 0xf, layer & 0x1);
    }

    public void updateBlockSkyLight(int x, int y, int z) {
        // todo
    }

    public void updateAllLight(Vector3f pos) {
        this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z);
        this.addLightUpdate((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateBlockLight(Long2ObjectMap<ShortSet> map) {
        if (map.isEmpty()) {
            return;
        }
        LongPriorityQueue lightPropagationQueue = new LongArrayFIFOQueue();
        Long2ByteMap lightRemovalQueue = new Long2ByteOpenHashMap();
        LongSet visited = new LongOpenHashSet();
        LongSet removalVisited = new LongOpenHashSet();

        for (Long2ObjectMap.Entry<ShortSet> entry : map.long2ObjectEntrySet()) {
            long chunkKey = entry.getLongKey();
            ShortSet blocks = entry.getValue();
            int chunkX = Chunk.fromKeyX(chunkKey);
            int chunkZ = Chunk.fromKeyZ(chunkKey);
            for (short blockKey : blocks) {
                Vector3i position = Chunk.fromKey(chunkKey, blockKey);
                Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
                if (chunk != null) {
                    int lcx = position.x & 0xF;
                    int lcz = position.z & 0xF;
                    int oldLevel = chunk.getBlockLight(lcx, position.y, lcz);
                    int newLevel = BlockRegistry.get().getBlock(chunk.getBlockId(lcx, position.y, lcz), 0).getLightLevel();
                    if (oldLevel != newLevel) {
                        this.setBlockLightAt(position.x, position.y, position.z, newLevel);
                        if (newLevel < oldLevel) {
                            removalVisited.add(Hash.hashBlock(position.x, position.y, position.z));
                            lightRemovalQueue.put(Hash.hashBlock(position.x, position.y, position.z), (byte) oldLevel);
                        } else {
                            visited.add(Hash.hashBlock(position.x, position.y, position.z));
                            lightPropagationQueue.enqueue(Hash.hashBlock(position.x, position.y, position.z));
                        }
                    }
                }
            }
        }
        map.clear();

        for (Long2ByteMap.Entry entry : lightRemovalQueue.long2ByteEntrySet()) {
            long node = entry.getLongKey();
            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);

            int lightLevel = entry.getByteValue();

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
            long node = lightPropagationQueue.dequeueLong();

            int x = Hash.hashBlockX(node);
            int y = Hash.hashBlockY(node);
            int z = Hash.hashBlockZ(node);

            int lightLevel = this.getBlockLightAt(x, y, z)
                    - BlockRegistry.get().getBlock(this.getBlockIdAt(x, y, z), 0).getFilterLevel();

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

    private void computeRemoveBlockLight(int x, int y, int z, int currentLight, Long2ByteMap queue,
                                         LongPriorityQueue spreadQueue, LongSet visited, LongSet spreadVisited) {
        int current = this.getBlockLightAt(x, y, z);
        long index = Hash.hashBlock(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);
            if (current > 1) {
                if (visited.add(index)) {
                    queue.put(Hash.hashBlock(x, y, z), (byte) current);
                }
            }
        } else if (current >= currentLight) {
            if (spreadVisited.add(index)) {
                spreadQueue.enqueue(Hash.hashBlock(x, y, z));
            }
        }
    }

    private void computeSpreadBlockLight(int x, int y, int z, int currentLight, LongPriorityQueue queue, LongSet visited) {
        int current = this.getBlockLightAt(x, y, z);
        long index = Hash.hashBlock(x, y, z);

        if (current < currentLight - 1) {
            this.setBlockLightAt(x, y, z, currentLight);

            if (visited.add(index)) {
                if (currentLight > 1) {
                    queue.enqueue(Hash.hashBlock(x, y, z));
                }
            }
        }
    }

    @Synchronized("lightQueue")
    public void addLightUpdate(int x, int y, int z) {
        long index = Chunk.key(x >> 4, z >> 4);
        this.lightQueue.computeIfAbsent(index, aLong -> new ShortOpenHashSet())
                .add(Chunk.blockKey(x, y, z));
    }

    public boolean setBlock(Vector3i pos, Block block) {
        return this.setBlock(pos, block, false);
    }

    public boolean setBlock(Vector3i pos, Block block, boolean direct) {
        return this.setBlock(pos, block, direct, true);
    }

    public boolean setBlock(Vector3i pos, Block block, boolean direct, boolean update) {
        return setBlock(pos.getX(), pos.getY(), pos.getZ(), 0, block, direct, update);
    }

    public boolean setBlock(BlockPosition pos, Block block) {
        return this.setBlock(pos, block, false);
    }

    public boolean setBlock(BlockPosition pos, Block block, boolean direct) {
        return this.setBlock(pos, block, direct, true);
    }

    public boolean setBlock(BlockPosition pos, Block block, boolean direct, boolean update) {
        return setBlock(pos.getX(), pos.getY(), pos.getZ(), pos.getLayer(), block, direct, update);
    }

    public boolean setBlock(int x, int y, int z, int layer, Block block, boolean direct, boolean update) {
        if (y < 0 || y >= 256) {
            return false;
        }
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        Block blockPrevious = chunk.getAndSetBlock(x & 0xF, y, z & 0xF, layer, block);
        if (Block.equals(blockPrevious, block, true)) {
            return false;
        }
        block.x = x;
        block.y = y;
        block.z = z;
        block.setLevel(this);
        block.setLayer(layer);
        int cx = x >> 4;
        int cz = z >> 4;
        long index = Chunk.key(cx, cz);
        if (direct) {
            this.sendBlocks(this.getChunkPlayers(cx, cz).toArray(new Player[0]), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
        } else {
            addBlockChange(index, x, y, z, layer);
        }

        if (update) {
            if (blockPrevious.isTransparent() != block.isTransparent() || blockPrevious.getLightLevel() != block.getLightLevel()) {
                addLightUpdate(x, y, z);
            }
            BlockUpdateEvent ev = new BlockUpdateEvent(block);
            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                for (Entity entity : this.getNearbyEntities(new SimpleAxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1))) {
                    this.scheduleEntityUpdate(entity);
                }
                block = ev.getBlock();
                block.onUpdate(BLOCK_UPDATE_NORMAL);
                this.updateAround(x, y, z, layer);
            }
        }
        return true;
    }

    private void addBlockChange(int x, int y, int z) {
        addBlockChange(x, y, z, 0);
    }

    private void addBlockChange(int x, int y, int z, int layer) {
        long index = Chunk.key(x >> 4, z >> 4);
        addBlockChange(index, x, y, z, layer);
    }

    private void addBlockChange(long index, int x, int y, int z, int layer) {
        IntSet current;
        try {
            current = this.changedBlocks.get(index, IntOpenHashSet::new);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Unable to get block changes", e);
        }
        synchronized (changedBlocks) {
            current.add(Chunk.blockKey(x, y, z, layer));
        }
    }

    @Nonnull
    public DroppedItem dropItem(Vector3i source, Item item) {
        return this.dropItem(source.add(0.5, 0.5, 0.5), item);
    }

    @Nonnull
    public DroppedItem dropItem(Vector3f source, Item item) {
        return this.dropItem(source, item, null);
    }

    @Nonnull
    public DroppedItem dropItem(Vector3f source, Item item, Vector3f motion) {
        return this.dropItem(source, item, motion, 10);
    }

    @Nonnull
    public DroppedItem dropItem(Vector3f source, Item item, Vector3f motion, int delay) {
        return this.dropItem(source, item, motion, false, delay);
    }

    @Nonnull
    public DroppedItem dropItem(Vector3f source, Item item, Vector3f motion, boolean dropAround, int delay) {
        checkNotNull(source, "source");
        checkNotNull(item, "item");
        checkArgument(!item.isNull(), "invalid item");

        if (motion == null) {
            if (dropAround) {
                float f = ThreadLocalRandom.current().nextFloat() * 0.5f;
                float f1 = ThreadLocalRandom.current().nextFloat() * ((float) Math.PI * 2);

                motion = new Vector3f(-MathHelper.sin(f1) * f, 0.2, MathHelper.cos(f1) * f);
            } else {
                motion = new Vector3f(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                        new java.util.Random().nextDouble() * 0.2 - 0.1);
            }
        }

        CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");

        DroppedItem droppedItem = EntityRegistry.get().newEntity(EntityTypes.ITEM,
                this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4),
                new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
                        .add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))

                        .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.x))
                                .add(new DoubleTag("", motion.y)).add(new DoubleTag("", motion.z)))

                        .putList(new ListTag<FloatTag>("Rotation")
                                .add(new FloatTag("", ThreadLocalRandom.current().nextFloat() * 360))
                                .add(new FloatTag("", 0)))

                        .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

        droppedItem.spawnToAll();
        return droppedItem;
    }

    public Item useBreakOn(Vector3i pos) {
        return this.useBreakOn(pos, null);
    }

    public Item useBreakOn(Vector3i pos, Item item) {
        return this.useBreakOn(pos, item, null);
    }

    public Item useBreakOn(Vector3i pos, Item item, Player player) {
        return this.useBreakOn(pos, item, player, false);
    }

    public Item useBreakOn(Vector3i pos, Item item, Player player, boolean createParticles) {
        return useBreakOn(pos, null, item, player, createParticles);
    }

    public Item useBreakOn(Vector3i pos, BlockFace face, Item item, Player player, boolean createParticles) {
        if (player != null && player.getGamemode() > 2) {
            return null;
        }
        Block target = this.getBlock(pos);
        Item[] drops;
        int dropExp = target.getDropExp();

        if (item == null) {
            item = Item.get(BlockIds.AIR, 0, 0);
        }

        boolean isSilkTouch = item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null;

        if (player != null) {
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanDestroy");
                boolean canBreak = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() != AIR && entry.getBlock() != null && entry.getBlock().getId() == target.getId()) {
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

            Item[] eventDrops;
            if (!player.isSurvival()) {
                eventDrops = new Item[0];
            } else if (isSilkTouch && target.canSilkTouch()) {
                eventDrops = new Item[]{target.toItem()};
            } else {
                eventDrops = target.getDrops(item);
            }

            BlockBreakEvent ev = new BlockBreakEvent(player, target, face, item, eventDrops, player.isCreative(),
                    (player.lastBreak + breakTime * 1000) > System.currentTimeMillis());

            if (player.isSurvival() && !target.isBreakable(item)) {
                ev.setCancelled();
            } else if(!player.isOp() && isInSpawnRadius(target)) {
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
        } else if (!target.isBreakable(item)) {
            return null;
        } else if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            drops = new Item[]{target.toItem()};
        } else {
            drops = target.getDrops(item);
        }

        Block above = this.getLoadedBlock(new Vector3f(target.x, target.y + 1, target.z));
        if (above != null) {
            if (above.getId() == FIRE) {
                this.setBlock(above, Block.get(BlockIds.AIR), true);
            }
        }

        if (createParticles) {
            Chunk chunk = this.getLoadedChunk(target.getChunkX(), target.getChunkZ());
            if (chunk != null) {
                this.addParticle(new DestroyBlockParticle(target.add(0.5), target), chunk.getPlayerLoaders());
            }
        }

        // Close BlockEntity before we check onBreak
        BlockEntity blockEntity = this.getLoadedBlockEntity(target);
        if (blockEntity != null) {
            blockEntity.onBreak();
            blockEntity.close();

            this.updateComparatorOutputLevel(target);
        }

        target.onBreak(item, player);

        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            item = Item.get(BlockIds.AIR, 0, 0);
        }

        if (this.getGameRules().get(GameRules.DO_TILE_DROPS)) {
            if (!isSilkTouch && player != null && player.isSurvival() && dropExp > 0 && drops.length != 0) {
                this.dropExpOrb(pos.add(0.5, 0.5, 0.5), dropExp);
            }

            if (player == null || player.isSurvival()) {
                for (Item drop : drops) {
                    if (drop.getCount() > 0) {
                        this.dropItem(pos.add(0.5, 0.5, 0.5), drop);
                    }
                }
            }
        }

        return item;
    }

    public void dropExpOrb(Vector3f source, int exp) {
        dropExpOrb(source, exp, null);
    }

    public void dropExpOrb(Vector3f source, int exp, Vector3f motion) {
        dropExpOrb(source, exp, motion, 10);
    }

    public void dropExpOrb(Vector3f source, int exp, Vector3f motion, int delay) {
        Random rand = ThreadLocalRandom.current();
        for (int split : XpOrb.splitIntoOrbSizes(exp)) {
            CompoundTag nbt = Entity.getDefaultNBT(source, motion == null ? new Vector3f(
                            (rand.nextDouble() * 0.2 - 0.1) * 2,
                            rand.nextDouble() * 0.4,
                            (rand.nextDouble() * 0.2 - 0.1) * 2) : motion,
                    rand.nextFloat() * 360f, 0);

            nbt.putShort("Value", split);
            nbt.putShort("PickupDelay", delay);

            XpOrb xpOrb = EntityRegistry.get().newEntity(EntityTypes.XP_ORB, this.getChunk(source.getChunkX(), source.getChunkZ()), nbt);
            xpOrb.spawnToAll();
        }
    }

    public Item useItemOn(Vector3i vector, Item item, BlockFace face, Vector3f clickPos) {
        return this.useItemOn(vector, item, face, clickPos, null);
    }

    public Item useItemOn(Vector3i vector, Item item, BlockFace face, Vector3f clickPos, Player player) {
        return this.useItemOn(vector, item, face, clickPos, player, true);
    }


    public Item useItemOn(Vector3i vector, Item item, BlockFace face, Vector3f clickPos, Player player, boolean playSound) {
        Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (block.y > 255 || block.y < 0) {
            return null;
        }

        if (target.getId() == AIR) {
            return null;
        }

        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
                    target.getId() == AIR ? Action.RIGHT_CLICK_AIR : Action.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > 2) {
                ev.setCancelled();
            }

            if(!player.isOp() && isInSpawnRadius(target)) {
                ev.setCancelled();
            }

            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onUpdate(BLOCK_UPDATE_TOUCH);
                if ((!player.isSneaking() || player.getInventory().getItemInHand().isNull()) && target.canBeActivated() && target.onActivate(item, player)) {
                    if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                        item = Item.get(BlockIds.AIR, 0, 0);
                    }
                    return item;
                }

                if (item.canBeActivated() && item.onActivate(this, player, block, target, face, clickPos)) {
                    if (item.getCount() <= 0) {
                        item = Item.get(BlockIds.AIR, 0, 0);
                        return item;
                    }
                }
            } else {
                if (item.getId() == ItemIds.BUCKET && ItemBucket.getBlockIdFromDamage(item.getDamage()) == FLOWING_WATER) {
                    player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(AIR, 0, block.setLayer(1))}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                }
                return null;
            }
        } else if (target.canBeActivated() && target.onActivate(item, player)) {
            if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
                item = Item.get(BlockIds.AIR, 0, 0);
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

        if (!(block.canBeReplaced() || (hand.getId() == STONE_SLAB && block.getId() == STONE_SLAB))) {
            return null;
        }

        if (target.canBeReplaced()) {
            block = target;
            hand.position(block);
        }

        if (!hand.canPassThrough() && hand.getBoundingBox() != null) {
            Set<Entity> entities = this.getCollidingEntities(hand.getBoundingBox());
            int realCount = 0;
            for (Entity e : entities) {
                if (e instanceof EntityArrow || e instanceof DroppedItem || (e instanceof Player && ((Player) e).isSpectator())) {
                    continue;
                }
                ++realCount;
            }

            if (player != null) {
                Vector3f diff = player.getNextPosition().subtract(player.getPosition());
                if (diff.lengthSquared() > 0.00001) {
                    AxisAlignedBB bb = player.getBoundingBox().getOffsetBoundingBox(diff.x, diff.y, diff.z);
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
            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            if (player.getGamemode() == 2) {
                Tag tag = item.getNamedTagEntry("CanPlaceOn");
                boolean canPlace = false;
                if (tag instanceof ListTag) {
                    for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                        if (v instanceof StringTag) {
                            Item entry = Item.fromString(((StringTag) v).data);
                            if (entry.getId() != AIR && entry.getBlock() != null && entry.getBlock().getId() == target.getId()) {
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

        Block liquid = block;
        Block air = block.getBlockAtLayer(1);
        BlockPosition layer0 = null;
        BlockPosition layer1 = null;
        if (air.getId() == BlockIds.AIR && (liquid instanceof BlockLiquid) && ((BlockLiquid) liquid).usesWaterLogging()
                && (liquid.getDamage() == 0 || liquid.getDamage() == 8) // Remove this line when MCPE-33345 is resolved
        ) {
            layer0 = liquid.layer(0);
            layer1 = air.layer(1);

            this.setBlock(layer1, liquid, false, false);
            this.setBlock(layer0, air, false, false);
            block = air;
            this.scheduleUpdate(block, 1);
        }

        try {
            if (!hand.place(item, block, target, face, clickPos, player)) {
                if (layer0 != null) {
                    this.setBlock(layer0, liquid, false, false);
                    this.setBlock(layer1, air, false, false);
                }
                return null;
            }
        } catch (Exception e) {
            if (layer0 != null) {
                this.setBlock(layer0, liquid, false, false);
                this.setBlock(layer1, air, false, false);
            }
            throw e;
        }

        if (player != null) {
            if (!player.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
        }

        if (playSound) {
            this.addLevelSoundEvent(hand.asVector3f(), LevelSoundEventPacket.SOUND_PLACE, BlockRegistry.get().getRuntimeId(hand));
        }

        if (item.getCount() <= 0) {
            item = Item.get(BlockIds.AIR, 0, 0);
        }
        return item;
    }

    public boolean isInSpawnRadius(BlockPosition vector3) {
        int distance = this.server.getSpawnRadius();
        if (distance > -1) {
            Vector2f t = new Vector2f(vector3.x, vector3.z);
            Vector2f s = new Vector2f(this.getSpawnLocation().x, this.getSpawnLocation().z);
            return t.distance(s) <= distance;
        }
        return false;
    }

    public Entity getEntity(long entityId) {
        synchronized (entities) {
            return this.entities.containsKey(entityId) ? this.entities.get(entityId) : null;
        }
    }

    public Entity[] getEntities() {
        synchronized (entities) {
            return entities.values().toArray(new Entity[0]);
        }
    }

    public Set<Entity> getCollidingEntities(AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Set<Entity> getCollidingEntities(AxisAlignedBB bb, Entity entity) {
        ImmutableSet.Builder<Entity> entities = null;

        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.getMinX() - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Set<Entity> colliding = this.getLoadedChunkEntities(x, z);
                    for (Entity ent : colliding) {
                        if ((entity == null || (ent != entity && entity.canCollideWith(ent)))
                                && ent.getBoundingBox().intersectsWith(bb)) {
                            if (entities == null) {
                                entities = ImmutableSet.builder();
                            }
                            entities.add(ent);
                        }
                    }
                }
            }
        }

        return entities == null ? ImmutableSet.of() : entities.build();
    }

    public Set<Entity> getNearbyEntities(AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null);
    }

    public Set<Entity> getNearbyEntities(AxisAlignedBB bb, Entity entity) {
        return getNearbyEntities(bb, entity, false);
    }

    public Set<Entity> getNearbyEntities(AxisAlignedBB bb, Entity entity, boolean loadChunks) {
        int minX = NukkitMath.floorDouble((bb.getMinX() - 2) * 0.0625);
        int maxX = NukkitMath.ceilDouble((bb.getMaxX() + 2) * 0.0625);
        int minZ = NukkitMath.floorDouble((bb.getMinZ() - 2) * 0.0625);
        int maxZ = NukkitMath.ceilDouble((bb.getMaxZ() + 2) * 0.0625);

        ImmutableSet.Builder<Entity> entities = null;

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                Set<Entity> entitiesInRange = loadChunks ? this.getChunkEntities(x, z) : this.getLoadedChunkEntities(x, z);
                for (Entity entityInRange : entitiesInRange) {
                    if (entityInRange != entity && entityInRange.getBoundingBox().intersectsWith(bb)) {
                        if (entities == null) {
                            entities = ImmutableSet.builder();
                        }
                        entities.add(entityInRange);
                    }
                }
            }
        }

        return entities == null ? ImmutableSet.of() : entities.build();
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


    public BlockEntity getBlockEntity(Vector3i pos) {
        Chunk chunk = this.getChunk(pos.getChunkX(), pos.getChunkZ());
        return chunk.getBlockEntity(pos.x & 0x0f, pos.y & 0xff, pos.z & 0x0f);
    }

    @Nullable
    public BlockEntity getLoadedBlockEntity(Vector3i pos) {
        Chunk chunk = this.getLoadedChunk(pos.getChunkX(), pos.getChunkZ());
        return chunk == null ? null : chunk.getBlockEntity(pos.x & 0x0f, pos.y & 0xff, pos.z & 0x0f);
    }

    @Nonnull
    public Set<Entity> getChunkEntities(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ).getEntities();
    }

    @Nonnull
    public Set<Entity> getLoadedChunkEntities(int chunkX, int chunkZ) {
        Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
        if (chunk != null) {
            return ImmutableSet.<Entity>builder()
                    .addAll(chunk.getEntities())
                    .addAll(chunk.getPlayers())
                    .build();
        }
        return Collections.emptySet();
    }


    @Nonnull
    public Collection<BlockEntity> getChunkBlockEntities(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ).getBlockEntities();
    }

    @Nonnull
    public Collection<BlockEntity> getLoadedBlockEntities(int chunkX, int chunkZ) {
        Chunk chunk = this.getLoadedChunk(chunkX, chunkZ);
        return chunk == null ? Collections.emptyList() : chunk.getBlockEntities();
    }


    public Identifier getBlockIdAt(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4).getBlockId(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    public void setBlockIdAt(int x, int y, int z, int layer, Identifier id) {
        this.getChunk(x >> 4, z >> 4).setBlockId(x & 0x0f, y & 0xff, z & 0x0f, layer, id);
        addBlockChange(x, y, z, layer);
    }

    @Override
    public Block getBlockAt(int x, int y, int z, int layer) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        return chunk.getBlock(x, y, z, layer);
    }

    @Override
    public void setBlockAt(int x, int y, int z, int layer, Block block) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        chunk.setBlock(x & 0x0f, y & 0xff, z & 0x0f, layer, block);
        addBlockChange(x, y, z, layer);
    }

    @Override
    public int getBlockRuntimeIdUnsafe(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4).getBlockRuntimeIdUnsafe(x & 0xF, y & 0xFF, z & 0xF, layer);
    }

    @Override
    public void setBlockRuntimeIdUnsafe(int x, int y, int z, int layer, int runtimeId) {
        this.getChunk(x >> 4, z >> 4).setBlockRuntimeIdUnsafe(x & 0xF, y & 0xFF, z & 0xF, layer, runtimeId);
        this.addBlockChange(x, y, z, layer);
    }

    @Override
    public int getBlockDataAt(int x, int y, int z, int layer) {
        return this.getChunk(x >> 4, z >> 4).getBlockData(x & 0x0f, y & 0xff, z & 0x0f, layer);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int layer, int data) {
        this.getChunk(x >> 4, z >> 4).setBlockData(x & 0x0f, y & 0xff, z & 0x0f, layer, data & 0x0f);
        this.addBlockChange(x, y, z);
    }

    public int getBlockSkyLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4).getBlockLight(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public void setBlockSkyLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4).setBlockLight(x & 0x0f, y & 0xff, z & 0x0f, (byte) (level & 0x0f));
    }

    public int getBlockLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4).getBlockLight(x & 0x0f, y & 0xff, z & 0x0f);
    }

    public void setBlockLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4).setBlockLight(x & 0x0f, y & 0xff, z & 0x0f, (byte) (level & 0x0f));
    }

    public int getBiomeId(int x, int z) {
        return this.getChunk(x >> 4, z >> 4).getBiome(x & 0x0f, z & 0x0f);
    }

    public void setBiomeId(int x, int z, byte biomeId) {
        this.getChunk(x >> 4, z >> 4).setBiome(x & 0x0f, z & 0x0f, biomeId);
    }

    public int getHeightMap(int x, int z) {
        return this.getChunk(x >> 4, z >> 4).getHeightMap(x & 0x0f, z & 0x0f);
    }

    public void setHeightMap(int x, int z, int value) {
        this.getChunk(x >> 4, z >> 4).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
    }

    @Nullable
    public Chunk getLoadedChunk(long chunkKey) {
        return this.chunkManager.getLoadedChunk(chunkKey);
    }

    @Nullable
    public Chunk getLoadedChunk(int chunkX, int chunkZ) {
        return this.chunkManager.getLoadedChunk(chunkX, chunkZ);
    }

    @Nonnull
    public Set<Chunk> getChunks() {
        return this.chunkManager.getLoadedChunks();
    }

    public int getChunkCount() {
        return this.chunkManager.getLoadedCount();
    }

    @Nonnull
    public Chunk getChunk(long chunkKey) {
        return this.chunkManager.getChunk(Chunk.fromKeyX(chunkKey), Chunk.fromKeyZ(chunkKey));
    }

    @Nonnull
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.chunkManager.getChunk(chunkX, chunkZ);
    }

    @Nonnull
    public CompletableFuture<Chunk> getChunkFuture(int chunkX, int chunkZ) {
        return this.chunkManager.getChunkFuture(chunkX, chunkZ);
    }

    public int getHighestBlockAt(int x, int z) {
        return this.getChunk(x >> 4, z >> 4).getHighestBlock(x & 0x0f, z & 0x0f);
    }

    public BlockColor getMapColorAt(int x, int z) {
        Chunk chunk = this.getChunk(x >> 4, z >> 4);
        int y = chunk.getHighestBlock(x, z);
        while (y > 1) {
            Block block = getBlock(new BlockPosition(x, y, z));
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
        return this.chunkManager.isChunkLoaded(x, z);
    }

    private boolean areNeighboringChunksLoaded(long hash) {
        return this.chunkManager.isChunkLoaded(hash + 1) &&
                this.chunkManager.isChunkLoaded(hash - 1) &&
                this.chunkManager.isChunkLoaded(hash + (1L << 32)) &&
                this.chunkManager.isChunkLoaded(hash - (1L << 32));
    }

    public Position getSpawnLocation() {
        return Position.fromObject(this.levelData.getSpawn().add(0.5, 0, 0.5), this);
    }

    public void setSpawnLocation(Vector3f pos) {
        Position previousSpawn = this.getSpawnLocation();
        this.levelData.setSpawn(pos.asVector3i());
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
        SetSpawnPositionPacket pk = new SetSpawnPositionPacket();
        pk.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        for (Player p : getPlayers().values()) p.dataPacket(pk);
    }

    public void requestChunk(int x, int z, Player player) {
        Preconditions.checkState(player.getLoaderId() > 0, player.getName() + " has no chunk loader");
        long index = Chunk.key(x, z);

        this.chunkSendQueue.putIfAbsent(index, new Int2ObjectOpenHashMap<>());

        this.chunkSendQueue.get(index).put(player.getLoaderId(), player);
    }

    private void sendChunk(int x, int z, long index, DataPacket packet) {
        /*
        if (this.chunkSendTasks.contains(index)) {
            for (Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, packet);
                }
            }

            this.chunkSendQueue.remove(index);
            this.chunkSendTasks.remove(index);
        }*/
    }

    public void scheduleEntityUpdate(Entity entity) {
        checkNotNull(entity, "entity");
        this.updateEntities.add(entity);
    }

    public void removeEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.remove(entity.getUniqueId());
            this.checkSleep();
        } else {
            entity.close();
        }

        synchronized (entities) {
            this.entities.remove(entity.getUniqueId());
        }
        this.updateEntities.remove(entity);
    }

    public void addEntity(Entity entity) {
        if (entity.getLevel() != this) {
            throw new LevelException("Invalid Entity level");
        }

        if (entity instanceof Player) {
            this.players.put(entity.getUniqueId(), (Player) entity);
        }
        synchronized (entities) {
            this.entities.put(entity.getUniqueId(), entity);
        }
    }

    public void addBlockEntity(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != this) {
            throw new LevelException("Invalid Block Entity level");
        }
        blockEntities.put(blockEntity.getId(), blockEntity);
    }

    public void scheduleBlockEntityUpdate(BlockEntity entity) {
        checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        if (!updateBlockEntities.contains(entity)) {
            updateBlockEntities.add(entity);
        }
    }

    public void removeBlockEntity(BlockEntity entity) {
        checkNotNull(entity, "entity");
        Preconditions.checkArgument(entity.getLevel() == this, "BlockEntity is not in this level");
        blockEntities.remove(entity.getId());
        updateBlockEntities.remove(entity);
    }

    public boolean isSpawnChunk(int x, int z) {
        Vector3i spawn = this.levelData.getSpawn();
        return Math.abs(x - (spawn.getChunkX())) <= 1 && Math.abs(z - (spawn.getChunkZ())) <= 1;
    }

    public Position getSafeSpawn() {
        return this.getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3f spawn) {
        if (spawn == null || spawn.y < 1) {
            spawn = this.getSpawnLocation();
        }

        if (spawn != null) {
            Vector3f v = spawn.floor();
            Chunk chunk = this.getLoadedChunk((int) v.x >> 4, (int) v.z >> 4);
            int x = (int) v.x & 0x0f;
            int z = (int) v.z & 0x0f;
            if (chunk != null) {
                int y = (int) NukkitMath.clamp(v.y, 0, 254);
                boolean wasAir = chunk.getBlockId(x, y - 1, z) == AIR;
                for (; y > 0; --y) {
                    Block block = chunk.getBlock(x, y, z);
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
                    Block block = chunk.getBlock(x, y + 1, z);
                    if (!this.isFullBlock(block)) {
                        block = chunk.getBlock(x, y, z);
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
        return (int) this.levelData.getTime();
    }

    public boolean isDaytime() {
        return this.skyLightSubtracted < 4;
    }

    public void setTime(int time) {
        this.levelData.setTime(time);
        this.sendTime();
    }

    public long getCurrentTick() {
        return this.levelData.getCurrentTick();
    }

    public String getName() {
        return this.levelData.getName();
    }

    public void stopTime() {
        this.getGameRules().put(GameRules.DO_DAYLIGHT_CYCLE, false);
        this.sendTime();
    }

    public void startTime() {
        this.getGameRules().put(GameRules.DO_DAYLIGHT_CYCLE, true);
        this.sendTime();
    }

    public long getSeed() {
        return this.levelData.getRandomSeed();
    }

    public void setSeed(long seed) {
        this.levelData.setRandomSeed(seed);
    }

    public void doChunkGarbageCollection() {
        this.chunkManager.tick();
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

    public void addEntityMovement(BaseEntity entity, double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = entity.getUniqueId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;

        Server.broadcastPacket(entity.getViewers(), pk);
    }

    public boolean isRaining() {
        return this.levelData.getRainLevel() > 0;
    }

    public boolean setRaining(boolean raining) {
        WeatherChangeEvent ev = new WeatherChangeEvent(this, raining);
        this.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        this.levelData.setRainLevel(raining ? 1 : 0);

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft

        if (raining) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = ThreadLocalRandom.current().nextInt(50000) + 10000;
            setRainTime(ThreadLocalRandom.current().nextInt(12000) + 12000);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
            setRainTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getRainTime() {
        return this.levelData.getRainTime();
    }

    public void setRainTime(int rainTime) {
        this.levelData.setRainTime(rainTime);
    }

    public boolean isThundering() {
        return isRaining() && this.levelData.getLightningLevel() > 0;
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

        this.levelData.setLightningLevel(thundering ? 1 : 0);

        LevelEventPacket pk = new LevelEventPacket();
        // These numbers are from Minecraft
        if (thundering) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = ThreadLocalRandom.current().nextInt(50000) + 10000;
            setThunderTime(ThreadLocalRandom.current().nextInt(12000) + 3600);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
            setThunderTime(ThreadLocalRandom.current().nextInt(168000) + 12000);
        }

        Server.broadcastPacket(this.getPlayers().values(), pk);

        return true;
    }

    public int getThunderTime() {
        return this.levelData.getLightningTime();
    }

    public void setThunderTime(int thunderTime) {
        this.levelData.setLightningTime(thunderTime);
    }

    public void sendWeather(Player[] players) {
        if (players == null) {
            players = this.getPlayers().values().toArray(new Player[0]);
        }

        LevelEventPacket pk = new LevelEventPacket();

        if (this.isRaining()) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = ThreadLocalRandom.current().nextInt(50000) + 10000;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
        }

        Server.broadcastPacket(players, pk);

        if (this.isThundering()) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = ThreadLocalRandom.current().nextInt(50000) + 10000;
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
        return this.levelData.getDimension();
    }

    public boolean canBlockSeeSky(Vector3i pos) {
        return this.getHighestBlockAt(pos.getX(), pos.getZ()) < pos.getY();
    }

    public int getStrongPower(Vector3i pos, BlockFace direction) {
        return this.getBlock(pos).getStrongPower(direction);
    }

    public int getStrongPower(Vector3i pos) {
        int i = 0;

        for (BlockFace face : BlockFace.values()) {
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

    public boolean isSidePowered(Vector3i pos, BlockFace face) {
        return this.getRedstonePower(pos, face) > 0;
    }

    public int getRedstonePower(Vector3i pos, BlockFace face) {
        Block block = this.getBlock(pos);
        return block.isNormalBlock() ? this.getStrongPower(pos) : block.getWeakPower(face);
    }

    public boolean isBlockPowered(Vector3i pos) {
        for (BlockFace face : BlockFace.values()) {
            if (this.getRedstonePower(pos.getSide(face), face) > 0) {
                return true;
            }
        }

        return false;
    }

    public int isBlockIndirectlyGettingPowered(BlockPosition pos) {
        int power = 0;

        for (BlockFace face : BlockFace.values()) {
            int blockPower = this.getRedstonePower(pos.getSide(face), face);

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

    @Override
    public String toString() {
        return "Level(id=" + id + ")";
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
//    public List<Entity> orderChunkEntitiesForSpawn(Chunk chunk) {
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
