package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.potion.Effect;

/**
 * Created by funcraft on 2015/11/11.
 */
public class PlayerFood {

    private int foodLevel = 20;
    private final int maxFoodLevel;
    private float foodSaturationLevel = 20f;
    private int foodTickTimer = 0;
    private double foodExpLevel = 0;

    private final Player player;

    public PlayerFood(Player player, int foodLevel, float foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.maxFoodLevel = 20;
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getLevel() {
        return this.foodLevel;
    }

    public int getMaxLevel() {
        return this.maxFoodLevel;
    }

    public void setLevel(int foodLevel) {
        this.setLevel(foodLevel, -1);
    }

    public void setLevel(int foodLevel, float saturationLevel) {
        if (foodLevel > 20) {
            foodLevel = 20;
        }

        if (foodLevel < 0) {
            foodLevel = 0;
        }

        if (foodLevel <= 6 && !(this.getLevel() <= 6)) {
            if (this.getPlayer().isSprinting()) {
                this.getPlayer().setSprinting(false);
            }
        }

        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.getPlayer(), foodLevel, saturationLevel);
        this.getPlayer().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            this.sendFoodLevel(this.getLevel());
            return;
        }
        int foodLevel0 = ev.getFoodLevel();
        float fsl = ev.getFoodSaturationLevel();
        this.foodLevel = foodLevel;
        if (fsl != -1) {
            if (fsl > foodLevel) fsl = foodLevel;
            this.foodSaturationLevel = fsl;
        }
        this.foodLevel = foodLevel0;
        this.sendFoodLevel();
    }

    public float getFoodSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodSaturationLevel(float fsl) {
        if (fsl > this.getLevel()) fsl = this.getLevel();
        if (fsl < 0) fsl = 0;
        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.getPlayer(), this.getLevel(), fsl);
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
        float sfl = this.getFoodSaturationLevel();
        int foodLevel = this.getLevel();
        if (sfl > 0) {
            float newSfl = sfl - amount;
            if (newSfl < 0) newSfl = 0;
            this.setFoodSaturationLevel(newSfl);
        } else {
            this.setLevel(foodLevel - amount);
        }
    }

    public void addFoodLevel(Food food) {
        this.addFoodLevel(food.getRestoreFood(), food.getRestoreSaturation());
    }

    public void addFoodLevel(int foodLevel, float fsl) {
        this.setLevel(this.getLevel() + foodLevel, this.getFoodSaturationLevel() + fsl);
    }

    public void sendFoodLevel() {
        this.sendFoodLevel(this.getLevel());
    }

    public void reset() {
        this.foodLevel = 20;
        this.foodSaturationLevel = 20;
        this.foodExpLevel = 0;
        this.foodTickTimer = 0;
        this.sendFoodLevel();
    }

    public void sendFoodLevel(int foodLevel) {
        if (this.getPlayer().spawned) {
            this.getPlayer().setAttribute(Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(foodLevel));
        }
    }

    public void update(int tickDiff) {
        if (!this.getPlayer().isFoodEnabled()) return;
        if (this.getPlayer().isAlive()) {
            int diff = Server.getInstance().getDifficulty();
            if (this.getLevel() > 17) {
                this.foodTickTimer += tickDiff;
                if (this.foodTickTimer >= 80) {
                    if (this.getPlayer().getHealth() < this.getPlayer().getMaxHealth()) {
                        EntityRegainHealthEvent ev = new EntityRegainHealthEvent(this.getPlayer(), 1, EntityRegainHealthEvent.CAUSE_EATING);
                        this.getPlayer().heal(ev);
                        //this.updateFoodExpLevel(3);
                    }
                    this.foodTickTimer = 0;
                }
            } else if (this.getLevel() == 0) {
                this.foodTickTimer += tickDiff;
                if (this.foodTickTimer >= 80) {
                    EntityDamageEvent ev = new EntityDamageEvent(this.getPlayer(), DamageCause.HUNGER, 1);
                    float now = this.getPlayer().getHealth();
                    if (diff == 1) {
                        if (now > 10) this.getPlayer().attack(ev);
                    } else if (diff == 2) {
                        if (now > 1) this.getPlayer().attack(ev);
                    } else {
                        this.getPlayer().attack(ev);
                    }

                    this.foodTickTimer = 0;
                }
            }
            if (this.getPlayer().hasEffect(Effect.HUNGER)) {
                this.updateFoodExpLevel(0.025);
            }
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

    /**
     * @deprecated use {@link #setLevel(int)} instead
     * @param foodLevel level
     **/
    @Deprecated
    public void setFoodLevel(int foodLevel) {
        setLevel(foodLevel);
    }

    /**
     * @deprecated use {@link #setLevel(int, float)} instead
     * @param foodLevel level
     * @param saturationLevel saturation
     **/
    @Deprecated
    public void setFoodLevel(int foodLevel, float saturationLevel) {
        setLevel(foodLevel, saturationLevel);
    }
}
