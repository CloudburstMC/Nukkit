package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
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
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.task.*;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.sound.Sound;
import cn.nukkit.math.*;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.redstone.Redstone;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.timings.LevelTimings;
import cn.nukkit.timings.Timings;
import cn.nukkit.timings.TimingsHistory;
import cn.nukkit.utils.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author: MagicDroidX Nukkit Project
 */
public class Level implements ChunkManager, Metadatable {

    private static int levelIdCounter = 1;
    private static int chunkLoaderCounter = 1;
    public static int COMPRESSION_LEVEL = 8;

    public static final int BLOCK_UPDATE_NORMAL = 1;
    public static final int BLOCK_UPDATE_RANDOM = 2;
    public static final int BLOCK_UPDATE_SCHEDULED = 3;
    public static final int BLOCK_UPDATE_WEAK = 4;
    public static final int BLOCK_UPDATE_TOUCH = 5;

    public static final int TIME_DAY = 0;
    public static final int TIME_SUNSET = 12000;
    public static final int TIME_NIGHT = 14000;
    public static final int TIME_SUNRISE = 23000;

    public static final int TIME_FULL = 24000;

    public static final int DIMENSION_OVERWORLD = 0;
    public static final int DIMENSION_NETHER = 1;

    private final Map<Long, BlockEntity> blockEntities = new HashMap<>();

    private Map<String, Map<Long, SetEntityMotionPacket.Entry>> motionToSend = new HashMap<>();
    private Map<String, Map<Long, MoveEntityPacket>> moveToSend = new HashMap<>();
    private Map<String, Map<Long, MovePlayerPacket>> playerMoveToSend = new HashMap<>();

    private final Map<Long, Player> players = new HashMap<>();

    private final Map<Long, Entity> entities = new HashMap<>();

    public final Map<Long, Entity> updateEntities = new HashMap<>();

    public final Map<Long, BlockEntity> updateBlockEntities = new HashMap<>();

    private Map<String, Block> blockCache = new HashMap<>();

    private Map<String, DataPacket> chunkCache = new HashMap<>();

    private boolean cacheChunks = false;

    private int sendTimeTicker = 0;

    private final Server server;

    private final int levelId;

    private LevelProvider provider;

    private final Map<Integer, ChunkLoader> loaders = new HashMap<>();

    private final Map<Integer, Integer> loaderCounter = new HashMap<>();

    private final Map<String, Map<Integer, ChunkLoader>> chunkLoaders = new HashMap<>();

    private final Map<String, Map<Integer, Player>> playerLoaders = new HashMap<>();

    private Map<String, List<DataPacket>> chunkPackets = new HashMap<>();

    private final Map<String, Long> unloadQueue = new HashMap<>();

    private float time;
    public boolean stopTime;

    private String folderName;

    private final Map<String, BaseFullChunk> chunks = new HashMap<>();

    private Map<String, Map<String, Vector3>> changedBlocks = new HashMap<>();

    private PriorityQueue<PriorityObject> updateQueue;
    private final Map<String, Integer> updateQueueIndex = new HashMap<>();

    private final Map<String, Map<Integer, Player>> chunkSendQueue = new HashMap<>();
    private final Map<String, Boolean> chunkSendTasks = new HashMap<>();

    private final Map<String, Boolean> chunkPopulationQueue = new HashMap<>();
    private final Map<String, Boolean> chunkPopulationLock = new HashMap<>();
    private final Map<String, Boolean> chunkGenerationQueue = new HashMap<>();
    private int chunkGenerationQueueSize = 8;
    private int chunkPopulationQueueSize = 2;

    private boolean autoSave = true;

    private BlockMetadataStore blockMetadata;

    private boolean useSections;
    private byte blockOrder;

    private Position temporalPosition;
    private Vector3 temporalVector;

    private final Block[] blockStates;

    public int sleepTicks = 0;

    private int chunkTickRadius;
    private Map<String, Integer> chunkTickList = new HashMap<>();
    private int chunksPerTicks;
    private boolean clearChunksOnTick;
    private final HashMap<Integer, Class<? extends Block>> randomTickBlocks = new HashMap<Integer, Class<? extends Block>>() {
        {
            put(Block.GRASS, BlockGrass.class);
            put(Block.FARMLAND, BlockFarmland.class);
            put(Block.MYCELIUM, BlockMycelium.class);

            put(Block.SAPLING, BlockSapling.class);

            put(Block.LEAVES, BlockLeaves.class);
            put(Block.LEAVES2, BlockLeaves2.class);

            put(Block.SNOW_LAYER, BlockSnowLayer.class);
            put(Block.ICE, BlockIce.class);
            put(Block.LAVA, BlockLava.class);
            put(Block.STILL_LAVA, BlockLavaStill.class);

            put(Block.CACTUS, BlockCactus.class);
            put(Block.BEETROOT_BLOCK, BlockBeetroot.class);
            put(Block.CARROT_BLOCK, BlockCarrot.class);
            put(Block.POTATO_BLOCK, BlockPotato.class);
            put(Block.MELON_STEM, BlockStemMelon.class);
            put(Block.PUMPKIN_STEM, BlockStemPumpkin.class);
            put(Block.WHEAT_BLOCK, BlockWheat.class);
            put(Block.SUGARCANE_BLOCK, BlockSugarcane.class);
            put(Block.RED_MUSHROOM, BlockMushroomRed.class);
            put(Block.BROWN_MUSHROOM, BlockMushroomBrown.class);

            put(Block.FIRE, BlockFire.class);
            put(Block.GLOWING_REDSTONE_ORE, BlockOreRedstoneGlowing.class);
        }
    };

    protected int updateLCG = (new Random()).nextInt();

    public LevelTimings timings;

    private int tickRate;
    public int tickRateTime = 0;
    public int tickRateCounter = 0;

    private Class<? extends Generator> generator;
    private Generator generatorInstance;

    public final java.util.Random rand = new java.util.Random();
    private boolean raining = false;
    private int rainTime = 0;
    private boolean thundering = false;
    private int thunderTime = 0;

    private long levelCurrentTick = 0;

    private final int dimension = DIMENSION_OVERWORLD;

