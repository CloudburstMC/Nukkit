package cn.nukkit.level.format.leveldb.serializer;

import cn.nukkit.entity.Entity;
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
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class EntitySerializer {

    public static void loadEntities(DB db, ChunkBuilder builder) {
        byte[] key = LevelDBKey.ENTITIES.getKey(builder.getX(), builder.getZ(), builder.getProvider().getLevel().getDimension());

        byte[] value = db.get(key);
        if (value == null) {
            return;
        }

        List<CompoundTag> entityTags = new ObjectArrayList<>();
        try (ByteArrayInputStream stream = new ByteArrayInputStream(value)) {
            while (stream.available() > 0) {
                deserializeNbt(NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN), entityTags::add);
            }
            builder.dataLoader((chunk, provider) -> chunk.setNbtEntities(entityTags));
        } catch (IOException e) {
            throw new RuntimeException("Unable to deserialize entity NBT", e);
        }
    }

    public static void saveEntities(WriteBatch db, LevelDBChunk chunk) {
        byte[] key = LevelDBKey.ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getLevel().getDimension());
        Collection<Entity> entities = chunk.getEntities().values();
        if (entities.isEmpty()) {
            db.delete(key);
            return;
        }

        byte[] value;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (Entity entity : entities) {
                if (entity.canSaveToStorage() && !entity.closed) {
                    // Player data are still saved externally
                    entity.saveNBT();
                    serializeNbt(entity.namedTag, nbt -> writeSilently(nbt, stream));
                }
            }
            value = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Can not create out stream", e);
        }
        db.put(key, value);
    }

    private static void deserializeNbt(CompoundTag nbt, Consumer<CompoundTag> handle) {
        // TODO: convert LevelDB format to our legacy
        if (!nbt.contains("id") || !nbt.contains("Pos")) {
            return;
        }
        handle.accept(nbt);
    }

    private static void serializeNbt(CompoundTag nbt, Consumer<CompoundTag> handle) {
        // TODO: use identifiers instead of class names here
        // might do some validation here too
        handle.accept(nbt);
    }

    private static void writeSilently(CompoundTag nbt, OutputStream stream) {
        try {
            NBTIO.write(nbt, stream, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write entity NBT", e);
        }
    }
}
