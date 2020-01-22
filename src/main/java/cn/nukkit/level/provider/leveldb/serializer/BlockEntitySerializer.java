package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.chunk.ChunkDataLoader;
import cn.nukkit.level.provider.leveldb.LevelDBKey;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.RequiredArgsConstructor;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
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
        try (ByteArrayInputStream stream = new ByteArrayInputStream(value)) {
            while (stream.available() > 0) {
                blockEntityTags.add(NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN));
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

        Collection<BlockEntity> entities = chunk.getBlockEntities();

        byte[] value;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (BlockEntity entity : entities) {
                NBTIO.write(entity.namedTag, stream, ByteOrder.LITTLE_ENDIAN);
            }
            value = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        db.put(key, value);
    }

    @RequiredArgsConstructor
    private static class BlockEntityLoader implements ChunkDataLoader {
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
                    if ((tag.getInt("x") >> 4) != chunk.getX() || ((tag.getInt("z") >> 4) != chunk.getZ())) {
                        dirty = true;
                        continue;
                    }
                    BlockEntity blockEntity = BlockEntity.createBlockEntity(tag.getString("id"), chunk, tag);
                    if (blockEntity == null) {
                        dirty = true;
                    }
                }
            }
            return dirty;
        }
    }
}
