package com.nukkitx.server.entity;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.level.NukkitLevel;
import org.objectweb.asm.ClassReader;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class EntitySpawner {
    private final Map<Class<? extends Entity>, EntityFactory<? extends BaseEntity>> spawnerMap = new ConcurrentHashMap<>();
    private final NukkitServer server;

    public EntitySpawner(NukkitServer server) {
        this.server = server;
    }

    public <T extends BaseEntity> void registerEntity(@Nonnull Class<T> clazz, @Nonnull EntityFactory<T> factory) throws Exception {
        Preconditions.checkNotNull(clazz, "clazz");
        Preconditions.checkNotNull(factory, "factory");

        ClassReader reader = new ClassReader(clazz.getProtectionDomain().getCodeSource().getLocation().openStream());
        EntityClassVisitor visitor = new EntityClassVisitor();
        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        Optional<String> classOptional = visitor.getEntityClass();
        if (classOptional.isPresent()) {
            Class<? extends Entity> apiEntityClass = (Class<? extends Entity>) NukkitServer.class.getClassLoader().loadClass(classOptional.get());
            if (spawnerMap.containsKey(apiEntityClass)) {
                throw new IllegalArgumentException("Class already registered");
            }
            spawnerMap.put(apiEntityClass, factory);
        }

        if (spawnerMap.containsKey(clazz)) {
            throw new IllegalArgumentException("Entity class already registered");
        }
    }

    public <T extends Entity> void unregisterEntity(@Nonnull Class<T> clazz) {
        Preconditions.checkNotNull(clazz, "clazz");
        spawnerMap.remove(clazz);
    }

    public <T extends Entity> T spawnEntity(@Nonnull Class<? extends Entity> clazz, @Nonnull Vector3f position, @Nonnull NukkitLevel level) {
        Preconditions.checkNotNull(clazz, "clazz");
        Preconditions.checkNotNull(position, "blockPosition");
        Preconditions.checkNotNull(level, "level");

        EntityFactory<T> factory;
        Preconditions.checkArgument((factory = (EntityFactory<T>) spawnerMap.get(clazz)) != null, "Entity class is not valid");

        return factory.newInstance(position, level, server);
    }
}
