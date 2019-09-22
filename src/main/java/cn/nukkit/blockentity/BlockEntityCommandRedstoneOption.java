package cn.nukkit.blockentity;

import java.util.ArrayList;
import java.util.List;

public enum BlockEntityCommandRedstoneOption {

    NEEDS_REDSTONE("Needs Redstone"), ALWAYS_ACTIVE("Always Active");

    private String name;

    BlockEntityCommandRedstoneOption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (BlockEntityCommandRedstoneOption t : BlockEntityCommandRedstoneOption.values()) {
            names.add(t.getName());
        }
        return names;
    }
}
