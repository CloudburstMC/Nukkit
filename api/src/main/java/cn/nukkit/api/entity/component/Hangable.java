package cn.nukkit.api.entity.component;

import cn.nukkit.api.util.data.BlockFace;

public interface Hangable {

    BlockFace getDirection();

    void setDirection(BlockFace direction);
}
