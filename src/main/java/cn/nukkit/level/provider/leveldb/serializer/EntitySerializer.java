package cn.nukkit.level.provider.leveldb.serializer;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.ChunkBuilder;
import cn.nukkit.level.chunk.ChunkDataLoader;
import cn.nukkit.level.provider.leveldb.LevelDBKey;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.registry.RegistryException;
import cn.nukkit.utils.Identifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.DB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
public class EntitySerializer {

    public static void loadEntities(DB db, ChunkBuilder builder) {
        byte[] key = LevelDBKey.ENTITIES.getKey(builder.getX(), builder.getZ());

        byte[] value = db.get(key);
        if (value == null) {
            return;
        }

        List<CompoundTag> entityTags = new ArrayList<>();
        try (ByteArrayInputStream stream = new ByteArrayInputStream(value)) {
            while (stream.available() > 0) {
                entityTags.add(NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        builder.dataLoader(new DataLoader(entityTags));
    }

    public static void saveEntities(DB db, Chunk chunk) {
        byte[] key = LevelDBKey.ENTITIES.getKey(chunk.getX(), chunk.getZ());
        Set<Entity> entities = chunk.getEntities();
        if (entities.isEmpty()) {
            db.delete(key);
            return;
        }

        byte[] value;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            for (Entity entity : entities) {
                entity.saveNBT();
                NBTIO.write(entity.namedTag, stream, ByteOrder.LITTLE_ENDIAN);
            }
            value = stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        db.put(key, value);
    }

    @RequiredArgsConstructor
    private static class DataLoader implements ChunkDataLoader {
        private final List<CompoundTag> entityTags;

        @Override
        public boolean load(Chunk chunk) {
            boolean dirty = false;
            for (CompoundTag entityTag : entityTags) {
                if (!entityTag.contains("identifier")) {
                    dirty = true;
                    continue;
                }
                ListTag pos = entityTag.getList("Pos");
                if ((((NumberTag) pos.get(0)).getData().intValue() >> 4) != chunk.getX() ||
                        ((((NumberTag) pos.get(2)).getData().intValue() >> 4) != chunk.getZ())) {
                    dirty = true;
                    continue;
                }
                Identifier identifier = Identifier.fromString(entityTag.getString("identifier"));
                EntityRegistry registry = EntityRegistry.get();
                EntityType<?> type = registry.getEntityType(identifier);
                try {
                    registry.newEntity(type, chunk, entityTag);
                } catch (RegistryException e) {
                    dirty = true;
                }
            }
            return dirty;
        }
    }
}
