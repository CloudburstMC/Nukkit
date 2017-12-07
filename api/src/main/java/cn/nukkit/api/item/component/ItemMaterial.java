package cn.nukkit.api.item.component;

import java.util.Optional;

public enum ItemMaterial implements ItemComponent {
    // Armor
    LEATHER,
    CHAIN,

    // Tools
    NONE(1f),
    WOOD(2f),
    STONE(4f),
    GOLD(12f),
    IRON(6f),
    DIAMOND(8f);

    final Float toolMultiplier;

    ItemMaterial() {
        this(null);
    }

    ItemMaterial(Float toolMmultiplier) {
        this.toolMultiplier = toolMmultiplier;
    }

    public Optional<Float> getToolMultiplier() {
        return Optional.ofNullable(toolMultiplier);
    }
}
