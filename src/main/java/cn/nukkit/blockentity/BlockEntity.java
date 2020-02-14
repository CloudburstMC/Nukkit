package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

public interface BlockEntity {

    BlockEntityType<?> getType();

    Vector3i getPosition();

    Level getLevel();

    void loadAdditionalData(CompoundTag tag);

    void saveAdditionalData(CompoundTagBuilder tag);

    CompoundTag getFullNBT();

    CompoundTag getShortNBT();

    CompoundTag getCleanNBT();

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

    Block getBlock();

    void spawnToAll();

    void spawnTo(Player player);
}
