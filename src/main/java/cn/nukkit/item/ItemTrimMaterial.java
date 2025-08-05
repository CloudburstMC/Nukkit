package cn.nukkit.item;

import lombok.Getter;

public interface ItemTrimMaterial {

    Type getMaterial();

    enum Type {

        AMETHYST("amethyst"),
        COPPER("copper"),
        DIAMOND("diamond"),
        EMERALD("emerald"),
        GOLD("gold"),
        IRON("iron"),
        LAPIS("lapis"),
        NETHERITE("netherite"),
        QUARTZ("quartz"),
        REDSTONE("redstone");

        @Getter
        private final String materialName;

        Type(String input) {
            this.materialName = input;
        }
    }
}
