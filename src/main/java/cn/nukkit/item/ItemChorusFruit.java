package cn.nukkit.item;

import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

/**
 * Created by Leonidius20 on 20.08.18.
 */
public class ItemChorusFruit extends ItemEdible {

    public ItemChorusFruit(Identifier id) {
        super(id);
    }

    @Override
    public boolean onClickAir(Player player, Vector3f directionVector) {
        return player.getServer().getTick() - player.getLastChorusFruitTeleport() >= 20;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        boolean successful = super.onUse(player, ticksUsed);
        if (successful) {
            player.onChorusFruitTeleport();
        }
        return successful;
    }
}