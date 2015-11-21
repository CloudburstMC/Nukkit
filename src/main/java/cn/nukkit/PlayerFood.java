package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Effect;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
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

    public Player getPlayer() {
        return this.player;
    }

    private int foodLevel = 20;
    private int foodSaturationLevel = 20;
    private int foodTickTimer = 0;
    private double foodExpLevel = 0;

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.setFoodLevel(foodLevel, -1);
    }

    public void setFoodLevel(int foodLevel, int FSL) {
        if (foodLevel > 20) foodLevel = 20;
        if (foodLevel < 0) foodLevel = 0;
        if (foodLevel <= 6 && !(this.getFoodLevel() <= 6)) {
            this.getPlayer().setSprinting(false);
        }
        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.getPlayer(), foodLevel, FSL);
        this.getPlayer().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            this.sendFoodLevel(this.getFoodLevel());
            return;
        }
        int foodLevel0 = ev.getFoodLevel();
        int fsl = ev.getFoodSaturationLevel();
        this.foodLevel = foodLevel;
        if (fsl != -1) {
            if (fsl > foodLevel) fsl = foodLevel;
            this.foodSaturationLevel = fsl;
        }
        this.foodLevel = foodLevel0;
        this.getPlayer().getServer().getLogger().debug(this.getPlayer().getName() + " set foodLevel: foodLevel = " + String.valueOf(this.getFoodLevel()) + "  FSL = " + this.getFoodSaturationLevel());
        this.sendFoodLevel();
    }

    public int getFoodSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodSaturationLevel(int fsl) {
        if (fsl > this.getFoodLevel()) fsl = this.getFoodLevel();
        if (fsl < 0) fsl = 0;
        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.getPlayer(), this.getFoodLevel(), fsl);
        this.getPlayer().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }
        fsl = ev.getFoodSaturationLevel();
        this.foodSaturationLevel = fsl;
    }

    public void useHunger() {
        this.useHunger(1);
    }

    public void useHunger(int amount) {
        int sfl = this.getFoodSaturationLevel();
        int foodLevel = this.getFoodLevel();
        if (sfl > 0) {
            int newSfl = sfl - amount;
            if (newSfl < 0) newSfl = 0;
            this.setFoodSaturationLevel(newSfl);
        } else {
            this.setFoodLevel(foodLevel - amount);
        }
    }

    public void addFoodLevel(int foodLevel, int fsl) {
        this.setFoodLevel(this.getFoodLevel() + foodLevel, this.getFoodSaturationLevel() + fsl);
    }

    public void sendFoodLevel() {
        this.sendFoodLevel(this.getFoodLevel());
    }

    public void resetFoodLevel() {
        this.foodLevel = 20;
        this.foodSaturationLevel = 20;
        this.foodExpLevel = 0;
        this.foodTickTimer = 0;
        this.sendFoodLevel();
    }

    public void sendFoodLevel(int foodLevel) {
        UpdateAttributesPacket updateAttributesPacket = new UpdateAttributesPacket();
        updateAttributesPacket.entityId = 0;
        updateAttributesPacket.entries = new Attribute[]{
                Attribute.getAttribute(Attribute.MAX_HUNGER).setMaxValue(20).setValue(foodLevel)
        };
        this.getPlayer().dataPacket(updateAttributesPacket);
    }

    public void updateFoodTickTimer(int tickDiff) {
        if (!this.getPlayer().isFoodEnabled()) return;
        int diff = Server.getInstance().getDifficulty();
        if (this.getFoodLevel() > 17) {
            this.foodTickTimer += tickDiff;
            if (this.foodTickTimer >= 80) {
                if (this.getPlayer().getHealth() < this.getPlayer().getMaxHealth()) {
                    EntityRegainHealthEvent ev = new EntityRegainHealthEvent(this.getPlayer(), 1, EntityRegainHealthEvent.CAUSE_EATING);
                    this.getPlayer().heal(1, ev);
                    //this.updateFoodExpLevel(3);
                }
                this.foodTickTimer = 0;
            }
        } else if (this.getFoodLevel() == 0) {
            this.foodTickTimer += tickDiff;
            if (this.foodTickTimer >= 80) {
                EntityDamageEvent ev = new EntityDamageEvent(this.getPlayer(), EntityDamageEvent.CAUSE_VOID, 1);
                int now = this.getPlayer().getHealth();
                if (diff == 1) {
                    if (now > 10) this.getPlayer().attack(1, ev);
                } else if (diff == 2) {
                    if (now > 1) this.getPlayer().attack(1, ev);
                } else {
                    this.getPlayer().attack(1, ev);
                }

                this.foodTickTimer = 0;
            }
        }
        if (this.getPlayer().hasEffect(Effect.HUNGER)) {
            this.updateFoodExpLevel(0.025);
        }

    }

    public void updateFoodExpLevel(double use) {
        if (!this.getPlayer().isFoodEnabled()) return;
        if (Server.getInstance().getDifficulty() == 0) return;
        if (this.getPlayer().hasEffect(Effect.SATURATION)) return;
        this.foodExpLevel += use;
        if (this.foodExpLevel > 4) {
            this.useHunger(1);
            this.foodExpLevel = 0;
        }
    }

}
