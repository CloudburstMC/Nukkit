package com.nukkitx.api.block;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface BlockSnapshot {

    BlockState getBlockState();
}
