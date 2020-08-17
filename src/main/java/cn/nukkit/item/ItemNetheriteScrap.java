package cn.nukkit.item;

/**
 * @author Kaooot
 * @version 1.0
 */
public class ItemNetheriteScrap extends Item {

    public ItemNetheriteScrap() {
        this(0, 1);
    }

    public ItemNetheriteScrap(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteScrap(Integer meta, int count) {
        super(NETHERITE_SCRAP, meta, count, "Netherite Scrap");
    }
}