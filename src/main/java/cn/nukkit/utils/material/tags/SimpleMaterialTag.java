package cn.nukkit.utils.material.tags;

import cn.nukkit.utils.material.MaterialType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class SimpleMaterialTag implements MaterialTag {

    private final Set<MaterialType> materials;

    public SimpleMaterialTag(MaterialType... materials) {
        this(Arrays.asList(materials));
    }

    public SimpleMaterialTag(Collection<MaterialType> materials) {
        this.materials = Collections.unmodifiableSet(new ObjectOpenHashSet<>(materials));
    }

    @Override
    public Set<MaterialType> getMaterials() {
        return this.materials;
    }
}
