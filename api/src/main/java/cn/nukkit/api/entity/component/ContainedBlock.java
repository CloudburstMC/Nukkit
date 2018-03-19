package cn.nukkit.api.entity.component;

import cn.nukkit.api.block.BlockState;

public interface ContainedBlock extends EntityComponent {

    BlockState getBlockState();
}
