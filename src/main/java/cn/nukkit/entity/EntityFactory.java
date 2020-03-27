package cn.nukkit.entity;

import cn.nukkit.level.Location;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {

    T create(EntityType<T> type, Location location);
}
