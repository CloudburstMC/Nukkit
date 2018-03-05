package cn.nukkit.api.block;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface BlockSnapshot {

    BlockState getBlockState();
}
