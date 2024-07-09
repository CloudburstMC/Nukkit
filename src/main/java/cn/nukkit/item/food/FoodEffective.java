package cn.nukkit.item.food;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.potion.Effect;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Snake1999 on 2016/1/13.
 * Package cn.nukkit.item.food in project nukkit.
 */
public class FoodEffective extends Food {

    protected final Map<Effect, Float> effects = new LinkedHashMap<>();

    public FoodEffective(int restoreFood, float restoreSaturation) {
        this.setRestoreFood(restoreFood);
        this.setRestoreSaturation(restoreSaturation);
    }

    public FoodEffective addEffect(Effect effect) {
        return addChanceEffect(1F, effect);
    }

    public FoodEffective addChanceEffect(float chance, Effect effect) {
        if (chance > 1f) chance = 1f;
        if (chance < 0f) chance = 0f;
        effects.put(effect, chance);
        return this;
    }

    @Override
    protected boolean onEatenBy(Player player) {
        super.onEatenBy(player);
        effects.forEach((effect, chance) -> {
            if (chance >= Math.random()) {
                player.addEffect(effect.clone(), EntityPotionEffectEvent.Cause.FOOD);
            }
        });
        return true;
    }
}
