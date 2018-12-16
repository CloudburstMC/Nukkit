package com.nukkitx.server.level;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.LevelData;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.misc.DroppedItemEntity;
import com.nukkitx.server.entity.system.*;
import com.nukkitx.server.level.manager.*;
import com.nukkitx.server.level.provider.ChunkProvider;
import com.nukkitx.server.metadata.MetadataSerializers;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
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
    private final LevelScoreboardManager scoreboard;
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
        this.scoreboard = new LevelScoreboardManager(this);

        if (levelData.getDefaultSpawn() == null) {
            levelData.setDefaultSpawn(generator.getDefaultSpawn());
        }

        registerSystem(NukkitPlayerSession.SYSTEM);
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
        scoreboard.onTick();

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
    public DroppedItemEntity dropItem(@Nonnull ItemInstance item, @Nonnull Vector3f position) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkNotNull(position, "position");
        Preconditions.checkArgument(getBlockIfChunkLoaded(position.toInt()).isPresent(), "dropped item cannot be spawned in unloaded chunk");
        return new DroppedItemEntity(position, this, server, item);
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
        packet.setDataLayer(0);
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
