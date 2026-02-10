package cn.nukkit.item;

public class ItemWindCharge extends Item {

    public ItemWindCharge() {
        this(0, 1);
    }

    public ItemWindCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemWindCharge(Integer meta, int count) {
        super(WIND_CHARGE, meta, count, "Wind Charge");
    }
}
