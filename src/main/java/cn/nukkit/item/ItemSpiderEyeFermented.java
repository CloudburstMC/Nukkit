package cn.nukkit.item;

/**
 * @author Leonidius20
 * @since 18.08.18
 */
public class ItemSpiderEyeFermented extends Item {

    public ItemSpiderEyeFermented() {
        this(0);
    }

    public ItemSpiderEyeFermented(Integer meta) {
        this(0, 1);
    }

    public ItemSpiderEyeFermented(Integer meta, int count) {
        super(FERMENTED_SPIDER_EYE, meta, count, "Fermented Spider Eye");
    }
}
