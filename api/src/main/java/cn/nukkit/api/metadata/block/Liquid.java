package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Liquid implements Metadata {
    private final byte level;

    public static Liquid of(int level) {
        Preconditions.checkArgument(level >= 0 && level < 8, "level is not valid (wanted 0-7)");
        return new Liquid((byte) level);
    }

    public byte getLevel() {
        return level;
    }
}
