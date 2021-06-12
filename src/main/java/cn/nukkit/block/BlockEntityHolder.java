package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.LevelException;
import jdk.nashorn.internal.objects.annotations.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public interface BlockEntityHolder<E extends BlockEntity> {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    default E getBlockEntity() {
        Level level = getLevel();
        if (level == null) {
            throw new LevelException("Undefined Level reference");
        }
        BlockEntity blockEntity;
        if (this instanceof Vector3) {
            blockEntity = level.getBlockEntity((Vector3) this);
        } else if (this instanceof BlockVector3) {
            blockEntity = level.getBlockEntity((BlockVector3) this);
        } else {
            blockEntity = level.getBlockEntity(new BlockVector3(getFloorX(), getFloorY(), getFloorZ()));
        }

        Class<? extends E> blockEntityClass = getBlockEntityClass();
        if (blockEntityClass.isInstance(blockEntity)) {
            return blockEntityClass.cast(blockEntity);
        }
        return null;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default E createBlockEntity() {
        return createBlockEntity(null);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default E createBlockEntity(@Nullable CompoundTag initialData, @Nullable Object... args) {
        String typeName = getBlockEntityType();
        FullChunk chunk = getChunk();
        if (chunk == null) {
            throw new LevelException("Undefined Level or chunk reference");
        }
        if (initialData == null) {
            initialData = new CompoundTag();
        } else {
            initialData = initialData.copy();
        }
        BlockEntity created = BlockEntity.createBlockEntity(typeName, chunk, 
                initialData
                    .putString("id", typeName)
                    .putInt("x", getFloorX())
                    .putInt("y", getFloorY())
                    .putInt("z", getFloorZ()), 
                args);

        Class<? extends E> entityClass = getBlockEntityClass();

        if (!entityClass.isInstance(created)) {
            String error = "Failed to create the block entity " + typeName + " of class " + entityClass + " at " + getLocation() + ", " +
                    "the created type is not an instance of the requested class. Created: " + created;
            if (created != null) {
                created.close();
            }
            throw new IllegalStateException(error);
        }
        return entityClass.cast(created);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    default E getOrCreateBlockEntity() {
        E blockEntity = getBlockEntity();
        if (blockEntity != null) {
            return blockEntity;
        }
        return createBlockEntity();
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Class<? extends E> getBlockEntityClass();
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    String getBlockEntityType();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    FullChunk getChunk();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int getFloorX();
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int getFloorY();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int getFloorZ();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Location getLocation();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Getter
    Level getLevel();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    static <E extends BlockEntity, H extends BlockEntityHolder<E>> E setBlockAndCreateEntity(@Nonnull H holder) {
        return setBlockAndCreateEntity(holder, true, true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    static <E extends BlockEntity, H extends BlockEntityHolder<E>> E setBlockAndCreateEntity(
            @Nonnull H holder, boolean direct, boolean update) {
        return setBlockAndCreateEntity(holder, direct, update, null);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    static <E extends BlockEntity, H extends BlockEntityHolder<E>> E setBlockAndCreateEntity(
            @Nonnull H holder, boolean direct, boolean update, @Nullable CompoundTag initialData,
            @Nullable Object... args) {
        Block block = holder.getBlock(); 
        Level level = block.getLevel();
        Block layer0 = level.getBlock(block, 0);
        Block layer1 = level.getBlock(block, 1);
        if (level.setBlock(block, block, direct, update)) {
            try {
                return holder.createBlockEntity(initialData, args);
            } catch (Exception e) {
                Loggers.logBlocKEntityHolder.warn("Failed to create block entity {} at {} at ", holder.getBlockEntityType(), holder.getLocation(), e);
                level.setBlock(layer0, 0, layer0, direct, update);
                level.setBlock(layer1, 1, layer1, direct, update);
                throw e;
            }
        }
        
        return null;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    default Block getBlock() {
        if (this instanceof Position) {
            return ((Position) this).getLevelBlock();
        } else if (this instanceof Vector3) {
            return getLevel().getBlock((Vector3) this);
        } else {
            return getLevel().getBlock(getFloorX(), getFloorY(), getFloorZ());
        }
    }
}
