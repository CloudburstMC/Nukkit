/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.entity;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
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
        Preconditions.checkNotNull(position, "position");
        Preconditions.checkNotNull(level, "level");

        EntityFactory<T> factory;
        Preconditions.checkArgument((factory = (EntityFactory<T>) spawnerMap.get(clazz)) != null, "Entity class is not valid");

        return factory.newInstance(position, level, server);
    }
}
