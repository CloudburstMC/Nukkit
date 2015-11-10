package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.network.protocol.UpdateAttributesPacket;

/**
 * Created by funcraft on 2015/11/11.
 */
public class PlayerFood {

    private Player player;

    public PlayerFood(Player player, int foodLevel, int foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public Player getPlayer() { return  this.player; }

    private int foodLevel = 20;
    private int foodSaturationLevel = 20;
    private int foodTickTimer = 0;
    private int foodExpLevel = 0;

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public int getFoodSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodSaturationLevel(int foodSaturationLevel) {
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public void sendFoodLevel() {
        this.sendFoodLevel(this.getFoodLevel());
    }

    public void sendFoodLevel(int foodLevel) {
        UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
        updateAttributesPacket.entityId = 0;
        updateAttributesPacket.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HUNGER).setMaxValue(20).setValue(foodLevel)
        };
        this.getPlayer().dataPacket(updateAttributesPacket);
    }

}
