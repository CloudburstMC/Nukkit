package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;

/**
 * @author xtypr
 * @since 2015/12/7
 */
@Deprecated
@DeprecationDetails(since = "FUTURE", by = "PowerNukkit", 
        reason = "Duplicated of BlockBricksNether and the other one is used instead of this one.",
        replaceWith = "BlockBricksNether"
)
@PowerNukkitDifference(since = "FUTURE", extendsOnlyInPowerNukkit = BlockBricksNether.class, insteadOf = BlockSolid.class)
@SuppressWarnings({"DeprecatedIsStillUsed", "java:S1133"})
public class BlockNetherBrick extends BlockBricksNether {
    @Deprecated
    @DeprecationDetails(since = "FUTURE", by = "PowerNukkit",
            reason = "Duplicated of BlockBricksNether and the other one is used instead of this one.",
            replaceWith = "BlockBricksNether"
    )
    public BlockNetherBrick() {
        // Does nothing
    }
}
