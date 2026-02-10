package cn.nukkit.item;

public class ItemOminousTrialKey extends Item {

    public ItemOminousTrialKey() {
        this(0, 1);
    }

    public ItemOminousTrialKey(Integer meta) {
        this(meta, 1);
    }

    public ItemOminousTrialKey(Integer meta, int count) {
        super(OMINOUS_TRIAL_KEY, meta, count, "Ominous Trial Key");
    }
}
