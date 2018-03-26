package cn.nukkit.server.block.entity;

import cn.nukkit.api.block.entity.SignBlockEntity;
import com.google.common.base.Preconditions;

import java.util.Arrays;

public class NukkitSignBlockEntity implements SignBlockEntity {
    private final String[] lines;

    public NukkitSignBlockEntity(String[] lines) {
        this.lines = lines;
    }

    @Override
    public String getLine(int num) {
        checkLineNumber(num);
        return lines[num];
    }

    @Override
    public String setLine(int num, String line) {
        checkLineNumber(num);
        return lines[num] = line;
    }

    @Override
    public String[] getLines() {
        return Arrays.copyOf(lines, 4);
    }

    public static void checkLineNumber(int num) {
        Preconditions.checkArgument(num >= 0 && num < 4, "Line number must be between 0 and 3");
    }
}
