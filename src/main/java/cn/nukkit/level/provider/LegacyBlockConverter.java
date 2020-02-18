package cn.nukkit.level.provider;

import cn.nukkit.block.BlockLog;
import cn.nukkit.block.BlockLog2;

public class LegacyBlockConverter {
    private static final LegacyBlockConverter INSTANCE = new LegacyBlockConverter();

    public static LegacyBlockConverter get() {
        return INSTANCE;
    }

    private LegacyBlockConverter() {
    }

    public void convertBlockState(final int[] blockState) {
        if (blockState[0] == 17) { // minecraft:log
            BlockLog.upgradeLegacyBlock(blockState);
        } else if (blockState[0] == 162) { // minecraft:log2
            BlockLog2.upgradeLegacyBlock(blockState);
        }
    }
}
