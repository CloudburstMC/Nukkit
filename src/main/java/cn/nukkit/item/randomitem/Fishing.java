package cn.nukkit.item.randomitem;

import cn.nukkit.item.Dye;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.potion.Potion;

import static cn.nukkit.item.randomitem.RandomItem.*;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.item.randomitem in project nukkit.
 */
public final class Fishing {

    public static final Selector ROOT_FISHING = putSelector(new Selector(ROOT));

    public static final Selector FISHES = putSelector(new Selector(ROOT_FISHING), 0.85F);
    public static final Selector TREASURES = putSelector(new Selector(ROOT_FISHING), 0.05F);
    public static final Selector JUNKS = putSelector(new Selector(ROOT_FISHING), 0.1F);
    public static final Selector FISH = putSelector(new ConstantItemSelector(Item.RAW_FISH, FISHES), 0.6F);
    public static final Selector SALMON = putSelector(new ConstantItemSelector(Item.RAW_SALMON, FISHES), 0.25F);
    public static final Selector CLOWNFISH = putSelector(new ConstantItemSelector(Item.CLOWNFISH, FISHES), 0.02F);
    public static final Selector PUFFERFISH = putSelector(new ConstantItemSelector(Item.PUFFERFISH, FISHES), 0.13F);
    public static final Selector TREASURE_BOW = putSelector(new ConstantItemSelector(Item.BOW, TREASURES), 0.1667F);
    //public static final Selector TREASURE_ENCHANTED_BOOK = putSelector(Item.get(Item.ENCHANTED_BOOK, TREASURES),  0.1667F);
    public static final Selector TREASURE_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, TREASURES), 0.1667F);
    //public static final Selector TREASURE_NAME_TAG = putSelector(Item.get(Item.NAME_TAG, TREASURES), 0.1667F);
    //public static final Selector TREASURE_SADDLE = putSelector(Item.get(Item.SADDLE, TREASURES), 0.1667F);
    public static final Selector JUNK_BOWL = putSelector(new ConstantItemSelector(Item.BOWL, JUNKS), 0.12F);
    public static final Selector JUNK_FISHING_ROD = putSelector(new ConstantItemSelector(Item.FISHING_ROD, JUNKS), 0.024F);
    public static final Selector JUNK_LEATHER = putSelector(new ConstantItemSelector(Item.LEATHER, JUNKS), 0.12F);
    public static final Selector JUNK_LEATHER_BOOTS = putSelector(new ConstantItemSelector(Item.LEATHER_BOOTS, JUNKS), 0.12F);
    public static final Selector JUNK_ROTTEN_FLESH = putSelector(new ConstantItemSelector(Item.ROTTEN_FLESH, JUNKS), 0.12F);
    public static final Selector JUNK_STICK = putSelector(new ConstantItemSelector(Item.STICK, JUNKS), 0.06F);
    public static final Selector JUNK_STRING_ITEM = putSelector(new ConstantItemSelector(Item.STRING, JUNKS), 0.06F);
    public static final Selector JUNK_WATTER_BOTTLE = putSelector(new ConstantItemSelector(Item.POTION, Potion.NO_EFFECTS, JUNKS), 0.12F);
    public static final Selector JUNK_BONE = putSelector(new ConstantItemSelector(Item.BONE, JUNKS), 0.12F);
    public static final Selector JUNK_INK_SAC = putSelector(new ConstantItemSelector(Item.DYE, Dye.BLACK, 10, JUNKS), 0.012F);
    public static final Selector JUNK_TRIPWIRE_HOOK = putSelector(new ConstantItemSelector(Item.TRIPWIRE_HOOK, JUNKS), 0.12F);

    public static Item getFishingResult(Item rod) {
        int fortuneLevel = 0;
        int lureLevel = 0;
        if (rod != null) {
            fortuneLevel = rod.getEnchantment(Enchantment.TYPE_FISHING_FORTUNE).getLevel();
            lureLevel = rod.getEnchantment(Enchantment.TYPE_FISHING_LURE).getLevel();
        }
        return getFishingResult(fortuneLevel, lureLevel);
    }

    public static Item getFishingResult(int fortuneLevel, int lureLevel) {
        float treasureChance = limitRange(0, 1, 0.05f + 0.01f * fortuneLevel - 0.01f * lureLevel);
        float junkChance = limitRange(0, 1, 0.05f - 0.025f * fortuneLevel - 0.01f * lureLevel);
        float fishChance = limitRange(0, 1, 1 - treasureChance - junkChance);
        putSelector(FISHES, fishChance);
        putSelector(TREASURES, treasureChance);
        putSelector(JUNKS, junkChance);
        Object result = selectFrom(ROOT_FISHING);
        if (result instanceof Item) return (Item) result;
        return null;
    }

    private static float limitRange(float min, float max, float value) {
        if (value >= max) return max;
        if (value <= min) return min;
        return value;
    }

}
