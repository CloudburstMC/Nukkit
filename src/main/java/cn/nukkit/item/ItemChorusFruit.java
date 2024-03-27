package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

public class ItemChorusFruit extends ItemEdible {

    public ItemChorusFruit() {
        this(0, 1);
    }

    public ItemChorusFruit(Integer meta) {
        this(meta, 1);
    }

    public ItemChorusFruit(Integer meta, int count) {
        super(CHORUS_FRUIT, 0, count, "Chorus Fruit");
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return player.getServer().getTick() - player.getLastChorusFruitTeleport() >= 20;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (super.onUse(player, ticksUsed)) {
            player.onChorusFruitTeleport();
        }
        return true;
    }
}
