package cn.nukkit.level;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Optional;

public enum GameRule {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn"),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    KEEP_INVENTORY("keepInventory"),
    MOB_GRIEFING("mobGriefing"),
    NATURAL_REGENERATION("naturalRegeneration"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomTickSpeed"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    SHOW_COORDINATES("showCoordinates"),
    TNT_EXPLODES("tntExplodes"),
    SHOW_DEATH_MESSAGE("showDeathMessages"),
    EXPERIMENTAL_GAMEPLAY("experimentalGameplay"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    DO_INSOMNIA("doInsomnia"),
    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled"),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit"),
    SPAWN_RADIUS("spawnRadius"),
    SHOW_TAGS("showTags");

    private final String name;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final GameRule[] EMPTY_ARRAY = new GameRule[0];

    GameRule(String name) {
        this.name = name;
    }

    public static Optional<GameRule> parseString(String gameRuleString) {
        //Backward compatibility
        if ("showDeathMessage".equalsIgnoreCase(gameRuleString)) {
            gameRuleString = "showDeathMessages";
        }

        for (GameRule gameRule: values()) {
            if (gameRule.getName().equalsIgnoreCase(gameRuleString)) {
                return Optional.of(gameRule);
            }
        }
        return Optional.empty();
    }

    public static String[] getNames() {
        String[] stringValues = new String[values().length];

        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].getName();
        }
        return stringValues;
    }

    public String getName() {
        return name;
    }
}
