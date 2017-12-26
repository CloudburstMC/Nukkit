package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerFoodLevelChangeEvent extends PlayerEvent implements Cancellable {

    protected int foodLevel;
    protected float foodSaturationLevel;
    private boolean cancelled;

    public PlayerFoodLevelChangeEvent(Player player, int foodLevel, float foodSaturationLevel) {
        super(player);
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }
}
