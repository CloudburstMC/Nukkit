package cn.nukkit.entity;

import cn.nukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityProvider<T extends Entity> {
    private final List<EntityFactory<T>> priority = new ArrayList<>();

    public EntityProvider(Map<Plugin, EntityFactory<T>> factoryMap) {
    }
}
