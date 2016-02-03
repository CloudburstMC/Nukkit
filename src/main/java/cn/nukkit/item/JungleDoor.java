package cn.nukkit.item;

import cn.nukkit.block.BlockDoorJungle;

public class JungleDoor extends Item {
    public JungleDoor() {
        this(0, 1);
    }

    public JungleDoor(Integer meta) {
        this(meta, 1);
    }

    public JungleDoor(Integer meta, int count) {
        super(JUNGLE_DOOR, 0, count, "Jungle Door");
        this.block = new BlockDoorJungle();
    }

}
