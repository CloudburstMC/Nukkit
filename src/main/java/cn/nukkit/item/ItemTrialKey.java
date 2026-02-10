package cn.nukkit.item;

public class ItemTrialKey extends Item {

    public ItemTrialKey() {
        this(0, 1);
    }

    public ItemTrialKey(Integer meta) {
        this(meta, 1);
    }

    public ItemTrialKey(Integer meta, int count) {
        super(TRIAL_KEY, meta, count, "Trial Key");
    }
}
