package cn.nukkit.item;

public class JungleDoor extends Item {
    public JungleDoor() {
        this(0, 1);
    }

    public JungleDoor(Integer meta) {
        this(meta, 1);
    }

    public JungleDoor(Integer meta, int count) {
        super(JUNGLE_DOOR, 0, count, "Jungle Door");
        this.block = new cn.nukkit.block.JungleDoor();
    }

}
