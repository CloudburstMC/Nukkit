package cn.nukkit.item.randomitem;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Dyes;
import cn.nukkit.utils.Potions;

import java.util.*;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.item.randomitem in project nukkit.
 */
public final class RandomItem {
    private static Map<Selector, Float> selectors = new HashMap<>();

    public static final Selector ROOT = new Selector(null);

    public static final Selector ROOT_FISHING = registerSelector(new Selector(ROOT));

    public static final Selector FISHES = registerSelector(new Selector(ROOT_FISHING), 0.85F);
    public static final Selector TREASURES = registerSelector(new Selector(ROOT_FISHING), 0.05F);
    public static final Selector JUNKS = registerSelector(new Selector(ROOT_FISHING), 0.1F);
    public static final Selector FISH = registerSelector(new ConstantItemSelector(Item.RAW_FISH, FISHES), 0.6F);
    public static final Selector SALMON = registerSelector(new ConstantItemSelector(Item.RAW_SALMON, FISHES), 0.25F);
    public static final Selector CLOWNFISH = registerSelector(new ConstantItemSelector(Item.CLOWNFISH, FISHES), 0.02F);
    public static final Selector PUFFERFISH = registerSelector(new ConstantItemSelector(Item.PUFFERFISH, FISHES), 0.13F);
    public static final Selector TREASURE_BOW = registerSelector(new ConstantItemSelector(Item.BOW, TREASURES), 0.1667F);
    //public static final Selector TREASURE_ENCHANTED_BOOK = registerSelector(Item.get(Item.ENCHANTED_BOOK, TREASURES),  0.1667F);
    public static final Selector TREASURE_FISHING_ROD = registerSelector(new ConstantItemSelector(Item.FISHING_ROD, TREASURES), 0.1667F);
    //public static final Selector TREASURE_NAME_TAG = registerSelector(Item.get(Item.NAME_TAG, TREASURES), 0.1667F);
    //public static final Selector TREASURE_SADDLE = registerSelector(Item.get(Item.SADDLE, TREASURES), 0.1667F);
    public static final Selector JUNK_BOWL = registerSelector(new ConstantItemSelector(Item.BOWL, JUNKS), 0.12F);
    public static final Selector JUNK_FISHING_ROD = registerSelector(new ConstantItemSelector(Item.FISHING_ROD, JUNKS), 0.024F);
    public static final Selector JUNK_LEATHER = registerSelector(new ConstantItemSelector(Item.LEATHER, JUNKS), 0.12F);
    public static final Selector JUNK_LEATHER_BOOTS = registerSelector(new ConstantItemSelector(Item.LEATHER_BOOTS, JUNKS), 0.12F);
    public static final Selector JUNK_ROTTEN_FLESH = registerSelector(new ConstantItemSelector(Item.ROTTEN_FLESH, JUNKS), 0.12F);
    public static final Selector JUNK_STICK = registerSelector(new ConstantItemSelector(Item.STICK, JUNKS), 0.06F);
    public static final Selector JUNK_STRING_ITEM = registerSelector(new ConstantItemSelector(Item.STRING, JUNKS), 0.06F);
    public static final Selector JUNK_WATTER_BOTTLE = registerSelector(new ConstantItemSelector(Item.POTION, Potions.NO_EFFECTS, JUNKS), 0.12F);
    public static final Selector JUNK_BONE = registerSelector(new ConstantItemSelector(Item.BONE, JUNKS), 0.12F);
    public static final Selector JUNK_INK_SAC = registerSelector(new ConstantItemSelector(Item.DYE, Dyes.BLACK, 10, JUNKS), 0.012F);
    public static final Selector JUNK_TRIPWIRE_HOOK = registerSelector(new ConstantItemSelector(Item.TRIPWIRE_HOOK, JUNKS), 0.12F);

    public static Selector registerSelector(Selector selector) {
        return registerSelector(selector, 1);
    }

    public static Selector registerSelector(Selector selector, float chance) {
        if (selector.getParent() == null) selector.setParent(ROOT);
        selectors.put(selector, chance);
        return selector;
    }

    public static Item getRandomItem() {
        Object result = selectFrom(ROOT_FISHING);
        if (result instanceof Item) return (Item) result;
        return null;
    }

    static Object selectFrom(Selector selector) {
        Objects.requireNonNull(selector);
        Map<Selector, Float> child = new HashMap<>();
        selectors.forEach((s, f) -> {
            if (s.getParent() == selector) child.put(s, f);
        });
        if (child.size() == 0) return selector.select();
        return selectFrom(Selector.selectRandom(child));
    }

}
