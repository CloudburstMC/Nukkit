package com.nukkitx.api.entity.component;

import com.nukkitx.api.util.data.BlockFace;

public interface Hangable {

    BlockFace getDirection();

    void setDirection(BlockFace direction);
}
