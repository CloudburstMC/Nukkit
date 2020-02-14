package cn.nukkit.blockentity.impl;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.level.Level;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author MagicDroidX
 */
@Log4j2
public abstract class BaseBlockEntity implements BlockEntity {

    private static final BiMap<String, Class<? extends BaseBlockEntity>> knownBlockEntities = HashBiMap.create(21);
    public static AtomicLong ID_ALLOCATOR = new AtomicLong(1);
    public final long id;
    private final BlockEntityType<?> type;
    private final Vector3i position;
    private final Chunk chunk;
    private final Level level;
    public boolean movable = true;
    public boolean closed = false;
    protected long lastUpdate;
    protected Server server;
    protected Timing timing;
    private String customName;

    public BaseBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        checkNotNull(type, "type");
        checkNotNull(chunk, "chunk");
        checkNotNull(position, "position");

        this.type = type;
        this.timing = Timings.getBlockEntityTiming(this);
        this.server = chunk.getLevel().getServer();
        this.position = position;
        this.chunk = chunk;
        this.level = chunk.getLevel();
        this.lastUpdate = System.currentTimeMillis();
        this.id = ID_ALLOCATOR.getAndIncrement();

        this.initBlockEntity();

        this.chunk.addBlockEntity(this);
        this.level.addBlockEntity(this);
    }

    @Override
    public BlockEntityType<?> getType() {
        return this.type;
    }

    protected void initBlockEntity() {
        if (isSpawnable()) {
            spawnToAll();
        }
    }

    public long getId() {
        return id;
    }

    public void loadAdditionalData(CompoundTag tag) {
        tag.listenForBoolean("isMovable", this::setMovable);
        tag.listenForString("CustomName", this::setCustomName);
    }

    public void saveAdditionalData(CompoundTagBuilder tag) {
        tag.booleanTag("isMovable", this.movable);
        if (this.customName != null) {
            tag.stringTag("CustomName", this.customName);
        }
    }

    @Override
    public final CompoundTag getFullNBT() {
        CompoundTagBuilder tag = CompoundTag.builder();

        this.saveAdditionalData(tag);

        tag.stringTag("id", BlockEntityRegistry.get().getPersistentId(this.type));
        tag.intTag("x", this.position.getX());
        tag.intTag("y", this.position.getY());
        tag.intTag("z", this.position.getZ());

        return tag.buildRootTag();
    }

    @Override
    public final CompoundTag getShortNBT() {
        CompoundTagBuilder tag = CompoundTag.builder();

        this.saveAdditionalData(tag);

        tag.stringTag("id", BlockEntityRegistry.get().getPersistentId(this.type));

        return tag.buildRootTag();
    }

    @Override
    public final CompoundTag getCleanNBT() {
        CompoundTagBuilder tag = CompoundTag.builder();

        this.saveAdditionalData(tag);

        return tag.buildRootTag();
    }

    @Override
    public abstract boolean isValid();

    public boolean onUpdate() {
        return false;
    }

    public final void scheduleUpdate() {
        this.level.scheduleBlockEntityUpdate(this);
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.chunk != null) {
                this.chunk.removeBlockEntity(this);
            }
            if (this.level != null) {
                this.level.removeBlockEntity(this);
            }
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void onBreak() {

    }

    public void setDirty() {
        chunk.setDirty();
    }

    @Override
    public boolean isMovable() {
        return movable;
    }

    @Override
    public void setMovable(boolean moveble) {
        this.movable = moveble;
    }

    /**
     * Gets the name of this object.
     *
     * @return The name of this object.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    @Nullable
    public final String getCustomName() {
        return customName;
    }

    /**
     * Changes the name of this object, or names it.
     *
     * @param customName The new name of this object.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final void setCustomName(@Nullable String customName) {
        this.customName = customName;
    }

    /**
     * Whether this object has a name.
     *
     * @return {@code true} for this object has a name.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final boolean hasCustomName() {
        return this.customName != null;
    }

    public void spawnTo(Player player) {
        if (this.closed) {
            return;
        }

        CompoundTag tag = this.getShortNBT();
        BlockEntityDataPacket packet = new BlockEntityDataPacket();
        packet.setBlockPosition(this.getPosition());
        packet.setData(getShortNBT());
        player.sendPacket(packet);
    }

    public void spawnToAll() {
        if (this.closed) {
            return;
        }

        for (Player player : this.getChunk().getPlayerLoaders()) {
            if (player.spawned) {
                this.spawnTo(player);
            }
        }
    }

    public final boolean updateFromClient(CompoundTag tag, Player player) {
        if (!tag.getString("id").equals(BlockEntityRegistry.get().getPersistentId(this.getType()))) {
            return false;
        }
        return this.updateCompoundTag(tag, player);
    }

    /**
     * Called when a player updates a block entity's NBT data
     * for example when writing on a sign.
     *
     * @param nbt    tag
     * @param player player
     * @return bool indication of success, will respawn the tile to the player if false.
     */
    protected boolean updateCompoundTag(CompoundTag nbt, Player player) {
        return false;
    }

    @Override
    public boolean isSpawnable() {
        return false;
    }

    @Override
    public Vector3i getPosition() {
        return position;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Block getBlock() {
        return this.chunk.getBlock(this.position.getX() & 0xf, this.position.getY() & 0xff, this.position.getZ() & 0xf);
    }
}
