package cn.nukkit.item;

public class ItemSuspiciousStew extends ItemEdible {
    
    public ItemSuspiciousStew() {
        this(0, 1);
    }
    
    public ItemSuspiciousStew(Integer meta) {
        this(meta, 1);
    }
    
    public ItemSuspiciousStew(Integer meta, int count) {
        super(SUSPICIOUS_STEW, meta, count, "Suspicious Stew");
    }
    
    @Override
    public int getMaxStackSize() {
        return 1;
    }
    
}
