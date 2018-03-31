package cn.nukkit.server.block;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.server.item.NukkitItemInstance;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlockUtil {
    public static final NukkitItemInstance AIR = new NukkitItemInstance(BlockTypes.AIR);
}