    public Level(Server server, String name, String path, Class<? extends LevelProvider> provider) {
        this.blockStates = Block.fullList;
        this.levelId = levelIdCounter++;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();

        try {
            this.provider = provider.getConstructor(Level.class, String.class).newInstance(this, path);
        } catch (Exception e) {
            throw new LevelException("Caused by " + Utils.getExceptionMessage(e));
        }

        this.provider.updateLevelName(name);

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.preparing",
                TextFormat.GREEN + this.provider.getName() + TextFormat.WHITE));

        this.generator = Generator.getGenerator(this.provider.getGenerator());

        try {
            this.blockOrder = (byte) provider.getMethod("getProviderOrder").invoke(null);
            this.useSections = (boolean) provider.getMethod("usesChunkSection").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.folderName = name;
        this.updateQueue = new PriorityQueue<>(11, new Comparator<PriorityObject>() {
            @Override
            public int compare(PriorityObject o1, PriorityObject o2) {
                return o1.priority > o2.priority ? 1 : (o1.priority == o2.priority ? 0 : -1);
            }
        });
        this.time = this.provider.getTime();

        this.raining = this.provider.isRaining();
        this.rainTime = this.provider.getRainTime();
        if (this.rainTime <= 0) {
            setRainTime(rand.nextInt(168000) + 12000);
        }

        this.thundering = this.provider.isThundering();
        this.thunderTime = this.provider.getThunderTime();
        if (this.thunderTime <= 0) {
            setThunderTime(rand.nextInt(168000) + 12000);
        }

        this.levelCurrentTick = this.provider.getCurrentTick();

        this.chunkTickRadius = Math.min(this.server.getViewDistance(),
                Math.max(1, (Integer) this.server.getConfig("chunk-ticking.tick-radius", 4)));
        this.chunksPerTicks = (int) this.server.getConfig("chunk-ticking.per-tick", 40);
        this.chunkGenerationQueueSize = (int) this.server.getConfig("chunk-generation.queue-size", 8);
        this.chunkPopulationQueueSize = (int) this.server.getConfig("chunk-generation.population-queue-size", 2);
        this.chunkTickList.clear();
        this.clearChunksOnTick = (boolean) this.server.getConfig("chunk-ticking.clear-tick-list", true);
        this.cacheChunks = (boolean) this.server.getConfig("chunk-sending.cache-chunks", false);
        this.timings = new LevelTimings(this);
        this.temporalPosition = new Position(0, 0, 0, this);
        this.temporalVector = new Vector3(0, 0, 0);
        this.tickRate = 1;
    }

    public static String chunkHash(int x, int z) {
        return x + ":" + z;
    }

    public static String blockHash(Vector3 block) {
        return blockHash(block.x, block.y, block.z);
    }

    public static String blockHash(double x, double y, double z) {
        return (int) x + ":" + (int) y + ":" + (int) z;
    }

    public static int chunkBlockHash(int x, int y, int z) {
        return (x << 11) | (z << 7) | y;
    }

    public static Vector3 getBlockXYZ(String hash) {
        String[] h = hash.split(":");
        return new Vector3(Integer.valueOf(h[0]), Integer.valueOf(h[1]), Integer.valueOf(h[2]));
    }

    public static Chunk.Entry getChunkXZ(String hash) {
        String[] h = hash.split(":");
        return new Chunk.Entry(Integer.valueOf(h[0]), Integer.valueOf(h[1]));
    }

    public static int generateChunkLoaderId(ChunkLoader loader) {
        if (loader.getLoaderId() == null || loader.getLoaderId() == 0) {
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
        try {
            this.generatorInstance = this.generator.getConstructor(Map.class)
                    .newInstance(this.provider.getGeneratorOptions());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.generatorInstance.init(this, new NukkitRandom(this.getSeed()));

        this.registerGenerator();
    }

    public void registerGenerator() {
        int size = this.server.getScheduler().getAsyncTaskPoolSize();
        for (int i = 0; i < size; ++i) {
            this.server.getScheduler().scheduleAsyncTask(new GeneratorRegisterTask(this, this.generatorInstance));
        }
    }

    public void unregisterGenerator() {
        int size = this.server.getScheduler().getAsyncTaskPoolSize();
        for (int i = 0; i < size; ++i) {
            this.server.getScheduler().scheduleAsyncTask(new GeneratorUnregisterTask(this));
        }
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
        if (this.getAutoSave()) {
            this.save();
        }

        for (BaseFullChunk chunk : new ArrayList<>(this.chunks.values())) {
            this.unloadChunk(chunk.getX(), chunk.getZ(), false);
        }

        this.unregisterGenerator();

        this.provider.close();
        this.provider = null;
        this.blockMetadata = null;
        this.blockCache.clear();
        this.temporalPosition = null;
        this.server.getLevels().remove(this.levelId);
    }

    public void addSound(Sound sound) {
        this.addSound(sound, (Player[]) null);
    }

    public void addSound(Sound sound, Player player) {
        this.addSound(sound, new Player[]{player});
    }

    public void addSound(Sound sound, Player[] players) {
        DataPacket[] packets = sound.encode();

        if (players == null) {
            if (packets != null) {
                for (DataPacket packet : packets) {
                    this.addChunkPacket((int) sound.x >> 4, (int) sound.z >> 4, packet);
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

    public void addSound(Sound sound, Collection<Player> players) {
        this.addSound(sound, players.stream().toArray(Player[]::new));
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
        this.addParticle(particle, players.stream().toArray(Player[]::new));
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
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.playerLoaders.containsKey(index)) {
            return new HashMap<>(this.playerLoaders.get(index));
        } else {
            return new HashMap<>();
        }
    }

    public ChunkLoader[] getChunkLoaders(int chunkX, int chunkZ) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunkLoaders.containsKey(index)) {
            return this.chunkLoaders.get(index).values().stream().toArray(ChunkLoader[]::new);
        } else {
            return new ChunkLoader[0];
        }
    }

    public void addChunkPacket(int chunkX, int chunkZ, DataPacket packet) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (!this.chunkPackets.containsKey(index)) {
            this.chunkPackets.put(index, new ArrayList<>());
        }
        this.chunkPackets.get(index).add(packet);
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        this.registerChunkLoader(loader, chunkX, chunkZ, true);
    }

    public void registerChunkLoader(ChunkLoader loader, int chunkX, int chunkZ, boolean autoLoad) {
        int hash = loader.getLoaderId();
        String index = Level.chunkHash(chunkX, chunkZ);
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

        this.cancelUnloadChunkRequest(chunkX, chunkZ);

        if (autoLoad) {
            this.loadChunk(chunkX, chunkZ);
        }
    }

    public void unregisterChunkLoader(ChunkLoader loader, int chunkX, int chunkZ) {
        int hash = loader.getLoaderId();
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunkLoaders.containsKey(index) && this.chunkLoaders.get(index).containsKey(hash)) {
            this.chunkLoaders.get(index).remove(hash);
            this.playerLoaders.get(index).remove(hash);
            if (this.chunkLoaders.get(index).isEmpty()) {
                this.chunkLoaders.remove(index);
                this.playerLoaders.remove(index);
                this.unloadChunkRequest(chunkX, chunkZ, true);
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

    public void checkTime() {
        if (!this.stopTime) {
            this.time += 1;
        }
    }

    public void sendTime(Player player) {
        if (this.stopTime) {
            SetTimePacket pk0 = new SetTimePacket();
            pk0.time = (int) this.time;
            pk0.started = true;
            player.dataPacket(pk0);
        }

        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;
        pk.started = !this.stopTime;

        player.dataPacket(pk);
    }

    public void sendTime() {
        if (this.stopTime) {
            SetTimePacket pk0 = new SetTimePacket();
            pk0.time = (int) this.time;
            pk0.started = true;
            Server.broadcastPacket(this.players.values().stream().toArray(Player[]::new), pk0);
        }

        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;
        pk.started = !this.stopTime;

        Server.broadcastPacket(this.players.values().stream().toArray(Player[]::new), pk);
    }

    public void doTick(int currentTick) {

        this.timings.doTick.startTiming();

        this.checkTime();

        if (++this.sendTimeTicker == 200) {
            this.sendTime();
            this.sendTimeTicker = 0;
        }

        // Tick Weather
        this.rainTime--;
        if (this.rainTime <= 0) {
            if (!this.setRaining(!this.raining)) {
                if (this.raining) {
                    setRainTime(rand.nextInt(12000) + 12000);
                } else {
                    setRainTime(rand.nextInt(168000) + 12000);
                }
            }
        }

        this.thunderTime--;
        if (this.thunderTime <= 0) {
            if (!this.setThundering(!this.thundering)) {
                if (this.thundering) {
                    setThunderTime(rand.nextInt(12000) + 3600);
                } else {
                    setThunderTime(rand.nextInt(168000) + 12000);
                }
            }
        }

        if (this.isThundering()) {
            synchronized (this) {
                for (FullChunk chunk : this.getChunks().values()) {
                    if (rand.nextInt(100000) == 0) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int LCG = this.updateLCG >> 2;
                        int x = LCG & 0x0f;
                        int z = LCG >> 8 & 0x0f;
                        int y = chunk.getHighestBlockAt(x, z);
                        int bId = chunk.getBlockId(x, y, z);
                        if (bId != Block.TALL_GRASS && bId != Block.WATER)
                            y += 1;
                        CompoundTag nbt = new CompoundTag()
                                .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", x + 16 * chunk.getX()))
                                        .add(new DoubleTag("", y)).add(new DoubleTag("", z + 16 * chunk.getZ())))
                                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0))
                                        .add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0))
                                        .add(new FloatTag("", 0)));

                        EntityLightning bolt = new EntityLightning(chunk, nbt);
                        LightningStrikeEvent ev = new LightningStrikeEvent(this, bolt);
                        if (!ev.isCancelled()) {
                            bolt.spawnToAll();
                        } else {
                            bolt.setEffect(false);
                        }

                    }

                }
            }

        }

        this.levelCurrentTick++;

        this.unloadChunks();
        this.timings.doTickPending.startTiming();
        while (this.updateQueue.peek() != null && this.updateQueue.peek().priority <= currentTick) {
            Block block = this.getBlock((Vector3) this.updateQueue.poll().data);
            this.updateQueueIndex.remove(Level.blockHash((int) block.x, (int) block.y, (int) block.z));
            block.onUpdate(BLOCK_UPDATE_SCHEDULED);
        }
        this.timings.doTickPending.stopTiming();

        TimingsHistory.entityTicks += this.updateEntities.size();
        this.timings.entityTick.startTiming();
        for (long id : new ArrayList<>(this.updateEntities.keySet())) {
            Entity entity = this.updateEntities.get(id);
            if (entity.closed || !entity.onUpdate(currentTick)) {
                this.updateEntities.remove(id);
            }
        }
        this.timings.entityTick.stopTiming();

        TimingsHistory.tileEntityTicks += this.updateBlockEntities.size();
        this.timings.blockEntityTick.startTiming();
        if (!this.updateBlockEntities.isEmpty()) {
            for (long id : new ArrayList<>(this.updateBlockEntities.keySet())) {
                if (!this.updateBlockEntities.get(id).onUpdate()) {
                    this.updateBlockEntities.remove(id);
                }
            }
        }
        this.timings.blockEntityTick.stopTiming();

        this.timings.tickChunks.startTiming();
        this.tickChunks();
        this.timings.tickChunks.stopTiming();

        if (!this.changedBlocks.isEmpty()) {
            if (!this.players.isEmpty()) {
                for (String index : new ArrayList<>(this.changedBlocks.keySet())) {
                    Map<String, Vector3> blocks = this.changedBlocks.get(index);
                    this.chunkCache.remove(index);
                    Chunk.Entry chunkEntry = Level.getChunkXZ(index);
                    int chunkX = chunkEntry.chunkX;
                    int chunkZ = chunkEntry.chunkZ;
                    if (blocks.size() > 512) {
                        FullChunk chunk = this.getChunk(chunkX, chunkZ);
                        for (Player p : this.getChunkPlayers(chunkX, chunkZ).values()) {
                            p.onChunkChanged(chunk);
                        }
                    } else {
                        this.sendBlocks(this.getChunkPlayers(chunkX, chunkZ).values().stream().toArray(Player[]::new),
                                blocks.values().stream().toArray(Vector3[]::new), UpdateBlockPacket.FLAG_ALL);
                    }
                }
            } else {
                this.chunkCache = new HashMap<>();
            }

            this.changedBlocks = new HashMap<>();
        }

        this.processChunkRequest();

        if (this.sleepTicks > 0 && --this.sleepTicks <= 0) {
            this.checkSleep();
        }

        for (String index : this.moveToSend.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            for (MoveEntityPacket packet : this.moveToSend.get(index).values()) {
                this.addChunkPacket(entry.chunkX, entry.chunkZ, packet);
            }
        }
        this.moveToSend = new HashMap<>();

        for (String index : this.motionToSend.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            SetEntityMotionPacket pk = new SetEntityMotionPacket();
            pk.entities = this.motionToSend.get(index).values().stream().toArray(SetEntityMotionPacket.Entry[]::new);
            this.addChunkPacket(entry.chunkX, entry.chunkZ, pk);
        }

        this.motionToSend = new HashMap<>();

        for (String index : this.playerMoveToSend.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            for (MovePlayerPacket packet : this.playerMoveToSend.get(index).values()) {
                this.addChunkPacket(entry.chunkX, entry.chunkZ, packet);
            }
        }
        this.playerMoveToSend = new HashMap<>();

        for (String key : this.chunkPackets.keySet()) {
            Chunk.Entry chunkEntry = Level.getChunkXZ(key);
            int chunkX = chunkEntry.chunkX;
            int chunkZ = chunkEntry.chunkZ;
            Player[] chunkPlayers = this.getChunkPlayers(chunkX, chunkZ).values().stream().toArray(Player[]::new);
            if (chunkPlayers.length > 0) {
                for (DataPacket pk : this.chunkPackets.get(key)) {
                    Server.broadcastPacket(chunkPlayers, pk);
                }
            }
        }

        this.chunkPackets = new HashMap<>();
        this.timings.doTick.stopTiming();
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

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Player[] players) {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_SET_DATA;
        pk.x = x + 0.5f;
        pk.y = y + 0.5f;
        pk.z = z + 0.5f;
        pk.data = (data << 8) | id;

        Server.broadcastPacket(players, pk);
    }

    public void sendBlockExtraData(int x, int y, int z, int id, int data, Collection<Player> players) {
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
        this.sendBlocks(target, blocks, flags, false);
    }

    public void sendBlocks(Player[] target, Vector3[] blocks, int flags, boolean optimizeRebuilds) {
        List<UpdateBlockPacket> packets = new ArrayList<>();
        UpdateBlockPacket packet = null;
        if (optimizeRebuilds) {
            Map<String, Boolean> chunks = new HashMap<>();
            for (Vector3 b : blocks) {
                if (b == null) {
                    continue;
                }
                packet = new UpdateBlockPacket();
                boolean first = false;

                String index = Level.chunkHash((int) b.x >> 4, (int) b.z >> 4);
                if (!chunks.containsKey(index)) {
                    chunks.put(index, true);
                    first = true;
                }

                if (b instanceof Block) {
                    List<UpdateBlockPacket.Entry> list = new ArrayList<>();
                    Collections.addAll(list, packet.records);
                    list.add(new UpdateBlockPacket.Entry((int) ((Block) b).x, (int) ((Block) b).z, (int) ((Block) b).y,
                            ((Block) b).getId(), ((Block) b).getDamage(), first ? flags : UpdateBlockPacket.FLAG_NONE));
                    packet.records = list.stream().toArray(UpdateBlockPacket.Entry[]::new);
                } else {
                    int fullBlock = this.getFullBlock((int) b.x, (int) b.y, (int) b.z);
                    List<UpdateBlockPacket.Entry> list = new ArrayList<>();
                    Collections.addAll(list, packet.records);
                    list.add(new UpdateBlockPacket.Entry((int) b.x, (int) b.z, (int) b.y, fullBlock >> 4,
                            fullBlock & 0xf, first ? flags : UpdateBlockPacket.FLAG_NONE));
                    packet.records = list.stream().toArray(UpdateBlockPacket.Entry[]::new);
                }
                packets.add(packet);
            }
        } else {
            for (Vector3 b : blocks) {
                if (b == null) {
                    continue;
                }
                packet = new UpdateBlockPacket();
                if (b instanceof Block) {
                    List<UpdateBlockPacket.Entry> list = new ArrayList<>();
                    Collections.addAll(list, packet.records);
                    list.add(new UpdateBlockPacket.Entry((int) ((Block) b).x, (int) ((Block) b).z, (int) ((Block) b).y,
                            ((Block) b).getId(), ((Block) b).getDamage(), flags));
                    packet.records = list.stream().toArray(UpdateBlockPacket.Entry[]::new);
                } else {
                    int fullBlock = this.getFullBlock((int) b.x, (int) b.y, (int) b.z);
                    List<UpdateBlockPacket.Entry> list = new ArrayList<>();
                    Collections.addAll(list, packet.records);
                    list.add(new UpdateBlockPacket.Entry((int) b.x, (int) b.z, (int) b.y, fullBlock >> 4,
                            fullBlock & 0xf, flags));
                    packet.records = list.stream().toArray(UpdateBlockPacket.Entry[]::new);
                }
                packets.add(packet);
            }
        }
        this.server.batchPackets(target, packets.toArray(new DataPacket[packets.size()]));
    }

    public void clearCache() {
        this.clearCache(false);
    }

    public void clearCache(boolean full) {
        if (full) {
            this.chunkCache = new HashMap<>();
            this.blockCache = new HashMap<>();
        } else {
            if (this.chunkCache.size() > 2048) {
                this.chunkCache = new HashMap<>();
            }

            if (this.blockCache.size() > 2048) {
                this.blockCache = new HashMap<>();
            }
        }
    }

    public void clearChunkCache(int chunkX, int chunkZ) {
        this.chunkCache.remove(Level.chunkHash(chunkX, chunkZ));
    }

    private void tickChunks() {
        if (this.chunksPerTicks <= 0 || this.loaders.isEmpty()) {
            this.chunkTickList = new HashMap<>();
            return;
        }

        int chunksPerLoader = Math.min(200, Math.max(1, (int) (((double) (this.chunksPerTicks - this.loaders.size()) / this.loaders.size() + 0.5))));
        int randRange = 3 + chunksPerLoader / 30;
        randRange = randRange > this.chunkTickRadius ? this.chunkTickRadius : randRange;

        for (ChunkLoader loader : this.loaders.values()) {
            int chunkX = (int) loader.getX() >> 4;
            int chunkZ = (int) loader.getZ() >> 4;

            String index = Level.chunkHash(chunkX, chunkZ);
            int existingLoaders = Math.max(0, this.chunkTickList.containsKey(index) ? this.chunkTickList.get(index) : 0);
            this.chunkTickList.put(index, existingLoaders + 1);
            for (int chunk = 0; chunk < chunksPerLoader; ++chunk) {
                int dx = new java.util.Random().nextInt(2 * randRange) - randRange;
                int dz = new java.util.Random().nextInt(2 * randRange) - randRange;
                String hash = Level.chunkHash(dx + chunkX, dz + chunkZ);
                if (!this.chunkTickList.containsKey(hash) && this.chunks.containsKey(hash)) {
                    this.chunkTickList.put(hash, -1);
                }
            }
        }

        int blockTest = 0;

        for (String index : new ArrayList<>(this.chunkTickList.keySet())) {
            int loaders = this.chunkTickList.get(index);

            Chunk.Entry chunkEntry = Level.getChunkXZ(index);
            int chunkX = chunkEntry.chunkX;
            int chunkZ = chunkEntry.chunkZ;

            FullChunk chunk;
            if (!this.chunks.containsKey(index) || (chunk = this.getChunk(chunkX, chunkZ, false)) == null) {
                this.chunkTickList.remove(index);
                continue;
            } else if (loaders <= 0) {
                this.chunkTickList.remove(index);
            }

            for (Entity entity : chunk.getEntities().values()) {
                entity.scheduleUpdate();
            }

            int blockId;
            if (this.useSections) {
                for (ChunkSection section : ((Chunk) chunk).getSections()) {
                    if (!(section instanceof EmptyChunkSection)) {
                        int Y = section.getY();
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int k = this.updateLCG >> 2;
                        for (int i = 0; i < 3; ++i, k >>= 10) {
                            int x = k & 0x0f;
                            int y = k >> 8 & 0x0f;
                            int z = k >> 16 & 0x0f;

                            blockId = section.getBlockId(x, y, z);
                            if (this.randomTickBlocks.containsKey(blockId)) {
                                Class<? extends Block> clazz = this.randomTickBlocks.get(blockId);
                                try {
                                    Block block = clazz.getConstructor(int.class).newInstance(section.getBlockData(x, y, z));
                                    block.x = chunkX * 16 + x;
                                    block.y = (Y << 4) + y;
                                    block.z = chunkZ * 16 + z;
                                    block.level = this;
                                    block.onUpdate(BLOCK_UPDATE_RANDOM);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            } else {
                for (int Y = 0; Y < 8 && (Y < 3 || blockTest != 0); ++Y) {
                    blockTest = 0;
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int k = this.updateLCG >> 2;
                    for (int i = 0; i < 3; ++i, k >>= 10) {
                        int x = k & 0x0f;
                        int y = k >> 8 & 0x0f;
                        int z = k >> 16 & 0x0f;

                        blockTest |= blockId = chunk.getBlockId(x, y + (Y << 4), z);
                        if (this.randomTickBlocks.containsKey(blockId)) {
                            Class<? extends Block> clazz = this.randomTickBlocks.get(blockId);

                            Block block;
                            try {
                                block = clazz.getConstructor(int.class).newInstance(chunk.getBlockData(x, y + (Y << 4), z));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            block.x = chunkX * 16 + x;
                            block.y = (Y << 4) + y;
                            block.z = chunkZ * 16 + z;
                            block.level = this;
                            block.onUpdate(BLOCK_UPDATE_RANDOM);
                        }
                    }
                }
            }
        }

        if (this.clearChunksOnTick) {
            this.chunkTickList = new HashMap<>();
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

        this.provider.setTime((int) this.time);
        this.provider.setRaining(this.raining);
        this.provider.setRainTime(this.rainTime);
        this.provider.setThundering(this.thundering);
        this.provider.setThunderTime(this.thunderTime);
        this.provider.setCurrentTick(this.levelCurrentTick);
        this.saveChunks();
        if (this.provider instanceof BaseLevelProvider) {
            this.provider.saveLevelData();
        }

        return true;
    }

    public void saveChunks() {
        for (FullChunk chunk : new ArrayList<>(this.chunks.values())) {
            if (chunk.hasChanged()) {
                try {
                    this.provider.setChunk(chunk.getX(), chunk.getZ(), chunk);
                    this.provider.saveChunk(chunk.getX(), chunk.getZ());

                    chunk.setChanged(false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void updateAround(Vector3 pos) {
        BlockUpdateEvent ev;
        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(this.temporalVector.setComponents(pos.x, pos.y - 1, pos.z))));
        if (!ev.isCancelled()) {
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(this.temporalVector.setComponents(pos.x, pos.y + 1, pos.z))));
        if (!ev.isCancelled()) {
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(this.temporalVector.setComponents(pos.x - 1, pos.y, pos.z))));
        if (!ev.isCancelled()) {
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(this.temporalVector.setComponents(pos.x + 1, pos.y, pos.z))));
        if (!ev.isCancelled()) {
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(this.temporalVector.setComponents(pos.x, pos.y, pos.z - 1))));
        if (!ev.isCancelled()) {
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
        }

        this.server.getPluginManager().callEvent(
                ev = new BlockUpdateEvent(this.getBlock(this.temporalVector.setComponents(pos.x, pos.y, pos.z + 1))));
        if (!ev.isCancelled()) {
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
        }
    }

    public void updateAroundRedstone(Block block) {
        BlockUpdateEvent ev = new BlockUpdateEvent(block);
        this.server.getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            for (Entity entity : this.getNearbyEntities(
                    new AxisAlignedBB(block.x - 1, block.y - 1, block.z - 1, block.x + 1, block.y + 1, block.z + 1))) {
                entity.scheduleUpdate();
            }
            ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);

            RedstoneUpdateEvent rsEv = new RedstoneUpdateEvent(ev.getBlock());
            this.server.getPluginManager().callEvent(rsEv);
            if (!rsEv.isCancelled()) {
                Block redstoneWire = rsEv.getBlock().getSide(Vector3.SIDE_DOWN);
                if (redstoneWire instanceof BlockRedstoneWire) {
                    if (rsEv.getBlock() instanceof BlockSolid) {
                        int level = redstoneWire.getPowerLevel();
                        redstoneWire.setPowerLevel(redstoneWire.getNeighborPowerLevel() - 1);
                        redstoneWire.getLevel().setBlock(redstoneWire, redstoneWire, true, true);
                        Redstone.deactive(redstoneWire, level);
                    } else {
                        redstoneWire.setPowerLevel(redstoneWire.getNeighborPowerLevel() - 1);
                        redstoneWire.getLevel().setBlock(redstoneWire, redstoneWire, true, true);
                        Redstone.active(redstoneWire);
                    }
                }
            }
        }
    }

    public void scheduleUpdate(Vector3 pos, int delay) {
        String index = Level.blockHash((int) pos.x, (int) pos.y, (int) pos.z);
        if (this.updateQueueIndex.containsKey(index) && this.updateQueueIndex.get(index) <= delay) {
            return;
        }
        this.updateQueueIndex.put(index, delay);
        this.updateQueue.add(
                new PriorityObject(new Vector3((int) pos.x, (int) pos.y, (int) pos.z), delay + this.server.getTick()));
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb) {
        return this.getCollisionBlocks(bb, false);
    }

    public Block[] getCollisionBlocks(AxisAlignedBB bb, boolean targetFirst) {
        int minX = NukkitMath.floorDouble(bb.minX);
        int minY = NukkitMath.floorDouble(bb.minY);
        int minZ = NukkitMath.floorDouble(bb.minZ);
        int maxX = NukkitMath.ceilDouble(bb.maxX);
        int maxY = NukkitMath.ceilDouble(bb.maxY);
        int maxZ = NukkitMath.ceilDouble(bb.maxZ);

        List<Block> collides = new ArrayList<>();

        if (targetFirst) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
                        if (block.getId() != 0 && block.collidesWithBB(bb)) {
                            return new Block[]{block};
                        }
                    }
                }
            }
        } else {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
                        if (block.getId() != 0 && block.collidesWithBB(bb)) {
                            collides.add(block);
                        }
                    }
                }
            }
        }

        return collides.stream().toArray(Block[]::new);
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
        int minX = NukkitMath.floorDouble(bb.minX);
        int minY = NukkitMath.floorDouble(bb.minY);
        int minZ = NukkitMath.floorDouble(bb.minZ);
        int maxX = NukkitMath.ceilDouble(bb.maxX);
        int maxY = NukkitMath.ceilDouble(bb.maxY);
        int maxZ = NukkitMath.ceilDouble(bb.maxZ);

        List<AxisAlignedBB> collides = new ArrayList<>();

        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    Block block = this.getBlock(this.temporalVector.setComponents(x, y, z));
                    if (!block.canPassThrough() && block.collidesWithBB(bb)) {
                        collides.add(block.getBoundingBox());
                    }
                }
            }
        }

        if (entities) {
            for (Entity ent : this.getCollidingEntities(bb.grow(0.25f, 0.25f, 0.25f), entity)) {
                collides.add(ent.boundingBox.clone());
            }
        }

        return collides.stream().toArray(AxisAlignedBB[]::new);
    }

    public int getFullLight(Vector3 pos) {
        FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);
        int level = 0;
        if (chunk != null) {
            level = chunk.getBlockSkyLight((int) pos.x & 0x0f, (int) pos.y & 0x7f, (int) pos.z & 0x0f);
            // TODO: decrease light level by time of day
            if (level < 15) {
                level = Math.max(chunk.getBlockLight((int) pos.x & 0x0f, (int) pos.y & 0x7f, (int) pos.z & 0x0f),
                        level);
                // todo: check this
            }
        }

        return level;
    }

    public int getFullBlock(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, false).getFullBlock(x & 0x0f, y & 0x7f, z & 0x0f);
    }

    public Block getBlock(Vector3 pos) {
        return this.getBlock(pos, true);
    }

    public Block getBlock(Vector3 pos, boolean cached) {
        String chunkIndex = Level.chunkHash((int) pos.x >> 4, (int) pos.z >> 4);
        String index = Level.blockHash((int) pos.x, (int) pos.y, (int) pos.z);
        int fullState = 0;
        if (cached && this.blockCache.containsKey(index)) {
            return this.blockCache.get(index);
        } else if (pos.y >= 0 && pos.y < 128 && this.chunks.containsKey(chunkIndex)) {
            fullState = this.chunks.get(chunkIndex).getFullBlock((int) pos.x & 0x0f, (int) pos.y & 0x7f,
                    (int) pos.z & 0x0f);
        } else {
            fullState = 0;
        }

        Block block = this.blockStates[fullState & 0xfff].clone();

        block.x = pos.x;
        block.y = pos.y;
        block.z = pos.z;
        block.level = this;

        this.blockCache.put(index, block);

        return block;
    }

    public void updateAllLight(Vector3 pos) {
        this.updateBlockSkyLight((int) pos.x, (int) pos.y, (int) pos.z);
        this.updateBlockLight((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public void updateBlockSkyLight(int x, int y, int z) {
        // todo
    }

    public void updateBlockLight(int x, int y, int z) {
        Queue<Vector3> lightPropagationQueue = new ConcurrentLinkedQueue<>();
        Queue<Object[]> lightRemovalQueue = new ConcurrentLinkedQueue<>();
        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Boolean> removalVisited = new HashMap<>();

        int oldLevel = this.getBlockLightAt(x, y, z);
        int newLevel = Block.light[this.getBlockIdAt(x, y, z)];

        if (oldLevel != newLevel) {
            this.setBlockLightAt(x, y, z, newLevel);

            if (newLevel < oldLevel) {
                removalVisited.put(Level.blockHash(x, y, z), true);
                lightRemovalQueue.add(new Object[]{new Vector3(x, y, z), oldLevel});
            } else {
                visited.put(Level.blockHash(x, y, z), true);
                lightPropagationQueue.add(new Vector3(x, y, z));
            }
        }

        while (!lightRemovalQueue.isEmpty()) {
            Object[] val = lightRemovalQueue.poll();
            Vector3 node = (Vector3) val[0];
            int lightLevel = (int) val[1];

            this.computeRemoveBlockLight((int) node.x - 1, (int) node.y, (int) node.z, lightLevel, lightRemovalQueue,
                    lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight((int) node.x + 1, (int) node.y, (int) node.z, lightLevel, lightRemovalQueue,
                    lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight((int) node.x, (int) node.y - 1, (int) node.z, lightLevel, lightRemovalQueue,
                    lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight((int) node.x, (int) node.y + 1, (int) node.z, lightLevel, lightRemovalQueue,
                    lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight((int) node.x, (int) node.y, (int) node.z - 1, lightLevel, lightRemovalQueue,
                    lightPropagationQueue, removalVisited, visited);
            this.computeRemoveBlockLight((int) node.x, (int) node.y, (int) node.z + 1, lightLevel, lightRemovalQueue,
                    lightPropagationQueue, removalVisited, visited);
        }

        while (!lightPropagationQueue.isEmpty()) {
            Vector3 node = lightPropagationQueue.poll();
            int lightLevel = this.getBlockLightAt((int) node.x, (int) node.y, (int) node.z)
                    - Block.lightFilter[this.getBlockIdAt((int) node.x, (int) node.y, (int) node.z)];

            if (lightLevel >= 1) {
                this.computeSpreadBlockLight((int) node.x - 1, (int) node.y, (int) node.z, lightLevel,
                        lightPropagationQueue, visited);
                this.computeSpreadBlockLight((int) node.x + 1, (int) node.y, (int) node.z, lightLevel,
                        lightPropagationQueue, visited);
                this.computeSpreadBlockLight((int) node.x, (int) node.y - 1, (int) node.z, lightLevel,
                        lightPropagationQueue, visited);
                this.computeSpreadBlockLight((int) node.x, (int) node.y + 1, (int) node.z, lightLevel,
                        lightPropagationQueue, visited);
                this.computeSpreadBlockLight((int) node.x, (int) node.y, (int) node.z - 1, lightLevel,
                        lightPropagationQueue, visited);
                this.computeSpreadBlockLight((int) node.x, (int) node.y, (int) node.z + 1, lightLevel,
                        lightPropagationQueue, visited);
            }
        }
    }

    private void computeRemoveBlockLight(int x, int y, int z, int currentLight, Queue<Object[]> queue,
                                         Queue<Vector3> spreadQueue, Map<String, Boolean> visited, Map<String, Boolean> spreadVisited) {
        int current = this.getBlockLightAt(x, y, z);
        String index = Level.blockHash(x, y, z);
        if (current != 0 && current < currentLight) {
            this.setBlockLightAt(x, y, z, 0);

            if (!visited.containsKey(index)) {
                visited.put(index, true);
                if (current > 1) {
                    queue.add(new Object[]{new Vector3(x, y, z), current});
                }
            }
        } else if (current >= currentLight) {
            if (!spreadVisited.containsKey(index)) {
                spreadVisited.put(index, true);
                spreadQueue.add(new Vector3(x, y, z));
            }
        }
    }

    private void computeSpreadBlockLight(int x, int y, int z, int currentLight, Queue<Vector3> queue,
                                         Map<String, Boolean> visited) {
        int current = this.getBlockLightAt(x, y, z);
        String index = Level.blockHash(x, y, z);

        if (current < currentLight) {
            this.setBlockLightAt(x, y, z, currentLight);

            if (!visited.containsKey(index)) {
                visited.put(index, true);
                if (currentLight > 1) {
                    queue.add(new Vector3(x, y, z));
                }
            }
        }
    }

    public boolean setBlock(Vector3 pos, Block block) {
        return this.setBlock(pos, block, false);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct) {
        return this.setBlock(pos, block, direct, true);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct, boolean update) {
        if (pos.y < 0 || pos.y >= 128) {
            return false;
        }

        if (this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, true).setBlock((int) pos.x & 0x0f, (int) pos.y & 0x7f,
                (int) pos.z & 0x0f, block.getId(), block.getDamage())) {
            Position position;
            if (!(pos instanceof Position)) {
                position = this.temporalPosition.setComponents(pos.x, pos.y, pos.z);
            } else {
                position = (Position) pos;
            }

            block.position(position);
            this.blockCache.remove(Level.blockHash((int) position.x, (int) position.y, (int) position.z));

            String index = Level.chunkHash((int) position.x >> 4, (int) position.z >> 4);

            if (direct) {
                this.sendBlocks(this.getChunkPlayers((int) position.x >> 4, (int) position.z >> 4).values().stream()
                        .toArray(Player[]::new), new Block[]{block}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                this.chunkCache.remove(index);
            } else {
                if (!this.changedBlocks.containsKey(index)) {
                    this.changedBlocks.put(index, new HashMap<>());
                }

                this.changedBlocks.get(index).put(Level.blockHash((int) block.x, (int) block.y, (int) block.z),
                        block.clone());
            }

            for (ChunkLoader loader : this.getChunkLoaders((int) position.x >> 4, (int) position.z >> 4)) {
                loader.onBlockChanged(block);
            }

            if (update) {
                this.updateAllLight(block);
                BlockUpdateEvent ev = new BlockUpdateEvent(block);
                this.server.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    for (Entity entity : this.getNearbyEntities(new AxisAlignedBB(block.x - 1, block.y - 1, block.z - 1,
                            block.x + 1, block.y + 1, block.z + 1))) {
                        entity.scheduleUpdate();
                    }
                    ev.getBlock().onUpdate(BLOCK_UPDATE_NORMAL);
                }
                this.updateAroundRedstone(block);
                this.updateAround(position);
            }

            return true;
        }

        return false;
    }

    public void dropItem(Vector3 source, Item item) {
        this.dropItem(source, item, null);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion) {
        this.dropItem(source, item, motion, 10);
    }

    public void dropItem(Vector3 source, Item item, Vector3 motion, int delay) {
        motion = motion == null ? new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                new java.util.Random().nextDouble() * 0.2 - 0.1) : motion;

        CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");

        if (item.getId() > 0 && item.getCount() > 0) {
            EntityItem itemEntity = new EntityItem(
                    this.getChunk((int) source.getX() >> 4, (int) source.getZ() >> 4, true),
                    new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
                            .add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))

                            .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.x))
                                    .add(new DoubleTag("", motion.y)).add(new DoubleTag("", motion.z)))

                            .putList(new ListTag<FloatTag>("Rotation")
                                    .add(new FloatTag("", new java.util.Random().nextFloat() * 360))
                                    .add(new FloatTag("", 0)))

                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", delay));

            itemEntity.spawnToAll();
        }
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
        if (player != null && player.getGamemode() > 1) {
            return null;
        }
        Block target = this.getBlock(vector);
        Item[] drops;
        if (item == null) {
            item = new ItemBlock(new BlockAir(), 0, 0);
        }

        if (player != null) {
            double breakTime = target.getBreakTime(item); // TODO: fix
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

            //breakTime -= 0.1;
            //TODO: Check if it's necessary to minus breakTime with 0.1.

            BlockBreakEvent ev = new BlockBreakEvent(player, target, item, player.isCreative(),
                    (player.lastBreak + breakTime * 1000) > System.currentTimeMillis());
            double distance;
            if (player.isSurvival() && !target.isBreakable(item)) {
                ev.setCancelled();
            } else if (!player.isOp() && (distance = this.server.getSpawnRadius()) > -1) {
                Vector2 t = new Vector2(target.x, target.z);
                Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
                if (!this.server.getOps().getAll().isEmpty() && t.distance(s) <= distance) {
                    ev.setCancelled();
                }
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
        } else if (!target.isBreakable(item)) {
            return null;
        } else {
            int[][] d = target.getDrops(item);
            drops = new Item[d.length];
            for (int i = 0; i < d.length; i++) {
                drops[i] = Item.get(d[i][0], d[i][1], d[i][2]);
            }
        }

        Block above = this.getBlock(new Vector3(target.x, target.y + 1, target.z));
        if (above != null) {
            if (above.getId() == Item.FIRE) {
                this.setBlock(above, new BlockAir(), true);
            }
        }

        Tag tag = item.getNamedTagEntry("CanDestroy");
        if (tag instanceof ListTag) {
            boolean canBreak = false;
            for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                if (v instanceof StringTag) {
                    Item entry = Item.fromString(((StringTag) v).data);
                    if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() == target.getId()) {
                        canBreak = true;
                        break;
                    }
                }
            }

            if (!canBreak) {
                return null;
            }
        }

        if (createParticles) {
            Map<Integer, Player> players = this.getChunkPlayers((int) target.x >> 4, (int) target.z >> 4);

            this.addParticle(new DestroyBlockParticle(target.add(0.5), target), players.values());

            if (player != null) {
                players.remove(player.getLoaderId());
            }
        }

        target.onBreak(item);

        BlockEntity blockEntity = this.getBlockEntity(target);
        if (blockEntity != null) {
            if (blockEntity instanceof InventoryHolder) {
                if (blockEntity instanceof BlockEntityChest) {
                    ((BlockEntityChest) blockEntity).unpair();
                }

                for (Item chestItem : ((InventoryHolder) blockEntity).getInventory().getContents().values()) {
                    this.dropItem(target, chestItem);
                }
            }

            blockEntity.close();
        }

        item.useOn(target);
        if (item.isTool() && item.getDamage() >= item.getMaxDurability()) {
            item = new ItemBlock(new BlockAir(), 0, 0);
        }

        int dropExp = target.getDropExp();
        if (player != null) {
            player.addExperience(dropExp);
            if (player.isSurvival()) {
                for (int ii = 1; ii <= dropExp; ii++) {
                    this.dropExpOrb(target, 1);
                }
            }
        }

        if (player == null || player.isSurvival()) {
            for (Item drop : drops) {
                if (drop.getCount() > 0) {
                    this.dropItem(vector.add(0.5, 0.5, 0.5), drop);
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
        motion = (motion == null) ? new Vector3(new java.util.Random().nextDouble() * 0.2 - 0.1, 0.2,
                new java.util.Random().nextDouble() * 0.2 - 0.1) : motion;
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.getX()))
                        .add(new DoubleTag("", source.getY())).add(new DoubleTag("", source.getZ())))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", motion.getX()))
                        .add(new DoubleTag("", motion.getY())).add(new DoubleTag("", motion.getZ())))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", 0)).add(new FloatTag("", 0)));
        Entity entity = new EntityXPOrb(this.getChunk(source.getFloorX() >> 4, source.getFloorZ() >> 4), nbt);
        EntityXPOrb xpOrb = (EntityXPOrb) entity;
        xpOrb.setExp(exp);
        xpOrb.setPickupDelay(delay);
        xpOrb.saveNBT();

        xpOrb.spawnToAll();

    }

    public Item useItemOn(Vector3 vector, Item item, int face, float fx, float fy, float fz) {
        return this.useItemOn(vector, item, face, fx, fy, fz, null);
    }

    public Item useItemOn(Vector3 vector, Item item, int face, float fx, float fy, float fz, Player player) {
        Block target = this.getBlock(vector);
        Block block = target.getSide(face);

        if (block.y > 127 || block.y < 0) {
            return null;
        }

        if (target.getId() == Item.AIR) {
            return null;
        }

        if (player != null) {
            PlayerInteractEvent ev = new PlayerInteractEvent(player, item, target, face,
                    target.getId() == 0 ? PlayerInteractEvent.RIGHT_CLICK_AIR : PlayerInteractEvent.RIGHT_CLICK_BLOCK);

            if (player.getGamemode() > 2) {
                ev.setCancelled();
            }

            int distance = this.server.getSpawnRadius();
            if (!player.isOp() && distance > -1) {
                Vector2 t = new Vector2(target.x, target.z);
                Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
                if (!this.server.getOps().getAll().isEmpty() && t.distance(s) <= distance) {
                    ev.setCancelled();
                }
            }

            this.server.getPluginManager().callEvent(ev);
            if (!ev.isCancelled()) {
                target.onUpdate(BLOCK_UPDATE_TOUCH);
                if (!player.isSneaking() && target.canBeActivated() && target.onActivate(item, player)) {
                    return item;
                }

                if (!player.isSneaking() && item.canBeActivated()
                        && item.onActivate(this, player, block, target, face, fx, fy, fz)) {
                    if (item.getCount() <= 0) {
                        item = new ItemBlock(new BlockAir(), 0, 0);
                        return item;
                    }
                }
            } else {
                return null;
            }
        } else if (target.canBeActivated() && target.onActivate(item, null)) {
            return item;
        }
        Block hand;
        if (item.canBePlaced()) {
            hand = item.getBlock();
            hand.position(block);
        } else {
            return null;
        }

        if (!(block.canBeReplaced() || (hand.getId() == Item.SLAB && block.getId() == Item.SLAB))) {
            return null;
        }

        if (target.canBeReplaced()) {
            block = target;
            hand.position(block);
        }

        if (hand.isSolid() && hand.getBoundingBox() != null) {
            Entity[] entities = this.getCollidingEntities(hand.getBoundingBox());
            int realCount = 0;
            for (Entity e : entities) {
                if (e instanceof EntityArrow || e instanceof EntityItem) {
                    continue;
                }
                ++realCount;
            }

            if (player != null) {
                Vector3 diff = player.getNextPosition().subtract(player.getPosition());
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

        Tag tag = item.getNamedTagEntry("CanPlaceOn");
        if (tag instanceof ListTag) {
            boolean canPlace = false;
            for (Tag v : ((ListTag<Tag>) tag).getAll()) {
                if (v instanceof StringTag) {
                    Item entry = Item.fromString(((StringTag) v).data);
                    if (entry.getId() > 0 && entry.getBlock() != null && entry.getBlock().getId() == target.getId()) {
                        canPlace = true;
                        break;
                    }
                }
            }

            if (!canPlace) {
                return null;
            }
        }

        if (player != null) {
            BlockPlaceEvent event = new BlockPlaceEvent(player, hand, block, target, item);
            int distance = this.server.getSpawnRadius();
            if (!player.isOp() && distance > -1) {
                Vector2 t = new Vector2(target.x, target.z);
                Vector2 s = new Vector2(this.getSpawnLocation().x, this.getSpawnLocation().z);
                if (!this.server.getOps().getAll().isEmpty() && t.distance(s) <= distance) {
                    event.setCancelled();
                }
            }

            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
        }

        if (!hand.place(item, block, target, face, fx, fy, fz, player)) {
            return null;
        }

        if (player != null && !player.isCreative()) {
            item.setCount(item.getCount() - 1);
        }

        if (item.getCount() <= 0) {
            item = new ItemBlock(new BlockAir(), 0, 0);
        }
        return item;
    }

    public Entity getEntity(long entityId) {
        return this.entities.containsKey(entityId) ? this.entities.get(entityId) : null;
    }

    public Entity[] getEntities() {
        return entities.values().stream().toArray(Entity[]::new);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb) {
        return this.getCollidingEntities(bb, null);
    }

    public Entity[] getCollidingEntities(AxisAlignedBB bb, Entity entity) {
        List<Entity> nearby = new ArrayList<>();

        if (entity == null || entity.canCollide()) {
            int minX = NukkitMath.floorDouble((bb.minX - 2) / 16);
            int maxX = NukkitMath.ceilDouble((bb.maxX + 2) / 16);
            int minZ = NukkitMath.floorDouble((bb.minZ - 2) / 16);
            int maxZ = NukkitMath.ceilDouble((bb.maxZ + 2) / 16);

            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    for (Entity ent : this.getChunkEntities(x, z).values()) {
                        if ((entity == null || (ent != entity && entity.canCollideWith(ent)))
                                && ent.boundingBox.intersectsWith(bb)) {
                            nearby.add(ent);
                        }
                    }
                }
            }
        }

        return nearby.stream().toArray(Entity[]::new);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb) {
        return this.getNearbyEntities(bb, null);
    }

    public Entity[] getNearbyEntities(AxisAlignedBB bb, Entity entity) {
        List<Entity> nearby = new ArrayList<>();

        int minX = NukkitMath.floorDouble((bb.minX - 2) / 16);
        int maxX = NukkitMath.ceilDouble((bb.maxX + 2) / 16);
        int minZ = NukkitMath.floorDouble((bb.minZ - 2) / 16);
        int maxZ = NukkitMath.ceilDouble((bb.maxZ + 2) / 16);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (Entity ent : this.getChunkEntities(x, z).values()) {
                    if (ent != entity && ent.boundingBox.intersectsWith(bb)) {
                        nearby.add(ent);
                    }
                }
            }
        }

        return nearby.stream().toArray(Entity[]::new);
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
        FullChunk chunk = this.getChunk((int) pos.x >> 4, (int) pos.z >> 4, false);

        if (chunk != null) {
            return chunk.getTile((int) pos.x & 0x0f, (int) pos.y & 0xff, (int) pos.z & 0x0f);
        }

        return null;
    }

    public Map<Long, Entity> getChunkEntities(int X, int Z) {
        FullChunk chunk;
        return (chunk = this.getChunk(X, Z)) != null ? chunk.getEntities() : new HashMap<>();
    }

    public Map<Long, BlockEntity> getChunkBlockEntities(int X, int Z) {
        FullChunk chunk;
        return (chunk = this.getChunk(X, Z)) != null ? chunk.getBlockEntities() : new HashMap<>();
    }

    @Override
    public int getBlockIdAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockId(x & 0x0f, y & 0x7f, z & 0x0f);
    }

    @Override
    public void setBlockIdAt(int x, int y, int z, int id) {
        this.blockCache.remove(Level.blockHash(x, y, z));
        this.getChunk(x >> 4, z >> 4, true).setBlockId(x & 0x0f, y & 0x7f, z & 0x0f, id & 0xff);

        String index = Level.chunkHash(x >> 4, z >> 4);
        if (!this.changedBlocks.containsKey(index)) {
            this.changedBlocks.put(index, new HashMap<>());
        }
        Vector3 v;
        this.changedBlocks.get(index).put(Level.blockHash(x, y, z), v = new Vector3(x, y, z));
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(v);
        }
    }

    public int getBlockExtraDataAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockExtraData(x & 0x0f, y & 0x7f, z & 0x0f);
    }

    public void setBlockExtraDataat(int x, int y, int z, int id, int data) {
        this.getChunk(x >> 4, z >> 4, true).setBlockExtraData(x & 0x0f, y & 0x7f, z & 0x0f, (data << 8) | id);

        this.sendBlockExtraData(x, y, z, id, data);
    }

    @Override
    public int getBlockDataAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockData(x & 0x0f, y & 0x7f, z & 0x0f);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        this.blockCache.remove(Level.blockHash(x, y, z));
        this.getChunk(x >> 4, z >> 4, true).setBlockData(x & 0x0f, y & 0x7f, z & 0x0f, data & 0x0f);

        String index = Level.chunkHash(x >> 4, z >> 4);
        if (!this.changedBlocks.containsKey(index)) {
            this.changedBlocks.put(index, new HashMap<>());
        }
        Vector3 v;
        this.changedBlocks.get(index).put(Level.blockHash(x, y, z), v = new Vector3(x, y, z));
        for (ChunkLoader loader : this.getChunkLoaders(x >> 4, z >> 4)) {
            loader.onBlockChanged(v);
        }
    }

    public int getBlockSkyLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockSkyLight(x & 0x0f, y & 0x7f, z & 0x0f);
    }

    public void setBlockSkyLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockSkyLight(x & 0x0f, y & 0x7f, z & 0x0f, level & 0x0f);
    }

    public int getBlockLightAt(int x, int y, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBlockLight(x & 0x0f, y & 0x7f, z & 0x0f);
    }

    public void setBlockLightAt(int x, int y, int z, int level) {
        this.getChunk(x >> 4, z >> 4, true).setBlockLight(x & 0x0f, y & 0x7f, z & 0x0f, level & 0x0f);
    }

    public int getBiomeId(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBiomeId(x & 0x0f, z & 0x0f);
    }

    public void setBiomeId(int x, int z, int biomeId) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeId(x & 0x0f, z & 0x0f, biomeId & 0x0f);
    }

    public int getHeightMap(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getHeightMap(x & 0x0f, z & 0x0f);
    }

    public void setHeightMap(int x, int z, int value) {
        this.getChunk(x >> 4, z >> 4, true).setHeightMap(x & 0x0f, z & 0x0f, value & 0x0f);
    }

    public int[] getBiomeColor(int x, int z) {
        return this.getChunk(x >> 4, z >> 4, true).getBiomeColor(x & 0x0f, z & 0x0f);
    }

    public void setBiomeColor(int x, int z, int R, int G, int B) {
        this.getChunk(x >> 4, z >> 4, true).setBiomeColor(x & 0x0f, z & 0x0f, R, G, B);
    }

    public Map<String, BaseFullChunk> getChunks() {
        return chunks;
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.chunks.containsKey(index)) {
            return this.chunks.get(index);
        } else if (this.loadChunk(chunkX, chunkZ, create)) {
            return this.chunks.get(index);
        }

        return null;
    }

    public void generateChunkCallback(int x, int z, BaseFullChunk chunk) {
        Timings.generationCallbackTimer.startTiming();
        String index = Level.chunkHash(x, z);
        if (this.chunkPopulationQueue.containsKey(index)) {
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
            if (chunk != null && (oldChunk == null || !oldChunk.isPopulated()) && chunk.isPopulated()
                    && chunk.getProvider() != null) {
                this.server.getPluginManager().callEvent(new ChunkPopulateEvent(chunk));

                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
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

        String index = Level.chunkHash(chunkX, chunkZ);
        FullChunk oldChunk = this.getChunk(chunkX, chunkZ, false);
        if (unload && oldChunk != null) {
            this.unloadChunk(chunkX, chunkZ, false, false);

            this.provider.setChunk(chunkX, chunkZ, chunk);
            this.chunks.put(index, chunk);
        } else {
            Map<Long, Entity> oldEntities = oldChunk != null ? oldChunk.getEntities() : new HashMap<>();

            Map<Long, BlockEntity> oldBlockEntities = oldChunk != null ? oldChunk.getBlockEntities() : new HashMap<>();

            this.provider.setChunk(chunkX, chunkZ, chunk);
            this.chunks.put(index, chunk);

            for (Entity entity : oldEntities.values()) {
                chunk.addEntity(entity);
                entity.chunk = chunk;
            }

            for (BlockEntity blockEntity : oldBlockEntities.values()) {
                chunk.addBlockEntity(blockEntity);
                blockEntity.chunk = chunk;
            }
        }

        this.chunkCache.remove(index);
        chunk.setChanged();

        if (!this.isChunkInUse(chunkX, chunkZ)) {
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
        return this.chunks.containsKey(Level.chunkHash(x, z)) || this.provider.isChunkLoaded(x, z);
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
        this.provider.setSpawn(pos);
        this.server.getPluginManager().callEvent(new SpawnChangeEvent(this, previousSpawn));
    }

    public void requestChunk(int x, int z, Player player) {
        String index = Level.chunkHash(x, z);
        if (!this.chunkSendQueue.containsKey(index)) {
            this.chunkSendQueue.put(index, new HashMap<>());
        }

        this.chunkSendQueue.get(index).put(player.getLoaderId(), player);
    }

    private void sendChunkFromCache(int x, int z) {
        String index = Level.chunkHash(x, z);
        if (this.chunkSendTasks.containsKey(index)) {
            for (Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, this.chunkCache.get(index));
                }
            }

            this.chunkSendQueue.remove(index);
            this.chunkSendTasks.remove(index);
        }
    }

    private void processChunkRequest() {
        if (!this.chunkSendQueue.isEmpty()) {
            this.timings.syncChunkSendTimer.startTiming();
            for (String index : new ArrayList<>(this.chunkSendQueue.keySet())) {
                if (this.chunkSendTasks.containsKey(index)) {
                    continue;
                }
                Chunk.Entry chunkEntry = Level.getChunkXZ(index);
                int x = chunkEntry.chunkX;
                int z = chunkEntry.chunkZ;
                this.chunkSendTasks.put(index, true);
                if (this.chunkCache.containsKey(index)) {
                    this.sendChunkFromCache(x, z);
                    continue;
                }
                this.timings.syncChunkSendPrepareTimer.startTiming();
                AsyncTask task = this.provider.requestChunkTask(x, z);
                if (task != null) {
                    this.server.getScheduler().scheduleAsyncTask(task);
                }
                this.timings.syncChunkSendPrepareTimer.stopTiming();
            }
            this.timings.syncChunkSendTimer.stopTiming();
        }
    }

    public void chunkRequestCallback(int x, int z, byte[] payload) {
        this.chunkRequestCallback(x, z, payload, FullChunkDataPacket.ORDER_COLUMNS);
    }

    public void chunkRequestCallback(int x, int z, byte[] payload, byte ordering) {
        this.timings.syncChunkSendTimer.startTiming();
        String index = Level.chunkHash(x, z);

        if (this.cacheChunks && !this.chunkCache.containsKey(index)) {
            this.chunkCache.put(index, Player.getChunkCacheFromData(x, z, payload, ordering));
            this.sendChunkFromCache(x, z);
            this.timings.syncChunkSendTimer.stopTiming();
            return;
        }

        if (this.chunkSendTasks.containsKey(index)) {
            for (Player player : this.chunkSendQueue.get(index).values()) {
                if (player.isConnected() && player.usedChunks.containsKey(index)) {
                    player.sendChunk(x, z, payload, ordering);
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
        this.clearChunkCache((int) blockEntity.getX() >> 4, (int) blockEntity.getZ() >> 4);
    }

    public void removeBlockEntity(BlockEntity blockEntity) {
        if (blockEntity.getLevel() != this) {
            throw new LevelException("Invalid Block Entity level");
        }
        blockEntities.remove(blockEntity.getId());
        updateBlockEntities.remove(blockEntity.getId());
        this.clearChunkCache((int) blockEntity.getX() >> 4, (int) blockEntity.getZ() >> 4);
    }

    public boolean isChunkInUse(int x, int z) {
        String index = Level.chunkHash(x, z);
        return this.chunkLoaders.containsKey(index) && !this.chunkLoaders.get(index).isEmpty();
    }

    public boolean loadChunk(int x, int z) {
        return this.loadChunk(x, z, true);
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        String index = Level.chunkHash(x, z);
        if (this.chunks.containsKey(index)) {
            return true;
        }

        this.timings.syncChunkLoadTimer.startTiming();

        this.cancelUnloadChunkRequest(x, z);

        BaseFullChunk chunk = this.provider.getChunk(x, z, generate);

        if (chunk == null) {
            if (generate) {
                throw new IllegalStateException("Could not create new Chunk");
            }
            return false;
        }

        this.chunks.put(index, chunk);
        chunk.initChunk();

        if (chunk.getProvider() != null) {
            this.server.getPluginManager().callEvent(new ChunkLoadEvent(chunk, !chunk.isGenerated()));
        } else {
            this.unloadChunk(x, z, false);
            this.timings.syncChunkLoadTimer.stopTiming();
            return false;
        }

        if (!chunk.isLightPopulated() && chunk.isPopulated()
                && (boolean) this.getServer().getConfig("chunk-ticking.light-updates", false)) {
            this.getServer().getScheduler().scheduleAsyncTask(new LightPopulationTask(this, chunk));
        }

        if (this.isChunkInUse(x, z)) {
            for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                loader.onChunkLoaded(chunk);
            }
        } else {
            this.unloadChunkRequest(x, z);
        }
        this.timings.syncChunkLoadTimer.stopTiming();
        return true;
    }

    private void queueUnloadChunk(int x, int z) {
        String index = Level.chunkHash(x, z);
        this.unloadQueue.put(index, System.currentTimeMillis());
        this.chunkTickList.remove(index);
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
        this.unloadQueue.remove(Level.chunkHash(x, z));
    }

    public boolean unloadChunk(int x, int z) {
        return this.unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean safe) {
        return this.unloadChunk(x, z, safe, true);
    }

    public boolean unloadChunk(int x, int z, boolean safe, boolean trySave) {
        if (safe && this.isChunkInUse(x, z)) {
            return false;
        }

        if (!this.isChunkLoaded(x, z)) {
            return true;
        }

        this.timings.doChunkUnload.startTiming();

        String index = Level.chunkHash(x, z);

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
                        this.provider.setChunk(x, z, chunk);
                        this.provider.saveChunk(x, z);
                    }
                }
                for (ChunkLoader loader : this.getChunkLoaders(x, z)) {
                    loader.onChunkUnloaded(chunk);
                }
            }
            this.provider.unloadChunk(x, z, safe);
        } catch (Exception e) {
            MainLogger logger = this.server.getLogger();
            logger.error(this.server.getLanguage().translateString("nukkit.level.chunkUnloadError", e.toString()));
            logger.logException(e);
        }

        this.chunks.remove(index);
        this.chunkTickList.remove(index);
        this.chunkCache.remove(index);

        this.timings.doChunkUnload.stopTiming();

        return true;
    }

    public boolean isSpawnChunk(int X, int Z) {
        int spawnX = (int) this.provider.getSpawn().getX() >> 4;
        int spawnZ = (int) this.provider.getSpawn().getZ() >> 4;

        return Math.abs(X - spawnX) <= 1 && Math.abs(Z - spawnZ) <= 1;
    }

    public Position getSafeSpawn() {
        return this.getSafeSpawn(null);
    }

    public Position getSafeSpawn(Vector3 spawn) {
        if (spawn == null || spawn.y < 1) {
            spawn = this.getSpawnLocation();
        }

        if (spawn != null) {
            Vector3 v = spawn.floor();
            FullChunk chunk = this.getChunk((int) v.x >> 4, (int) v.z >> 4, false);
            int x = (int) v.x & 0x0f;
            int z = (int) v.z & 0x0f;
            if (chunk != null) {
                int y = (int) Math.min(126, v.y);
                boolean wasAir = chunk.getBlockId(x, y - 1, z) == 0;
                for (; y > 0; --y) {
                    int b = chunk.getFullBlock(x, y, z);
                    Block block = Block.get(b >> 4, b & 0x0f);
                    if (this.isFullBlock(block)) {
                        if (wasAir) {
                            y++;
                            break;
                        }
                    } else {
                        wasAir = true;
                    }
                }

                for (; y >= 0 && y < 128; ++y) {
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
        return (int) time;
    }

    public String getName() {
        return this.provider.getName();
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
        String index = Level.chunkHash(x, z);
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
                    this.chunkPopulationQueue.put(index, true);
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int zz = -1; zz <= 1; ++zz) {
                            this.chunkPopulationLock.put(Level.chunkHash(x + xx, z + zz), true);
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

        String index = Level.chunkHash(x, z);
        if (!this.chunkGenerationQueue.containsKey(index)) {
            Timings.generationTimer.startTiming();
            this.chunkGenerationQueue.put(index, true);
            GenerationTask task = new GenerationTask(this, this.getChunk(x, z, true));
            this.server.getScheduler().scheduleAsyncTask(task);
            Timings.generationTimer.stopTiming();
        }
    }

    public void regenerateChunk(int x, int z) {
        this.unloadChunk(x, z, false);

        this.cancelUnloadChunkRequest(x, z);

        this.generateChunk(x, z);
    }

    public void doChunkGarbageCollection() {
        this.timings.doChunkGC.startTiming();
        // remove all invaild block entities.
        List<BlockEntity> toClose = new ArrayList<>();
        for (BlockEntity anBlockEntity : blockEntities.values()) {
            if (anBlockEntity == null)
                continue;
            if (anBlockEntity.isBlockEntityValid())
                continue;
            toClose.add(anBlockEntity);
        }
        for (BlockEntity be : toClose.toArray(new BlockEntity[toClose.size()])) {
            be.close();
        }

        for (String index : this.chunks.keySet()) {

            if (!this.unloadQueue.containsKey(index)) {
                Chunk.Entry chunkEntry = Level.getChunkXZ(index);
                int X = chunkEntry.chunkX;
                int Z = chunkEntry.chunkZ;
                if (!this.isSpawnChunk(X, Z)) {
                    this.unloadChunkRequest(X, Z, true);
                }
            }
        }

        for (FullChunk chunk : new ArrayList<>(this.provider.getLoadedChunks().values())) {
            if (!this.chunks.containsKey(Level.chunkHash(chunk.getX(), chunk.getZ()))) {
                this.provider.unloadChunk(chunk.getX(), chunk.getZ(), false);
            }
        }

        this.provider.doGarbageCollection();
        this.timings.doChunkGC.stopTiming();
    }

    public void unloadChunks() {
        this.unloadChunks(false);
    }

    public void unloadChunks(boolean force) {
        if (!this.unloadQueue.isEmpty()) {
            int maxUnload = 96;
            long now = System.currentTimeMillis();

            for (String index : new ArrayList<>(this.unloadQueue.keySet())) {
                long time = this.unloadQueue.get(index);

                Chunk.Entry chunkEntry = Level.getChunkXZ(index);
                int X = chunkEntry.chunkX;
                int Z = chunkEntry.chunkZ;

                if (!force) {
                    if (maxUnload <= 0) {
                        break;
                    } else if (time > (now - 30000)) {
                        continue;
                    }
                }

                if (this.unloadChunk(X, Z, true)) {
                    this.unloadQueue.remove(index);
                    --maxUnload;
                }
            }
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

    public void addEntityMotion(int chunkX, int chunkZ, long entityId, double x, double y, double z) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (!this.motionToSend.containsKey(index)) {
            this.motionToSend.put(index, new HashMap<>());
        }
        this.motionToSend.get(index).put(entityId, new SetEntityMotionPacket.Entry(entityId, x, y, z));
    }

    public void addEntityMovement(int chunkX, int chunkZ, long entityId, double x, double y, double z, double yaw,
                                  double pitch, double headYaw) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (!this.moveToSend.containsKey(index)) {
            this.moveToSend.put(index, new HashMap<>());
        }

        MoveEntityPacket pk = new MoveEntityPacket();
        pk.eid = entityId;
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        this.moveToSend.get(index).put(entityId, pk);
    }

    public void addPlayerMovement(int chunkX, int chunkZ, long entityId, double x, double y, double z, double yaw,
                                  double pitch, boolean onGround) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (!this.playerMoveToSend.containsKey(index)) {
            this.playerMoveToSend.put(index, new HashMap<>());
        }

        MovePlayerPacket pk = new MovePlayerPacket();
        pk.eid = entityId;
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) yaw;
        pk.pitch = (float) pitch;
        pk.onGround = onGround;
        this.playerMoveToSend.get(index).put(entityId, pk);
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
            pk.data = rand.nextInt(50000) + 10000;
            setRainTime(rand.nextInt(12000) + 12000);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
            setRainTime(rand.nextInt(168000) + 12000);
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
            pk.data = rand.nextInt(50000) + 10000;
            setThunderTime(rand.nextInt(12000) + 3600);
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_THUNDER;
            setThunderTime(rand.nextInt(168000) + 12000);
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
            players = this.getPlayers().values().stream().toArray(Player[]::new);
        }

        LevelEventPacket pk = new LevelEventPacket();

        if (this.isRaining()) {
            pk.evid = LevelEventPacket.EVENT_START_RAIN;
            pk.data = rand.nextInt(50000) + 10000;
        } else {
            pk.evid = LevelEventPacket.EVENT_STOP_RAIN;
        }

        Server.broadcastPacket(players, pk);

        if (this.isThundering()) {
            pk.evid = LevelEventPacket.EVENT_START_THUNDER;
            pk.data = rand.nextInt(50000) + 10000;
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
        this.sendWeather(players.stream().toArray(Player[]::new));
    }

    public int getDimension() {
        return dimension;
    }

    public boolean canBlockSeeSky(Vector3 pos) {
        return this.getHighestBlockAt(pos.getFloorX(), pos.getFloorZ()) < pos.getY();
    }

}
