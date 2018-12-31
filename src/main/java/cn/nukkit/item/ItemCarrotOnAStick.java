package cn.nukkit.item;

/**
 * Created by lion on 21.03.17.
 */
public class ItemCarrotOnAStick extends ItemTool {

    public ItemCarrotOnAStick() {
        this(0, 1);
    }

    public ItemCarrotOnAStick(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrotOnAStick(Integer meta, int count) {
        super(CARROT_ON_A_STICK, meta, count, "Carrot on a stick");
    }

}

