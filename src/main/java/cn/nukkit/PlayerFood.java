package cn.nukkit;

import cn.nukkit.entity.Attribute;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.potion.Effect;

/**
 * This class handles player's food.
 * <p>
 * Created by funcraft on 2015/11/11.
 */
public class PlayerFood {

    private int foodLevel;
    private float foodSaturationLevel;
    private int foodTickTimer;
    private double foodExpLevel;

    private final Player player;

    public PlayerFood(Player player, int foodLevel, float foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getLevel() {
        return this.foodLevel;
    }

    public int getMaxLevel() {
        return 20;
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

        if (foodLevel <= 6 && !(this.foodLevel <= 6)) {
            if (this.player.isSprinting()) {
                this.player.setSprinting(false);
            }
        }

        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.player, foodLevel, saturationLevel);
        this.player.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            this.sendFoodLevel(this.foodLevel);
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
        if (fsl > this.foodLevel) fsl = this.foodLevel;
        if (fsl < 0) fsl = 0;
        PlayerFoodLevelChangeEvent ev = new PlayerFoodLevelChangeEvent(this.player, this.foodLevel, fsl);
        this.player.getServer().getPluginManager().callEvent(ev);
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
        float sfl = this.foodSaturationLevel;
        int foodLevel = this.foodLevel;
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
        this.setLevel(this.foodLevel + foodLevel, this.foodSaturationLevel + fsl);
    }

    public void sendFoodLevel() {
        this.sendFoodLevel(this.foodLevel);
    }

    public void reset() {
        this.foodLevel = 20;
        this.foodSaturationLevel = 20;
        this.foodExpLevel = 0;
        this.foodTickTimer = 0;
        this.sendFoodLevel();
    }

    public void sendFoodLevel(int foodLevel) {
        if (this.player.spawned) {
            this.player.setAttribute(Attribute.getAttribute(Attribute.MAX_HUNGER).setValue(foodLevel).setDefaultValue(getMaxLevel()));
        }
    }

    public void update(int tickDiff) {
        if (!this.player.isFoodEnabled()) return;
        if (this.player.isAlive()) {
            int diff = Server.getInstance().getDifficulty();
            if (this.foodLevel > 17 || diff == 0) {
                this.foodTickTimer += tickDiff;
                if (this.foodTickTimer >= 80) {
                    if (this.player.getHealth() < this.player.getRealMaxHealth()) {
                        EntityRegainHealthEvent ev = new EntityRegainHealthEvent(this.player, 1, EntityRegainHealthEvent.CAUSE_EATING);
                        this.player.heal(ev);
                        this.updateFoodExpLevel(6);
                    }
                    this.foodTickTimer = 0;
                }
            } else if (this.foodLevel == 0) {
                this.foodTickTimer += tickDiff;
                if (this.foodTickTimer >= 80) {
                    EntityDamageEvent ev = new EntityDamageEvent(this.player, DamageCause.HUNGER, 1);
                    float now = this.player.getHealth();
                    if (diff == 1) {
                        if (now > 10) this.player.attack(ev);
                    } else if (diff == 2) {
                        if (now > 1) this.player.attack(ev);
                    } else {
                        this.player.attack(ev);
                    }

                    this.foodTickTimer = 0;
                }
            }
            Effect hunger = this.player.getEffect(Effect.HUNGER);
            if (hunger != null) {
                this.updateFoodExpLevel(0.1 * (hunger.getAmplifier() + 1));
            }
        }
    }

    public void updateFoodExpLevel(double use) {
        if (!this.player.isFoodEnabled()) return;
        if (Server.getInstance().getDifficulty() == 0) return;
        if (this.player.hasEffect(Effect.SATURATION)) return;
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

    @Override
    public String toString() {
        return "PlayerFood(player= " + player + ", foodLevel=" + foodLevel + ", foodSaturationLevel=" + foodSaturationLevel + ", foodTickTimer=" + foodTickTimer + ", foodExpLevel=" + foodExpLevel + ")";
    }
}
