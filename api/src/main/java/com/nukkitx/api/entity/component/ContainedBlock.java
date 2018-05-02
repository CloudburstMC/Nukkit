package com.nukkitx.api.entity.component;

import com.nukkitx.api.block.BlockState;

public interface ContainedBlock extends EntityComponent {

    BlockState getBlockState();
}
