package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.impl.BaseBlockEntity;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.chunk.ChunkDataLoader;
import cn.nukkit.level.provider.leveldb.LevelDBKey;
import cn.nukkit.registry.BlockEntityRegistry;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.nbt.stream.NBTInputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import lombok.RequiredArgsConstructor;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlockEntitySerializer {

    public static void loadBlockEntities(DB db, ChunkBuilder builder) {
        byte[] key = LevelDBKey.BLOCK_ENTITIES.getKey(builder.getX(), builder.getZ());

        byte[] value = db.get(key);
        if (value == null) {
            return;
        }

        List<CompoundTag> blockEntityTags = new ArrayList<>();
        try (ByteArrayInputStream stream = new ByteArrayInputStream(value);
             NBTInputStream nbtInputStream = NbtUtils.createReaderLE(stream)) {
            while (stream.available() > 0) {
                blockEntityTags.add((CompoundTag) nbtInputStream.readTag());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        builder.dataLoader(new BlockEntityLoader(blockEntityTags));
    }

    public static void saveBlockEntities(WriteBatch db, Chunk chunk) {
        byte[] key = LevelDBKey.BLOCK_ENTITIES.getKey(chunk.getX(), chunk.getZ());
        if (chunk.getBlockEntities().isEmpty()) {
            db.delete(key);
            return;
        }

        Collection<BaseBlockEntity> entities = chunk.getBlockEntities();

        byte[] value;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(stream)) {
            for (BaseBlockEntity entity : entities) {
                nbtOutputStream.write(entity.getFullNBT());
            }
            value = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        db.put(key, value);
    }

    @RequiredArgsConstructor
    private static class BlockEntityLoader implements ChunkDataLoader {
        private static final BlockEntityRegistry REGISTRY = BlockEntityRegistry.get();
        private final List<CompoundTag> blockEntityTags;

        @Override
        public boolean load(Chunk chunk) {
            boolean dirty = false;
            for (CompoundTag tag : blockEntityTags) {
                if (tag != null) {
                    if (!tag.contains("id")) {
                        dirty = true;
                        continue;
                    }
                    Vector3i position = Vector3i.from(tag.getInt("x"), tag.getInt("y"), tag.getInt("y"));
                    if ((position.getX() >> 4) != chunk.getX() || ((position.getZ() >> 4) != chunk.getZ())) {
                        dirty = true;
                        continue;
                    }
                    BlockEntityType<?> type = REGISTRY.getBlockEntityType(tag.getString("id"));

                    BlockEntity blockEntity = REGISTRY.newEntity(type, chunk, position);
                    if (blockEntity == null) {
                        dirty = true;
                    }
                }
            }
            return dirty;
        }
    }
}
