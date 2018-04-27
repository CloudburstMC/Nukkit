package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;

public enum MonsterEgg implements Metadata {
    STONE,
    COBBLESTONE,
    MOSSY_STONE,
    CRACKED_STONE_BRICK,
    CHISELED_STONE_BRICK;

    @Override
    public String toString() {
        return "MonsterEgg(" +
                "type=" + name() +
                ')';
    }
}
