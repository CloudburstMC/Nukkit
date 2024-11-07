package cn.nukkit.item;

public class ItemRecoveryCompass extends Item {

    public ItemRecoveryCompass() {
        this(0, 1);
    }

    public ItemRecoveryCompass(Integer meta) {
        this(meta, 1);
    }

    public ItemRecoveryCompass(Integer meta, int count) {
        super(RECOVERY_COMPASS, meta, count, "Recovery Compass");
    }
}
