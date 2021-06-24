package cn.nukkit.level;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.Optional;

public enum GameRule {

    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled"),
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    DO_INSOMNIA("doInsomnia"),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn"),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    @Since("FUTURE") FREEZE_DAMAGE("freezeDamage"),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit"),
    KEEP_INVENTORY("keepInventory"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    MOB_GRIEFING("mobGriefing"),
    NATURAL_REGENERATION("naturalRegeneration"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomTickSpeed"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    SHOW_COORDINATES("showCoordinates"),
    @Since("FUTURE") SHOW_DEATH_MESSAGES("showDeathMessages"),
    @PowerNukkitOnly @Deprecated 
    @DeprecationDetails(since = "FUTURE", 
            reason = "Added by upstream with a different name",
            replaceWith = "SHOW_DEATH_MESSAGES")
    SHOW_DEATH_MESSAGE(SHOW_DEATH_MESSAGES.name),
    SPAWN_RADIUS("spawnRadius"),
    TNT_EXPLODES("tntExplodes"),
    EXPERIMENTAL_GAMEPLAY("experimentalGameplay"),
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
