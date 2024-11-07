package cn.nukkit.item;

/**
 * Created by Leonidius20 on 18.08.18.
 */
public class ItemSpiderEyeFermented extends Item {

    public ItemSpiderEyeFermented() {
        this(0, 1);
    }

    public ItemSpiderEyeFermented(Integer meta) {
        this(meta, 1);
    }

    public ItemSpiderEyeFermented(Integer meta, int count) {
        super(FERMENTED_SPIDER_EYE, 0, count, "Fermented Spider Eye");
    }
}
