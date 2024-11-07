package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.leveldb.LevelDBKey;
import cn.nukkit.level.format.leveldb.structure.ChunkBuilder;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;

public class BlockEntitySerializer {

    public static void loadBlockEntities(DB db, ChunkBuilder builder) {
        byte[] key = LevelDBKey.BLOCK_ENTITIES.getKey(builder.getX(), builder.getZ(), builder.getProvider().getLevel().getDimension());

        byte[] value = db.get(key);
        if (value == null) {
            return;
        }

        List<CompoundTag> blockEntities = new ObjectArrayList<>();
        try (ByteArrayInputStream stream = new ByteArrayInputStream(value)) {
            while (stream.available() > 0) {
                blockEntities.add(NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN));
            }
            builder.dataLoader((chunk, provider) -> chunk.setNbtBlockEntities(blockEntities));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveBlockEntities(WriteBatch db, LevelDBChunk chunk) {
        byte[] key = LevelDBKey.BLOCK_ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getLevel().getDimension());
        if (chunk.getBlockEntities().isEmpty()) {
            db.delete(key);
            return;
        }

        Collection<BlockEntity> entities = chunk.getBlockEntities().values();

        byte[] value;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (BlockEntity blockEntity : entities) {
                if (blockEntity.canSaveToStorage()) {
                    blockEntity.saveNBT();
                    NBTIO.write(blockEntity.namedTag, stream, ByteOrder.LITTLE_ENDIAN);
                }
            }
            value = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        db.put(key, value);
    }
}
