package cn.nukkit.utils.material.tags;

import cn.nukkit.utils.material.MaterialType;

import java.util.Set;

public interface MaterialTag {

    default boolean has(MaterialType type) {
        return this.getMaterials().contains(type);
    }

    Set<MaterialType> getMaterials();

    static MaterialTag of(MaterialType... materials) {
        return new SimpleMaterialTag(materials);
    }
}
