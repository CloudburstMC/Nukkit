package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Hangable;
import cn.nukkit.api.util.data.BlockFace;

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
