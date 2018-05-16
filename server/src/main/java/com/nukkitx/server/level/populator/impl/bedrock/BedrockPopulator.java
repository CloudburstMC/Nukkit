package com.nukkitx.server.level.populator.impl.bedrock;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.server.block.NukkitBlockState;

/**
 * @author DaPorkchop_
 */
interface BedrockPopulator {
    BlockState BEDROCK = new NukkitBlockState(BlockTypes.BEDROCK, null, null);
}
