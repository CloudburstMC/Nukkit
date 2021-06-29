package cn.nukkit.item;

public class ItemFireworkStar extends Item {

    public ItemFireworkStar() {
        this(0, 1);
    }

    public ItemFireworkStar(Integer meta) {
        this(meta, 1);
    }

    public ItemFireworkStar(Integer meta, int count) {
        super(FIREWORKSCHARGE, meta, count, "Firework Star");
    }
}
