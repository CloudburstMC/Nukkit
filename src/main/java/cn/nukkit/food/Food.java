package cn.nukkit.food;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Effect;
import cn.nukkit.event.player.PlayerEatFoodEvent;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;

import java.util.*;

/**
 * Created by Snake1999 on 2016/1/13.
 * Package cn.nukkit.food in project nukkit.
 */
public abstract class Food {

    private static Map<NodeIDMetaPlugin, Food> registryCustom = new LinkedHashMap<>();
    private static Map<NodeIDMeta, Food> registryDefault = new LinkedHashMap<>();

    //some of following are not available yet in MCPE 0.13.1
    public static final Food apple = registerDefaultFood(new FoodNormal(4, 2.4F).addRelative(Item.APPLE));
    public static final Food apple_golden = registerDefaultFood(new FoodEffective(4, 9.6F)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(5 * 20))
            .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20))
            .addRelative(Item.GOLDEN_APPLE));
    public static final Food apple_golden_enchanted = registerDefaultFood(new FoodEffective(4, 9.6F)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(4).setDuration(30 * 20))
            .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20))
            .addEffect(Effect.getEffect(Effect.DAMAGE_RESISTANCE).setDuration(5 * 60 * 20))
            .addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(5 * 60 * 20))
            .addRelative(Item.GOLDEN_APPLE_ENCHANTED));
    public static final Food beef_raw = registerDefaultFood(new FoodNormal(3, 1.8F).addRelative(Item.RAW_BEEF));
    public static final Food beetroot = registerDefaultFood(new FoodNormal(1, 1.2F).addRelative(Item.BEETROOT));
    public static final Food beetroot_soup = registerDefaultFood(new FoodNormal(6, 7.2F).addRelative(Item.BEETROOT_SOUP));
    public static final Food bread = registerDefaultFood(new FoodNormal(5, 6F).addRelative(Item.BREAD));
    public static final Food cake_slice = registerDefaultFood(new FoodNormal(2, 0.4F)
            .addRelative(Block.CAKE_BLOCK, 0).addRelative(Block.CAKE_BLOCK, 1).addRelative(Block.CAKE_BLOCK, 2)
            .addRelative(Block.CAKE_BLOCK, 3).addRelative(Block.CAKE_BLOCK, 4).addRelative(Block.CAKE_BLOCK, 5)
            .addRelative(Block.CAKE_BLOCK, 6));
    public static final Food carrot = registerDefaultFood(new FoodNormal(3, 4.8F).addRelative(Item.CARROT));
    public static final Food carrot_golden = registerDefaultFood(new FoodNormal(6, 14.4F).addRelative(Item.GOLDEN_CARROT));
    public static final Food chicken_raw = registerDefaultFood(new FoodEffective(2, 1.2F)
            .addChanceEffect(0.3F, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
            .addRelative(Item.RAW_CHICKEN));
    public static final Food chicken_cooked = registerDefaultFood(new FoodNormal(6, 7.2F).addRelative(Item.COOKED_CHICKEN));
    public static final Food chorus_fruit = registerDefaultFood(new FoodNormal(4, 2.4F));
    public static final Food cookie = registerDefaultFood(new FoodNormal(2, 0.4F).addRelative(Item.COOKIE));
    public static final Food melon_slice = registerDefaultFood(new FoodNormal(2, 1.2F).addRelative(Item.MELON_SLICE));
    public static final Food mushroom_stew = registerDefaultFood(new FoodInBowl(6, 7.2F).addRelative(Item.MUSHROOM_STEW));
    public static final Food mutton_cooked = registerDefaultFood(new FoodNormal(6, 9.6F));
    public static final Food mutton_raw = registerDefaultFood(new FoodNormal(2, 1.2F));
    public static final Food porkchop_cooked = registerDefaultFood(new FoodNormal(8, 12.8F).addRelative(Item.COOKED_PORKCHOP));
    public static final Food porkchop_raw = registerDefaultFood(new FoodNormal(3, 1.8F).addRelative(Item.RAW_PORKCHOP));
    public static final Food potato_raw = registerDefaultFood(new FoodNormal(1, 0.6F).addRelative(Item.POTATO));
    public static final Food potato_baked = registerDefaultFood(new FoodNormal(5, 7.2F).addRelative(Item.BAKED_POTATO));
    public static final Food potato_poisonous = registerDefaultFood(new FoodEffective(2, 1.2F)
            .addChanceEffect(0.6F, Effect.getEffect(Effect.POISON).setDuration(4 * 20))
            .addRelative(Item.POISONOUS_POTATO));
    public static final Food pumpkin_pie = registerDefaultFood(new FoodNormal(8, 4.8F).addRelative(Item.PUMPKIN_PIE));
    public static final Food rabbit_cooked = registerDefaultFood(new FoodNormal(5, 6F).addRelative(Item.COOKED_RABBIT));
    public static final Food rabbit_raw = registerDefaultFood(new FoodNormal(3, 1.8F).addRelative(Item.RAW_RABBIT));
    public static final Food rabbit_stew = registerDefaultFood(new FoodInBowl(10, 12F).addRelative(Item.RABBIT_STEW));
    public static final Food rotten_flesh = registerDefaultFood(new FoodEffective(4, 0.8F)
            .addChanceEffect(0.8F, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
            .addRelative(Item.ROTTEN_FLESH));
    public static final Food spider_eye = registerDefaultFood(new FoodEffective(2, 3.2F)
            .addEffect(Effect.getEffect(Effect.POISON).setDuration(4 * 20))
            .addRelative(Item.SPIDER_EYE));
    public static final Food steak = registerDefaultFood(new FoodNormal(8, 12.8F).addRelative(Item.COOKED_BEEF));
    //different kinds of fishes
    public static final Food clownfish = registerDefaultFood(new FoodNormal(1, 0.2F).addRelative(Item.CLOWNFISH));
    public static final Food fish_cooked = registerDefaultFood(new FoodNormal(5, 6F).addRelative(Item.COOKED_FISH));
    public static final Food fish_raw = registerDefaultFood(new FoodNormal(2, 0.4F).addRelative(Item.RAW_FISH));
    public static final Food salmon_cooked = registerDefaultFood(new FoodNormal(6, 9.6F).addRelative(Item.COOKED_SALMON));
    public static final Food salmon_raw = registerDefaultFood(new FoodNormal(2, 0.4F).addRelative(Item.RAW_SALMON));
    public static final Food pufferfish = registerDefaultFood(new FoodEffective(1, 0.2F)
            .addEffect(Effect.getEffect(Effect.HUNGER).setAmplifier(2).setDuration(15 * 20))
            .addEffect(Effect.getEffect(Effect.NAUSEA).setAmplifier(1).setDuration(15 * 20))
            .addEffect(Effect.getEffect(Effect.POISON).setAmplifier(4).setDuration(60 * 20))
            .addRelative(Item.PUFFERFISH));

    //Opened API for plugins
    public static Food registerFood(Food food, Plugin plugin) {
        Objects.requireNonNull(food);
        Objects.requireNonNull(plugin);
        food.relativeIDs.forEach(n -> registryCustom.put(new NodeIDMetaPlugin(n.id, n.meta, plugin), food));
        return food;
    }

    private static Food registerDefaultFood(Food food) {
        food.relativeIDs.forEach(n -> registryDefault.put(n, food));
        return food;
    }

    public static Food getByRelative(Item item) {
        Objects.requireNonNull(item);
        return getByRelative(item.getId(), item.getDamage());
    }

    public static Food getByRelative(Block block) {
        Objects.requireNonNull(block);
        return getByRelative(block.getId(), block.getDamage());
    }

    public static Food getByRelative(int relativeID, int meta) {
        final Food[] result = {null};
        registryCustom.forEach((n, f) -> {
            if (n.id == relativeID && n.meta == meta && n.plugin.isEnabled()) result[0] = f;
        });
        if (result[0] == null) {
            registryDefault.forEach((n, f) -> {
                if (n.id == relativeID && n.meta == meta) result[0] = f;
            });
        }
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

    static class NodeIDMeta {
        int id;
        int meta;

        NodeIDMeta(int id, int meta) {
            this.id = id;
            this.meta = meta;
        }
    }

    static class NodeIDMetaPlugin extends NodeIDMeta {
        Plugin plugin;

        NodeIDMetaPlugin(int id, int meta, Plugin plugin) {
            super(id, meta);
            this.plugin = plugin;
        }
    }

}
