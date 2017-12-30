package cn.nukkit.api.item.component;

import java.util.Optional;

public enum MaterialItemComponent implements ItemComponent {
    // Armor
    LEATHER,
    CHAIN,

    // Tools
    NONE(1f),
    WOOD(2f),
    STONE(4f),
    IRON(6f),
    GOLD(12f),
    DIAMOND(8f);

    final Float toolMultiplier;

    MaterialItemComponent() {
        this(null);
    }

    MaterialItemComponent(Float toolMmultiplier) {
        this.toolMultiplier = toolMmultiplier;
    }

    public Optional<Float> getToolMultiplier() {
        return Optional.ofNullable(toolMultiplier);
    }
}
