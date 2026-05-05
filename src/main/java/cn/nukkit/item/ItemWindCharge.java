package cn.nukkit.item;

public class ItemWindCharge extends ProjectileItem {

    public ItemWindCharge() {
        this(0, 1);
    }

    public ItemWindCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemWindCharge(Integer meta, int count) {
        super(WIND_CHARGE, meta, count, "Wind Charge");
    }

    @Override
    public String getProjectileEntityType() {
        return "WindCharge";
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}
