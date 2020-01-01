package cn.nukkit.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum PlayerFlag {
    SLEEP(1);

    private static final Int2ObjectMap<PlayerFlag> ID_MAP = new Int2ObjectOpenHashMap<>();

    static {
        for (PlayerFlag flag : values()) {
            ID_MAP.put(flag.id, flag);
        }
    }

    private final int id;

    PlayerFlag(int id) {
        this.id = id;
    }

    public static PlayerFlag from(int id) {
        return ID_MAP.get(id);
    }

    public int getId() {
        return id;
    }
}
