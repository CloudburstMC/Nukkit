package cn.nukkit.food;

import cn.nukkit.Player;
import cn.nukkit.entity.Effect;

import java.util.*;

/**
 * Created by Snake1999 on 2016/1/13.
 * Package cn.nukkit.food in project nukkit.
 */
public class FoodEffective extends Food {

    protected Map<Effect, Float> effects = new LinkedHashMap<>();

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
        List<Effect> toApply = new LinkedList<>();
        effects.forEach((effect, chance) -> {
            if (chance >= Math.random()) toApply.add(effect);
        });
        toApply.forEach(player::addEffect);
        return true;
    }
}
