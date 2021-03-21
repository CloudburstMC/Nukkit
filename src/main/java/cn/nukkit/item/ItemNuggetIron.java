package cn.nukkit.item;

/**
 * @author good777LUCKY
 */
public class ItemNuggetIron extends Item {

    public ItemNuggetIron() {
        this(0, 1);
    }
    
    public ItemNuggetIron(Integer meta) {
        this(meta, 1);
    }
    
    public ItemNuggetIron(Integer meta, int count) {
        super(IRON_NUGGET, meta, count, "Iron Nugget");
    }
}
