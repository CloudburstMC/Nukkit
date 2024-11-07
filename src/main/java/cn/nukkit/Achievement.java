package cn.nukkit;

import cn.nukkit.utils.TextFormat;

import java.util.HashMap;

/**
 * Achievement list and functions
 *
 * @author CreeperFace
 */
public class Achievement {

    /**
     * All known achievements.
     * <p>
     * Based on <a href="https://minecraft.fandom.com/wiki/Achievement/Java_Edition#List_of_achievements">...</a>
     */
    public static final HashMap<String, Achievement> achievements = new HashMap<String, Achievement>() {
        {
            put("openInventory", new Achievement("Taking Inventory"));
            put("mineWood", new Achievement("Getting Wood", "openInventory"));
            put("buildWorkBench", new Achievement("Benchmarking", "mineWood"));
            put("buildPickaxe", new Achievement("Time to Mine!", "buildWorkBench"));
            put("buildFurnace", new Achievement("Hot Topic", "buildPickaxe"));
            put("acquireIron", new Achievement("Acquire Hardware", "buildFurnace"));
            put("buildHoe", new Achievement("Time to Farm!", "buildWorkBench"));
            put("makeBread", new Achievement("Bake Bread", "buildHoe"));
            put("bakeCake", new Achievement("The Lie", "buildHoe"));
            put("buildBetterPickaxe", new Achievement("Getting an Upgrade", "buildPickaxe"));
            put("cookFish", new Achievement("Delicious Fish", "buildFurnace"));
            put("onARail", new Achievement("On A Rail", "acquireIron"));
            put("buildSword", new Achievement("Time to Strike!", "buildWorkBench"));
            put("killEnemy", new Achievement("Monster Hunter", "buildSword"));
            put("killCow", new Achievement("Cow Tipper", "buildSword"));
            put("flyPig", new Achievement("When Pigs Fly", "killCow"));
            put("snipeSkeleton", new Achievement("Sniper Duel", "killEnemy"));
            put("diamonds", new Achievement("DIAMONDS!", "acquireIron"));
            put("portal", new Achievement("We Need to Go Deeper", "diamonds"));
            put("ghast", new Achievement("Return to Sender", "portal"));
            put("blazeRod", new Achievement("Into Fire", "portal"));
            put("potion", new Achievement("Local Brewery", "blazeRod"));
            put("theEnd", new Achievement("The End?", "blazeRod"));
            put("theEnd2", new Achievement("The End.", "theEnd"));
            put("enchantments", new Achievement("Enchanter", "diamonds"));
            put("overkill", new Achievement("Overkill", "enchantments"));
            put("bookcase", new Achievement("Librarian", "enchantments"));
            put("exploreAllBiomes", new Achievement("Adventuring Time", "theEnd")); //TODO
            put("spawnWither", new Achievement("The Beginning?", "theEnd"));
            put("killWither", new Achievement("The Beginning.", "spawnWither"));
            put("fullBeacon", new Achievement("Beaconator", "killWither"));
            put("breedCow", new Achievement("Repopulation", "killCow"));
            put("diamondsToYou", new Achievement("Diamonds to you!", "diamonds"));
            put("overpowered", new Achievement("Overpowered", "buildBetterPickaxe"));
        }
    };

    /**
     * Broadcasts achievement get message if player does not have the achievement yet. Returns true if broadcast.
     */
    public static boolean broadcast(Player player, String achievementId) {
        if (!achievements.containsKey(achievementId)) {
            return false;
        }

        String translation = TextFormat.WHITE + Server.getInstance().getLanguage().translateString("chat.type.achievement", player.getDisplayName(), TextFormat.GREEN + "[" + achievements.get(achievementId).message + "]", null);

        if (Server.getInstance().announceAchievements) {
            Server.getInstance().broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
        return true;
    }

    /**
     * Register an achievement
     * @param name save id
     * @param achievement achievement
     * @return true if successful, false if save id is already in use
     */
    public static boolean add(String name, Achievement achievement) {
        if (achievements.containsKey(name)) {
            return false;
        }

        achievements.put(name, achievement);
        return true;
    }

    public final String message;
    public final String[] requires;

    /**
     * @param message achievement name displayed in achievement get message
     * @param requires save IDs of achievements player must complete before this achievement can be completed
     */
    public Achievement(String message, String... requires) {
        this.message = message;
        this.requires = requires;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Broadcasts achievement get message
     */
    public void broadcast(Player player) {
        String translation = TextFormat.WHITE + Server.getInstance().getLanguage().translateString("chat.type.achievement", player.getDisplayName(), TextFormat.GREEN + "[" + this.message + "]", null);

        if (Server.getInstance().announceAchievements) {
            Server.getInstance().broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
    }
}
