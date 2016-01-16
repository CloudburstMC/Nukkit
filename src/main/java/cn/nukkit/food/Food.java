package cn.nukkit.food;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Effect;
import cn.nukkit.event.player.PlayerEatFoodEvent;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Snake1999 on 2016/1/13.
 * Package cn.nukkit.food in project nukkit.
 */
public abstract class Food {

    private static Map<NodeIDMeta, Food> registry = new LinkedHashMap<>();

    //some of following are not available yet in MCPE 0.13.1
    public static final Food apple = registerFood(new FoodNormal(4, 2.4F).addRelative(Item.APPLE));
    public static final Food apple_golden = registerFood(new FoodEffective(4, 9.6F)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(5 * 20))
            .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20))
            .addRelative(Item.GOLDEN_APPLE));
    public static final Food apple_golden_enchanted = registerFood(new FoodEffective(4, 9.6F)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(4).setDuration(30 * 20))
            .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20))
            .addEffect(Effect.getEffect(Effect.DAMAGE_RESISTANCE).setDuration(5 * 60 * 20))
            .addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(5 * 60 * 20))
            .addRelative(Item.GOLDEN_APPLE_ENCHANTED));
    public static final Food beef_raw = registerFood(new FoodNormal(3, 1.8F).addRelative(Item.RAW_BEEF));
    public static final Food beetroot = registerFood(new FoodNormal(1, 1.2F).addRelative(Item.BEETROOT));
    public static final Food beetroot_soup = registerFood(new FoodNormal(6, 7.2F).addRelative(Item.BEETROOT_SOUP));
    public static final Food bread = registerFood(new FoodNormal(5, 6F).addRelative(Item.BREAD));
    public static final Food cake_slice = registerFood(new FoodNormal(2, 0.4F)
            .addRelative(Block.CAKE_BLOCK, 0).addRelative(Block.CAKE_BLOCK, 1).addRelative(Block.CAKE_BLOCK, 2)
            .addRelative(Block.CAKE_BLOCK, 3).addRelative(Block.CAKE_BLOCK, 4).addRelative(Block.CAKE_BLOCK, 5)
            .addRelative(Block.CAKE_BLOCK, 6));
    public static final Food carrot = registerFood(new FoodNormal(3, 4.8F).addRelative(Item.CARROT));
    public static final Food carrot_golden = registerFood(new FoodNormal(6, 14.4F).addRelative(Item.GOLDEN_CARROT));
    public static final Food chicken_raw = registerFood(new FoodEffective(2, 1.2F)
            .addChanceEffect(0.3F, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
            .addRelative(Item.RAW_CHICKEN));
    public static final Food chicken_cooked = registerFood(new FoodNormal(6, 7.2F).addRelative(Item.COOKED_CHICKEN));
    public static final Food chorus_fruit = registerFood(new FoodNormal(4, 2.4F));
    public static final Food cookie = registerFood(new FoodNormal(2, 0.4F).addRelative(Item.COOKIE));
    public static final Food melon_slice = registerFood(new FoodNormal(2, 1.2F).addRelative(Item.MELON_SLICE));
    public static final Food mushroom_stew = registerFood(new FoodInBowl(6, 7.2F).addRelative(Item.MUSHROOM_STEW));
    public static final Food mutton_cooked = registerFood(new FoodNormal(6, 9.6F));
    public static final Food mutton_raw = registerFood(new FoodNormal(2, 1.2F));
    public static final Food porkchop_cooked = registerFood(new FoodNormal(8, 12.8F).addRelative(Item.COOKED_PORKCHOP));
    public static final Food porkchop_raw = registerFood(new FoodNormal(3, 1.8F).addRelative(Item.RAW_PORKCHOP));
    public static final Food potato_raw = registerFood(new FoodNormal(1, 0.6F).addRelative(Item.POTATO));
    public static final Food potato_baked = registerFood(new FoodNormal(5, 7.2F).addRelative(Item.BAKED_POTATO));
    public static final Food potato_poisonous = registerFood(new FoodEffective(2, 1.2F)
            .addChanceEffect(0.6F, Effect.getEffect(Effect.POISON).setDuration(4 * 20))
            .addRelative(Item.POISONOUS_POTATO));
    public static final Food pumpkin_pie = registerFood(new FoodNormal(8, 4.8F).addRelative(Item.PUMPKIN_PIE));
    public static final Food rabbit_cooked = registerFood(new FoodNormal(5, 6F).addRelative(Item.COOKED_RABBIT));
    public static final Food rabbit_raw = registerFood(new FoodNormal(3, 1.8F).addRelative(Item.RAW_RABBIT));
    public static final Food rabbit_stew = registerFood(new FoodInBowl(10, 12F).addRelative(Item.RABBIT_STEW));
    public static final Food rotten_flesh = registerFood(new FoodEffective(4, 0.8F)
            .addChanceEffect(0.8F, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
            .addRelative(Item.ROTTEN_FLESH));
    public static final Food spider_eye = registerFood(new FoodEffective(2, 3.2F)
            .addEffect(Effect.getEffect(Effect.POISON).setDuration(4 * 20))
            .addRelative(Item.SPIDER_EYE));
    public static final Food steak = registerFood(new FoodNormal(8, 12.8F).addRelative(Item.COOKED_BEEF));
    //different kinds of fishes
    public static final Food clownfish = registerFood(new FoodNormal(1, 0.2F).addRelative(Item.CLOWNFISH));
    public static final Food fish_cooked = registerFood(new FoodNormal(5, 6F).addRelative(Item.COOKED_FISH));
    public static final Food fish_raw = registerFood(new FoodNormal(2, 0.4F).addRelative(Item.RAW_FISH));
    public static final Food salmon_cooked = registerFood(new FoodNormal(6, 9.6F).addRelative(Item.COOKED_SALMON));
    public static final Food salmon_raw = registerFood(new FoodNormal(2, 0.4F).addRelative(Item.RAW_SALMON));
    public static final Food pufferfish = registerFood(new FoodEffective(1, 0.2F)
            .addEffect(Effect.getEffect(Effect.HUNGER).setAmplifier(2).setDuration(15 * 20))
            .addEffect(Effect.getEffect(Effect.NAUSEA).setAmplifier(1).setDuration(15 * 20))
            .addEffect(Effect.getEffect(Effect.POISON).setAmplifier(4).setDuration(60 * 20))
            .addRelative(Item.PUFFERFISH));

    public static Food registerFood(Food food) {
        food.relativeIDs.forEach(n -> registry.put(n, food));
        return food;
    }

    public static Food getByRelative(Item item) {
        return getByRelative(item.getId(), item.getDamage());
    }

    public static Food getByRelative(Block block) {
        return getByRelative(block.getId(), block.getDamage());
    }

    public static Food getByRelative(int relativeID, int meta) {
        final Food[] result = {null};
        registry.forEach((n, f) -> {
            if (n.id == relativeID && n.meta == meta) result[0] = f;
        });
        return result[0];
    }

    protected int restoreFood = 0;
    protected float restoreSaturation = 0;
    protected List<NodeIDMeta> relativeIDs = new ArrayList<>();

    public final boolean eatenBy(Player player) {
        PlayerEatFoodEvent event = new PlayerEatFoodEvent(player, this);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        return event.getFood().onEatenBy(player);
    }

    protected boolean onEatenBy(Player player) {
        player.getFoodData().addFoodLevel(this);
        return true;
    }

    public Food addRelative(int relativeID) {
        return addRelative(relativeID, 0);
    }

    public Food addRelative(int relativeID, int meta) {
        NodeIDMeta node = new NodeIDMeta(relativeID, meta);
        return addRelative(node);
    }

    private Food addRelative(NodeIDMeta node) {
        if (!relativeIDs.contains(node)) relativeIDs.add(node);
        return this;
    }

    public int getRestoreFood() {
        return restoreFood;
    }

    public Food setRestoreFood(int restoreFood) {
        this.restoreFood = restoreFood;
        return this;
    }

    public float getRestoreSaturation() {
        return restoreSaturation;
    }

    public Food setRestoreSaturation(float restoreSaturation) {
        this.restoreSaturation = restoreSaturation;
        return this;
    }

    class NodeIDMeta {
        int id;
        int meta;

        NodeIDMeta(int id, int meta) {
            this.id = id;
            this.meta = meta;
        }
    }

}
