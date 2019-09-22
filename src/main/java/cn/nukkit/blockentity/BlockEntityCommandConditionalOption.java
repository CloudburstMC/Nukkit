
package cn.nukkit.blockentity;

import java.util.ArrayList;
import java.util.List;

public enum BlockEntityCommandConditionalOption {

    UNCONDITIONAL("Unconditional"), CONDITIONAL("Conditional");

    private String name;

    BlockEntityCommandConditionalOption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (BlockEntityCommandConditionalOption t : BlockEntityCommandConditionalOption.values()) {
            names.add(t.getName());
        }
        return names;
    }
}
