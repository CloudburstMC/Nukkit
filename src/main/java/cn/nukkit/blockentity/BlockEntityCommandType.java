package cn.nukkit.blockentity;

import java.util.ArrayList;
import java.util.List;

public enum BlockEntityCommandType {
    COMMAND_BLOCK_TYPE_IMPULSE("Impulse"), COMMAND_BLOCK_TYPE_CHAIN("Chain"), COMMAND_BLOCK_TYPE_REPEAT("Repeat");

    private String name;

    BlockEntityCommandType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (BlockEntityCommandType t : BlockEntityCommandType.values()) {
            names.add(t.getName());
        }
        return names;
    }
}
