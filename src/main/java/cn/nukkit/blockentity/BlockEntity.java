package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;

import javax.annotation.Nullable;

public interface BlockEntity {

    BlockEntityType<?> getType();

    Vector3i getPosition();

    Level getLevel();

    void loadAdditionalData(CompoundTag tag);

    void saveAdditionalData(CompoundTagBuilder tag);

    /**
     * Gets the NBT for items with block entity data
     * Contains all server side NBT without ID and position
     *
     * @return block entity tag
     */
    CompoundTag getItemTag();

    /**
     * Get the NBT for saving the block entity to disk
     * Contains all server side NBT with ID and position
     *
     * @return block entity tag
     */
    CompoundTag getServerTag();

    /**
     * Get the NBT for saving sending to the client in {@link BlockEntityDataPacket}
     * Contains all server side NBT with ID but no position
     *
     * @return block entity tag
     */
    CompoundTag getClientTag();

    /**
     * Gets the block entity NBT that is sent in a chunk packet
     * Contains no server side NBT
     *
     * @return block entity tag
     */
    CompoundTag getChunkTag();

    boolean isValid();

    boolean isClosed();

    void close();

    boolean isMovable();

    void setMovable(boolean movable);

    @Nullable
    String getCustomName();

    void setCustomName(@Nullable String customName);

    boolean hasCustomName();

    boolean isSpawnable();

    boolean updateFromClient(CompoundTag tag, Player player);

    Block getBlock();

    void spawnToAll();

    void spawnTo(Player player);

    void scheduleUpdate();

    void setDirty();

    boolean onUpdate();

    void onBreak();
}
