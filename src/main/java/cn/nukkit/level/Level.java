package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.Ice;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.level.LevelUnloadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.GeneratorRegisterTask;
import cn.nukkit.level.generator.GeneratorUnregisterTask;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.BlockMetadataStore;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.tile.Tile;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.PriorityObject;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
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

    private Map<Integer, Tile> tiles = new HashMap<>();

    private Map<String, Map<Integer, double[][]>> motionToSend = new HashMap<>();
    private Map<String, Map<Integer, double[][]>> moveToSend = new HashMap<>();

    private Map<Integer, Player> players = new HashMap<>();

    private Map<Integer, Entity> entities = new HashMap<>();

    public Map<Integer, Entity> updateEntities = new HashMap<>();

    public Map<Integer, Tile> updateTiles = new HashMap<>();

    private Map<String, Block> blockCache = new HashMap<>();

    private Map<String, DataPacket> chunkCache = new HashMap<>();

    private boolean cacheChunks = false;

    private int sendTimeTicker = 0;

    private Server server;

    private int levelId;

    private LevelProvider provider;

    private Map<Integer, ChunkLoader> loaders = new HashMap<>();

    private Map<Integer, Integer> loaderCounter = new HashMap<>();

    private Map<String, Map<Integer, ChunkLoader>> chunkLoaders = new HashMap<>();

    private Map<String, Map<Integer, Player>> playerLoaders = new HashMap<>();

    private Map<String, List<DataPacket>> chunkPackets = new HashMap<>();

    private Map<String, Long> unloadQueue;

    private float time;
    public boolean stopTime;

    private String folderName;

    private Map<String, FullChunk> chunks = new HashMap<>();

    private Map<String, Map<String, Vector3>> changedBlocks = new HashMap<>();

    private PriorityQueue<PriorityObject> updateQueue;
    private Map<String, Integer> updateQueueIndex = new HashMap<>();

    private Map<String, Map<String, Player>> chunkSendQueue = new HashMap<>();
    private Map<String, Boolean> chunkSendTasks = new HashMap<>();

    private Map<String, Boolean> chunkPopulationQueue = new HashMap<>();
    private Map<String, Boolean> chunkPopulationLock = new HashMap<>();
    private Map<String, Boolean> chunkGenerationQueue = new HashMap<>();
    private int chunkGenerationQueueSize = 8;
    private int chunkPopulationQueueSize = 2;

    private boolean autoSave = true;

    private BlockMetadataStore blockMetadata;

    private boolean useSections;
    private byte blockOrder;

    private Position temporalPosition;
    private Vector3 temporalVector;

    private Block[] blockStates;

    public int sleepTicks = 0;

    private int chunkTickRadius;
    private Map<String, Integer> chunkTickList = new HashMap<>();
    private int chunksPerTicks;
    private boolean clearChunksOnTick;
    private HashMap<Integer, Class<? extends Block>> randomTickBlocks = new HashMap<Integer, Class<? extends Block>>() {{
        //todo alot blocks
        put(Block.ICE, Ice.class);
    }};

    private int tickRate;
    public int tickRateTime = 0;
    public int tickRateCounter = 0;

    private Class<? extends Generator> generator;
    private Generator generatorInstance;

    public Level(Server server, String name, String path, Class<? extends LevelProvider> provider) {
        this.blockStates = Block.fullList;
        this.levelId = levelIdCounter++;
        this.blockMetadata = new BlockMetadataStore(this);
        this.server = server;
        this.autoSave = server.getAutoSave();

        try {
            this.provider = provider.getConstructor(Level.class, String.class).newInstance(this, path);
        } catch (Exception e) {
            throw new LevelException("Wrong constructor in class " + provider.getName());
        }

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.preparing", this.provider.getName()));
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
                return o1.priority < o2.priority ? 1 : (o1.priority == o2.priority ? 0 : -1);
            }
        });
        this.time = this.provider.getTime();

        this.chunkTickRadius = Math.min(this.server.getViewDistance(), Math.max(1, (Integer) this.server.getConfig("chunk-ticking.tick-radius", 4)));
        this.chunksPerTicks = (int) this.server.getConfig("chunk-ticking.per-tick", 40);
        this.chunkGenerationQueueSize = (int) this.server.getConfig("chunk-generation.queue-size", 8);
        this.chunkPopulationQueueSize = (int) this.server.getConfig("chunk-generation.population-queue-size", 2);
        this.chunkTickList.clear();
        this.clearChunksOnTick = (boolean) this.server.getConfig("chunk-ticking.clear-tick-list", true);
        this.cacheChunks = (boolean) this.server.getConfig("chunk-sending.cache-chunks", false);

        this.temporalPosition = new Position(0, 0, 0, this);
        this.temporalVector = new Vector3(0, 0, 0);
        this.tickRate = 1;
    }

    public static String chunkHash(int x, int z) {
        return x + ":" + z;
    }

    public static String blockHash(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    public static Vector3 getBlockVector3(String hash) {
        String[] h = hash.split(":");
        return new Vector3(Integer.valueOf(h[0]), Integer.valueOf(h[1]), Integer.valueOf(h[2]);
    }

    public static Vector2 getBlockVector2(String hash) {
        String[] h = hash.split(":");
        return new Vector2(Integer.valueOf(h[0]), Integer.valueOf(h[1]));
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
        try {
            this.generatorInstance = generator.getConstructor(Map.class).newInstance(this.provider.getGeneratorOptions());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.generatorInstance.init(this, new Random(this.getSeed()));

        this.registerGenerator();
    }

    public void registerGenerator() {
        int size = this.server.getScheduler().getAsyncTaskPoolSize();
        for (int i = 0; i < size; ++i) {
            this.server.getScheduler().scheduleAsyncTaskToWorker(new GeneratorRegisterTask(this, this.generatorInstance), i);
        }
    }

    public void unregisterGenerator() {
        int size = this.server.getScheduler().getAsyncTaskPoolSize();
        for (int i = 0; i < size; ++i) {
            this.server.getScheduler().scheduleAsyncTaskToWorker(new GeneratorUnregisterTask(this), i);
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

        for (FullChunk chunk : this.chunks.values()) {
            this.unloadChunk(chunk.getX(), chunk.getZ(), false);
        }

        this.unregisterGenerator();

        this.provider.close();
        this.provider = null;
        this.blockMetadata = null;
        this.blockCache.clear();
        this.temporalPosition = null;
    }

    public void addSound() {
        //todo
    }

    public void addParticle() {
        //todo
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

        if (this.equals(this.server.getDefaultLevel()) && !force) {
            ev.setCancelled();
        }

        this.server.getPluginManager().callEvent(ev);

        if (!force && ev.isCancelled()) {
            return false;
        }

        this.server.getLogger().info(this.server.getLanguage().translateString("nukkit.level.unloading", this.getName()));
        Level defaultLevel = this.server.getDefaultLevel();

        for (Player player : this.getPlayers().values()) {
            if (this.equals(defaultLevel) || defaultLevel == null) {
                player.close(player.getLeaveMessage(), "Forced default level unload");
            } else if (defaultLevel != null) {
                player.teleport(this.server.getDefaultLevel().getSafeSpawn());
            }
        }

        if (this.equals(defaultLevel)) {
            this.server.setDefaultLevel(null);
        }

        this.close();

        return true;
    }

    public Player[] getChunkPlayers(int chunkX, int chunkZ) {
        String index = Level.chunkHash(chunkX, chunkZ);
        if (this.playerLoaders.containsKey(index)) {
            return this.playerLoaders.get(index).values().stream().toArray(Player[]::new);
        } else {
            return new Player[0];
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
            this.time += 1.25;
        }
    }

    public void sendTime() {
        SetTimePacket pk = new SetTimePacket();
        pk.time = (int) this.time;
        pk.started = !this.stopTime;

        Server.broadcastPacket(this.players, pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
    }

    public void doTick(long currentTick) {
        this.checkTime();

        if (++this.sendTimeTicker == 200) {
            this.sendTime();
            this.sendTimeTicker = 0;
        }

        this.unloadChunks();

        while (this.updateQueue.peek() != null && this.updateQueue.peek().priority <= currentTick) {
            Block block = this.getBlock((Vector3) this.updateQueue.poll().data);
            this.updateQueueIndex.remove(Level.blockHash((int) block.x, (int) block.y, (int) block.z));
            block.onUpdate(BLOCK_UPDATE_SCHEDULED);
        }

        for (Map.Entry<Integer, Entity> entry : this.updateEntities.entrySet()) {
            int id = entry.getKey();
            Entity entity = entry.getValue();
            if (entity.closed || !entity.onUpdate(currentTick)) {
                this.updateEntities.remove(id);
            }
        }

        if (!this.updateTiles.isEmpty()) {
            for (Map.Entry<Integer, Tile> entry : this.updateTiles.entrySet()) {
                if (!entry.getValue().onUpdate()) {
                    this.updateTiles.remove(entry.getKey());
                }
            }
        }

        this.tickChunks();

        if (!this.changedBlocks.isEmpty()) {
            if (!this.players.isEmpty()) {
                for (Map.Entry<String, Map<String, Vector3>> entry : this.changedBlocks.entrySet()) {
                    String index = entry.getKey();
                    Map<String, Vector3> blocks = entry.getValue();
                    Vector2 chunkVector = Level.getBlockVector2(index);
                    int chunkX = (int) chunkVector.getX();
                    int chunkZ = (int) chunkVector.getY();
                    if (blocks.size() > 512) {
                        BaseFullChunk chunk = this.getChunk(chunkX, chunkZ);
                        for (Player p : this.getChunkPlayers(chunkX, chunkZ)) {
                            p.onChunkChanged(chunk);
                        }
                    } else {
                        this.sendBlocks(this.getChunkPlayers(chunkX, chunkZ), blocks, UpdateBlockPacket.FLAG_ALL);
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

        for (Map.Entry<String, Map<Integer, double[][]>> entry : this.moveToSend.entrySet()) {
            Vector2 v = Level.getBlockVector2(entry.getKey());
            int chunkX = (int) v.getX();
            int chunkZ = (int) v.getY();
            MoveEntityPacket pk = new MoveEntityPacket();
            pk.entities = entry.getValue().values().stream().toArray(double[][]::new);
            this.addChunkPacket(chunkX, chunkZ, pk.setChannel(Network.CHANNEL_MOVEMENT));
        }
        this.moveToSend = new HashMap<>();

        for (Map.Entry<String, Map<Integer, double[][]>> entry : this.motionToSend.entrySet()) {
            Vector2 v = Level.getBlockVector2(entry.getKey());
            int chunkX = (int) v.getX();
            int chunkZ = (int) v.getY();
            SetEntityMotionPacket pk = new SetEntityMotionPacket();
            pk.entities = entry.getValue().values().stream().toArray(double[][]::new);
            this.addChunkPacket(chunkX, chunkZ, pk.setChannel(Network.CHANNEL_MOVEMENT));
        }
        this.motionToSend = new HashMap<>();

        for (Map.Entry<String, List<DataPacket>> entry : this.chunkPackets.entrySet()) {
            Vector2 v = Level.getBlockVector2(entry.getKey());
            int chunkX = (int) v.getX();
            int chunkZ = (int) v.getY();
            Player[] chunkPlayers = this.getChunkPlayers(chunkX, chunkZ);
            if (chunkPlayers.length > 0) {
                for (DataPacket pk : entry.getValue()) {
                    Server.broadcastPacket(chunkPlayers, pk);
                }
            }
        }

        this.chunkPackets = new HashMap<>();
    }

    public void checkSleep() {
        if (this.players.isEmpty()) {
            return;
        }

        boolean resetTime = true;
        for (Player p : this.getPlayers()) {
            if (!p.isSleeping()) {
                resetTime = false;
                break;
            }
        }

        if (resetTime) {
            int time = this.getTime() % Level.TIME_FULL;

            if (time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE) {
                this.setTime(this.getTime() + Level.TIME_FULL - time);

                for (Player p : this.getPlayers()) {
                    p.stopSleep();
                }
            }
        }
    }

    public void sendBlocks(Player[] target, Block[] blocks) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE);
    }

    public void sendBlocks(Player[] target, Block[] blocks, int flags) {
        this.sendBlocks(target, blocks, UpdateBlockPacket.FLAG_NONE, false);
    }

    public void sendBlocks(Player[] target, Block[] blocks, int flags, boolean optimizeRebuilds) {
        UpdateBlockPacket pk = new UpdateBlockPacket();

        if (optimizeRebuilds) {
            Map<String, Boolean> chunks = new HashMap<>();
            for (Block b : blocks) {
                if (b == null) {
                    continue;
                }

                boolean first = false;

                String index = Level.chunkHash((int) b.x >> 4, (int) b.z >> 4);
                if (!chunks.containsKey(index)) {
                    chunks.put(index, true);
                    first = true;
                }

                List<int[]> list = new ArrayList<>();
                Collections.addAll(list, pk.records);
                list.add(new int[]{(int) b.x, (int) b.z, (int) b.y, b.getId(), b.getDamage(), first ? flags : UpdateBlockPacket.FLAG_NONE});
                pk.records = list.stream().toArray(int[][]::new);
            }
        } else {
            for (Block b : blocks) {
                if (b == null) {
                    continue;
                }

                List<int[]> list = new ArrayList<>();
                Collections.addAll(list, pk.records);
                list.add(new int[]{(int) b.x, (int) b.z, (int) b.y, b.getId(), b.getDamage(), flags});
                pk.records = list.stream().toArray(int[][]::new);
            }
        }

        Server.broadcastPacket(target, pk.setChannel(Network.CHANNEL_BLOCKS));
    }

    public void clearCache() {
        this.clearCache(false);
    }

    public void clearCache(boolean full) {
        if (full) {
            this.chunkCache = new HashMap<>();
            this.blockCache = new HashMap<>();
        } else {
            if (this.chunkCache.size() > 768) {
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


    public Block getBlock(Vector3 pos) {
        return this.getBlock(pos, true);
    }

    public Block getBlock(Vector3 pos, boolean cached) {
        //todo !!!!
        return null;
    }

    public boolean setBlock(Vector3 pos, Block block) {
        return this.setBlock(pos, block, false, true);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct) {
        return this.setBlock(pos, block, direct, true);
    }

    public boolean setBlock(Vector3 pos, Block block, boolean direct, boolean update) {
        //todo!!
        return false;
    }

    public Item useBreakOn(Vector3 vector) {
        return this.useBreakOn(vector, null, false);
    }

    public Item useBreakOn(Vector3 vector, Player player) {
        return this.useBreakOn(vector, player, false);
    }

    public Item useBreakOn(Vector3 vector, Player player, boolean createParticles) {
        //todo
        return null;
    }

    public void chunkRequestCallback(int x, int z, byte[] payload) {
        //todo
    }

    public void addTile(Tile tile) throws LevelException {
        if (tile.getLevel() != this) {
            throw new LevelException("Invalid Tile level");
        }
        tiles.put(tile.getId(), tile);
        this.clearChunkCache((int) tile.getX() >> 4, (int) tile.getZ() >> 4);
    }

    public void removeTile(Tile tile) throws LevelException {
        if (tile.getLevel() != this) {
            throw new LevelException("Invalid Tile level");
        }
        tiles.remove(tile.getId());
        updateTiles.remove(tile.getId());
        this.clearChunkCache((int) tile.getX() >> 4, (int) tile.getZ() >> 4);
    }


    public String getName() {
        return this.provider.getName();
    }

    public String getFolderName() {
        return this.folderName;
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


}
