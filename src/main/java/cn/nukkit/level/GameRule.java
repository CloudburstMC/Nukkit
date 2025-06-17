package cn.nukkit.level;

import java.util.Locale;
import java.util.Optional;

public enum GameRule {

    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled"),
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    DO_INSOMNIA("doInsomnia"),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn"),
    DO_LIMITED_CRAFTING("doLimitedCrafting"),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    FREEZE_DAMAGE("freezeDamage"),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit"),
    KEEP_INVENTORY("keepInventory"),
    LOCATOR_BAR("locatorBar"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    MOB_GRIEFING("mobGriefing"),
    NATURAL_REGENERATION("naturalRegeneration"),
    PLAYERS_SLEEPING_PERCENTAGE("playersSleepingPercentage"),
    PROJECTILES_CAN_BREAK_BLOCKS("projectilesCanBreakBlocks"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomTickSpeed"),
    RECIPES_UNLOCK("recipesUnlock"),
    RESPAWN_BLOCKS_EXPLODE("respawnBlocksExplode"),
    SHOW_BORDER_EFFECT("showBorderEffect"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    SHOW_COORDINATES("showCoordinates"),
    SHOW_DAYS_PLAYED("showDaysPlayed"),
    SHOW_DEATH_MESSAGES("showDeathMessages"),
    SHOW_RECIPE_MESSAGES("showRecipeMessages"),
    SHOW_TAGS("showTags"),
    SPAWN_RADIUS("spawnRadius"),
    TNT_EXPLODES("tntExplodes"),
    TNT_EXPLOSION_DROP_DECAY("tntExplosionDropDecay");

    private final String name;

    GameRule(String name) {
        this.name = name;
    }

    public static Optional<GameRule> parseString(String gameRuleString) {
        for (GameRule gameRule: values()) {
            if (gameRule.name.equalsIgnoreCase(gameRuleString)) {
                return Optional.of(gameRule);
            }
        }
        return Optional.empty();
    }

    public static String[] getNames() {
        String[] stringValues = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].name;
        }
        return stringValues;
    }

    public static String[] getNamesLowerCase() {
        String[] stringValues = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].name.toLowerCase(Locale.ROOT);
        }
        return stringValues;
    }

    public String getName() {
        return name;
    }
}
