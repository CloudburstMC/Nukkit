package cn.nukkit.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.math.BlockFace;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class TransparentBlocksWhitelist {
    public static int[] transparentExceptions = new int[] {
            BlockID.SEA_LANTERN,
            BlockID.GLOWSTONE,
            BlockID.GLASS,
            BlockID.COBBLE_WALL,
            BlockID.STONE_WALL,
            BlockID.COBBLESTONE_WALL,
            BlockID.ACACIA_WOOD_STAIRS,
            BlockID.BIRCH_WOOD_STAIRS,
            BlockID.DARK_OAK_WOOD_STAIRS,
            BlockID.FENCE,
            BlockID.SLAB
    };

    public static boolean isException(Integer blockid, Block block, BlockFace face, Block target) {
        if(Arrays.stream(transparentExceptions).anyMatch(blockid::equals)) {
            switch (blockid) {
                case BlockID.COBBLE_WALL:
                    return face == BlockFace.UP;
                case BlockID.BIRCH_WOOD_STAIRS:
                case BlockID.ACACIA_WOOD_STAIRS:
                case BlockID.DARK_OAK_WOOD_STAIRS: //TODO: isUpsideDown method
                    return target.getPropertyValue(UPSIDE_DOWN);
                default: return true;
            }
        }
        return false;
    }
}
