package cn.nukkit.item;

/**
 * @author joserobjr
 */
public class ItemHoneycomb extends Item {
    
    public ItemHoneycomb() {
        this(0, 1);
    }
    
    public ItemHoneycomb(Integer meta) {
        this(meta, 1);
    }
    
    public ItemHoneycomb(Integer meta, int count) {
        super(HONEYCOMB, meta, count, "Honeycomb");
    }
    
}
