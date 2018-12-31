package cn.nukkit.item;

public class ItemPotionLingering extends Item {

    public ItemPotionLingering() {
        this(0, 1);
    }

    public ItemPotionLingering(Integer meta) {
        this(meta, 1);
    }

    public ItemPotionLingering(Integer meta, int count) {
        super(POTION, meta, count, "Lingering Potion");
    }

}