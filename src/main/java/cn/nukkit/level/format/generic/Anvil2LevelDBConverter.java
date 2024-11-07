package cn.nukkit.level.format.generic;

import cn.nukkit.Server;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.RegionLoader;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.TaskHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Anvil2LevelDBConverter {

    private final Level sourceLevel;
    private final Level targetLevel;
    private final ExecutorService executor;

    public Anvil2LevelDBConverter(Level sourceLevel) {
        this.sourceLevel = sourceLevel;

        Server server = sourceLevel.getServer();
        String levelName = sourceLevel.getFolderName() + "-convert";

        if (!server.generateLevel(levelName, 0, Generator.getGenerator("void"))) {
            throw new IllegalStateException("Failed to generate target level, make sure it doesn't exist: " + levelName);
        }

        this.targetLevel = server.getLevelByName(levelName);
        if (this.targetLevel == null) {
            throw new IllegalStateException("Failed to load target level: " + levelName);
        }

        this.sourceLevel.setAutoSave(false);
        this.sourceLevel.isBeingConverted = true;

        this.targetLevel.setAutoSave(false);
        this.targetLevel.isBeingConverted = true;

        // DO NOT INCREASE THREAD COUNT
        this.executor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder()
                .setNameFormat("Converted Thread " + sourceLevel.getFolderName() + " - %s")
                .build());
    }

    public CompletableFuture<Void> convert() {
        try {
            return this.convertUnsafe();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert level " + this.sourceLevel.getFolderName(), e);
        } finally {
            this.executor.shutdown();
        }
    }

    private CompletableFuture<Void> convertUnsafe() throws IOException {
        Anvil anvil = (Anvil) this.sourceLevel.getProvider();
        LevelDBProvider levelDBProvider = (LevelDBProvider) this.targetLevel.getProvider();

        Server server = this.sourceLevel.getServer();
        server.getLogger().info("Converting level " + this.targetLevel.getFolderName());

        // Clone level data
        CompoundTag levelData = anvil.getLevelData().clone();
        levelData.putString("generatorName", this.sourceLevel.getGenerator().getName());
        levelData.remove("GameRules");
        levelDBProvider.setLevelData(levelData, this.sourceLevel.getGameRules());
        levelDBProvider.saveLevelData();

        boolean nether = this.sourceLevel.getDimension() == Level.DIMENSION_NETHER;
        DimensionData dimensionData = DimensionEnum.getDataFromId(this.sourceLevel.getDimension());
        if (dimensionData == null) {
            server.getLogger().warning("Invalid DimensionData, using OVERWORLD");
            dimensionData = DimensionEnum.OVERWORLD.getDimensionData();
        }
        this.targetLevel.setDimensionData(dimensionData);

        List<Path> regions = new ObjectArrayList<>();
        Path regionFolder = Paths.get("worlds/" + this.sourceLevel.getFolderName() + "/region");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(regionFolder, "**.mca")) {
            for (Path path : stream) {
                regions.add(path);
            }
        }

        AtomicInteger regionCounter = new AtomicInteger();
        AtomicInteger chunksConverted = new AtomicInteger();
        AtomicInteger chunksConvertedPerSecond = new AtomicInteger();

        TaskHandler tickFuture = server.getScheduler().scheduleRepeatingTask(null, () ->
                chunksConvertedPerSecond.set(0), 20);

        IntConsumer callback = chunksCount -> {
            chunksConverted.addAndGet(chunksCount);
            int regionNumber = regionCounter.incrementAndGet();

            int chps = chunksConvertedPerSecond.addAndGet(chunksCount);
            String message = "[Convert-%s] [%s/%s] [%s chps] Converted %s chunks";
            server.getLogger().info(String.format(message, this.sourceLevel.getFolderName(), regionNumber, regions.size(), chps, chunksCount));
        };

        List<CompletableFuture<Void>> futures = new ObjectArrayList<>();
        for (Path regionPath : regions) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            this.executor.execute(() -> {
                convertRegion(regionPath, anvil, levelDBProvider, callback, nether ? 128 : 256);
                future.complete(null);
            });
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenCompleteAsync((v, error) -> {
            server.getScheduler().cancelTask(tickFuture.getTaskId());
            if (error != null) {
                server.getLogger().error("Failed to convert level " + this.sourceLevel.getFolderName(), error);
            } else {
                this.convertFinished();
            }
        }, task -> server.getScheduler().scheduleTask(null, task));
    }

    private void convertFinished() {
        Server server = this.sourceLevel.getServer();

        server.unloadLevel(this.targetLevel);

        if (sourceLevel.equals(server.getDefaultLevel())) {
            server.getLogger().warning("Couldn't unload original level due to it being loaded as the default level. Please restart the server.");
        } else {
            server.unloadLevel(this.sourceLevel);
        }

        server.getLogger().info("[Convert-" + this.sourceLevel.getFolderName() + "] All done! Converted level is saved under worlds/" + this.targetLevel.getFolderName() + "/");
    }

    private static void convertRegion(Path regionFile, Anvil anvil, LevelDBProvider levelDBProvider, IntConsumer callback, int maxY) {
        RegionPosition regionPos = RegionPosition.fromPath(regionFile);
        assert regionPos != null;

        try {
            BaseRegionLoader regionLoader = new RegionLoader(anvil, regionPos.x, regionPos.z);
            int chunks = 0;
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    int chunkX = regionPos.x << 5 | x;
                    int chunkZ = regionPos.z << 5 | z;

                    BaseFullChunk oldChunk = regionLoader.readChunk(chunkX - (getRegionIndexX(chunkX) << 5), chunkZ - (getRegionIndexZ(chunkZ) << 5)); // anvil.getChunk(chunkX, chunkZ, false);
                    if (oldChunk == null || (!oldChunk.isPopulated() && !oldChunk.isGenerated())) {
                        continue;
                    }

                    chunks++;
                    BaseFullChunk newChunk = levelDBProvider.getChunk(chunkX, chunkZ, true);
                    convertChunk(oldChunk, (LevelDBChunk) newChunk, levelDBProvider, anvil, maxY);
                }
            }

            callback.accept(chunks);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert region " + regionPos, e);
        }
    }

    private static void convertChunk(BaseFullChunk oldChunk, LevelDBChunk newChunk, LevelDBProvider levelDBProvider, Anvil anvil, int maxY) {
        oldChunk.initChunk();
        newChunk.setGenerated(true);
        newChunk.setPopulated(oldChunk.isPopulated());

        newChunk.setBiomeIdArray(oldChunk.getBiomeIdArray());

        newChunk.heightMap = Arrays.copyOf(oldChunk.getHeightMapArray(), oldChunk.getHeightMapArray().length);
        newChunk.tiles = oldChunk.getBlockEntities();
        newChunk.entities = oldChunk.getEntities();
        newChunk.tileList = oldChunk.tileList;

        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockY = 0; blockY < maxY; blockY++) {
                for (int blockZ = 0; blockZ < 16; blockZ++) {
                    int fullId = oldChunk.getFullBlock(blockX, blockY, blockZ);
                    newChunk.setFullBlockId(blockX, blockY, blockZ, fullId);
                    newChunk.setBlockLight(blockX, blockY, blockZ, oldChunk.getBlockSkyLight(blockX, blockY, blockZ));
                }
            }
        }

        levelDBProvider.saveChunkSync(newChunk.getX(), newChunk.getZ(), newChunk);
        levelDBProvider.unloadChunk(newChunk.getX(), newChunk.getZ(), false);
        anvil.unloadChunk(oldChunk.getX(), oldChunk.getZ(), false);
    }

    protected static int getRegionIndexX(int chunkX) {
        return chunkX >> 5;
    }

    protected static int getRegionIndexZ(int chunkZ) {
        return chunkZ >> 5;
    }

    @ToString
    @RequiredArgsConstructor
    private static class RegionPosition {
        private static final Pattern PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");

        private final int x;
        private final int z;

        static RegionPosition fromPath(Path regionPath) {
            Matcher matcher = PATTERN.matcher(regionPath.getFileName().toString());
            if (!matcher.matches()) {
                return null;
            }
            int x = Integer.parseInt(matcher.group(1));
            int z = Integer.parseInt(matcher.group(2));
            return new RegionPosition(x, z);
        }
    }
}
