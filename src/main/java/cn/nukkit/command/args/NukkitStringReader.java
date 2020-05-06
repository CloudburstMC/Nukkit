package cn.nukkit.command.args;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NukkitStringReader {
    private final String string;
    private int cursor;

    public char peek() {
        return string.charAt(cursor);
    }

    public boolean canRead(int length) {
        return cursor + length <= string.length();
    }

    public boolean canRead() {
        return canRead(1);
    }
}
