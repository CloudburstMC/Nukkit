package cn.nukkit.utils.material.tags;

import cn.nukkit.block.BlockTypes;
import cn.nukkit.item.ItemTypes;
import cn.nukkit.utils.material.MaterialType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collections;
import java.util.Set;

public class LazilyInitializedMaterialTag implements MaterialTag {

    private final String tag;
    private Set<MaterialType> materials;

    public LazilyInitializedMaterialTag(String tag) {
        this.tag = tag;
    }

    private void init() {
        Set<String> definitions = MaterialTags.getVanillaDefinitions(tag);
        if (definitions == null) {
            throw new IllegalStateException("Unknown vanilla tag " + this.tag);
        }

        Set<MaterialType> materials = new ObjectOpenHashSet<>();
        for (String definition : definitions) {
            MaterialType material = BlockTypes.get(definition);
            if (material == null) {
                material = ItemTypes.get(definition);
            }

            if (material != null) {
                materials.add(material);
            }
        }

        this.materials = Collections.unmodifiableSet(materials);
    }

    @Override
    public Set<MaterialType> getMaterials() {
        if (this.materials == null) {
            this.init();
        }
        return this.materials;
    }
}
