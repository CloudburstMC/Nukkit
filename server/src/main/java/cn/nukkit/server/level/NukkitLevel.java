package cn.nukkit.server.level;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.misc.DroppedItem;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.LevelData;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.chunk.generator.ChunkGenerator;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.misc.DroppedItemEntity;
import cn.nukkit.server.entity.system.*;
import cn.nukkit.server.level.manager.*;
import cn.nukkit.server.level.provider.ChunkProvider;
import cn.nukkit.server.metadata.MetadataSerializers;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.network.minecraft.packet.BlockEntityDataPacket;
import cn.nukkit.server.network.minecraft.packet.UpdateBlockPacket;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class NukkitLevel implements Level {
    @Getter
    private static final LevelPaletteManager paletteManager = new LevelPaletteManager();
    private static final int FULL_TIME = 24000;
    private final LevelData levelData;
    private final String levelId;
    @Getter
    private final LevelEntityManager entityManager;
    @Getter
    private final LevelPacketManager packetManager;
    @Getter
    private final LevelBlockManager blockManager;
    @Getter
    private final LevelChunkManager chunkManager;
    @Getter
    private final NukkitServer server;
    private long currentTick;

    public NukkitLevel(NukkitServer server, String levelId, ChunkProvider chunkProvider, LevelData levelData, ChunkGenerator generator) {
        this.server = server;
        this.levelData = levelData;
        this.currentTick = levelData.getSavedTick();
        this.levelId = levelId;
        this.entityManager = new LevelEntityManager(this);
        this.packetManager = new LevelPacketManager(this, server.getConfiguration().getMechanics().getViewDistance());
        this.blockManager = new LevelBlockManager(this);
        this.chunkManager = new LevelChunkManager(server, this, chunkProvider, generator);

        if (levelData.getDefaultSpawn() == null) {
            levelData.setDefaultSpawn(generator.getDefaultSpawn());
        }

        registerSystem(PlayerSession.SYSTEM);
        registerSystem(FlammableDecrementSystem.SYSTEM);
        registerSystem(PhysicsSystem.SYSTEM);
        registerSystem(PickupDelayDecrementSystem.SYSTEM);
        registerSystem(PortalCooldownDecrementSystem.SYSTEM);
        registerSystem(MergeItemsSystem.SYSTEM);
        registerSystem(ItemPickupSystem.SYSTEM);
    }

    @Override
    public String getId() {
        return levelId;
    }

    @Override
    public long getCurrentTick() {
        return currentTick;
    }

    @Override
    public LevelData getData() {
        return levelData;
    }

    @Override
    public String getName() {
        return levelData.getName();
    }

    @Override
    public int getTime() {
        return (int) ((currentTick + levelData.getSavedTime()) % FULL_TIME);
    }

    @Override
    public Optional<Chunk> getChunkIfLoaded(int x, int z) {
        return chunkManager.getChunkIfLoaded(x, z);
    }

    @Override
    public CompletableFuture<Chunk> getChunk(int x, int z) {
        return chunkManager.getChunk(x, z);
    }

    @Override
    public void save() {
        // TODO
    }

    @Override
    public void registerSystem(System system) {
        entityManager.registerSystem(system);
    }

    @Override
    public void deregisterSystem(System system) {
        entityManager.deregisterSystem(system);
    }

    public void onTick() {
        currentTick++;

        entityManager.onTick();
        packetManager.onTick();
        blockManager.onTick();

        if (currentTick % 20 == 0) {
            chunkManager.onTick();
        }
    }

    public <T extends Entity> CompletableFuture<T> spawn(@Nonnull Class<? extends Entity> clazz, @Nonnull Vector3f position) {
        CompletableFuture<T> future = new CompletableFuture<>();

        getChunk(position.getFloorX() >> 4, position.getFloorZ() >> 4).whenComplete((chunk, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
                return;
            }

            future.complete(server.getEntitySpawner().spawnEntity(clazz, position, this));
        });

        return future;
    }

    @Override
    public CompletableFuture<DroppedItem> dropItem(@Nonnull ItemInstance item, @Nonnull Vector3f position) {
        CompletableFuture<DroppedItem> future = new CompletableFuture<>();

        getChunk(position.getFloorX() >> 4, position.getFloorZ() >> 4).whenComplete((chunk, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
                return;
            }

            future.complete(new DroppedItemEntity(position, this, server, item));
        });

        return future;
    }

    public void broadcastBlockUpdate(Entity entity, Vector3i position) {
        Optional<Block> block = getBlockIfChunkLoaded(position);
        if (!block.isPresent()) {
            log.warn("Cannot update block at {} as chunk is not loaded", position);
            return;
        }

        BlockState state = block.get().getBlockState();
        UpdateBlockPacket packet = new UpdateBlockPacket();
        packet.setRuntimeId(paletteManager.getOrCreateRuntimeId(state));
        packet.setBlockPosition(position);
        packet.setDataLayer(UpdateBlockPacket.DataLayer.NORMAL);
        packetManager.queuePacketForViewers(entity, packet);

        if (state.getBlockEntity().isPresent()) {
            CompoundTag tag = MetadataSerializers.serializeNBT(state);
            if (tag == null) {
                throw new IllegalStateException("Serialized BlockEntity tag was null");
            }
            tag.tagInt("x", position.getX());
            tag.tagInt("y", position.getY());
            tag.tagInt("z", position.getZ());

            BlockEntityDataPacket packet1 = new BlockEntityDataPacket();
            packet1.setBlockPostion(position);
            packet1.setData(tag);
            packetManager.queuePacketForViewers(entity, packet1);
        }
    }
}
