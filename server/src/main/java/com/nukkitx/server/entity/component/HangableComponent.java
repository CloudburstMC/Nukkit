package com.nukkitx.server.entity.component;

import com.nukkitx.api.entity.component.Hangable;
import com.nukkitx.api.util.data.BlockFace;

public class HangableComponent implements Hangable {
    private BlockFace direction;

    @Override
    public BlockFace getDirection() {
        return direction;
    }

    @Override
    public void setDirection(BlockFace direction) {
        this.direction = direction;
    }
}
