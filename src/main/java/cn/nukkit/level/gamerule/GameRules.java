package cn.nukkit.level.gamerule;


public class GameRules {

    public static final BooleanGameRule COMMAND_BLOCK_OUTPUT = BooleanGameRule.of("commandblockoutput", true);
    public static final BooleanGameRule DO_DAYLIGHT_CYCLE = BooleanGameRule.of("dodaylightcycle", true);
    public static final BooleanGameRule DO_ENTITY_DROPS = BooleanGameRule.of("doentitydrops", true);
    public static final BooleanGameRule DO_FIRE_TICK = BooleanGameRule.of("dofiretick", true);
    public static final BooleanGameRule DO_MOB_LOOT = BooleanGameRule.of("domobloot", true);
    public static final BooleanGameRule DO_MOB_SPAWNING = BooleanGameRule.of("domobspawning", true);
    public static final BooleanGameRule DO_TILE_DROPS = BooleanGameRule.of("dotiledrops", true);
    public static final BooleanGameRule DO_WEATHER_CYCLE = BooleanGameRule.of("doweathercycle", true);
    public static final BooleanGameRule DROWNING_DAMAGE = BooleanGameRule.of("drowningdamage", true);
    public static final BooleanGameRule FALL_DAMAGE = BooleanGameRule.of("falldamage", true);
    public static final BooleanGameRule FIRE_DAMAGE = BooleanGameRule.of("firedamage", true);
    public static final BooleanGameRule KEEP_INVENTORY = BooleanGameRule.of("keepinventory", false);
    public static final BooleanGameRule MOB_GRIEFING = BooleanGameRule.of("mobgriefing", true);
    public static final BooleanGameRule PVP = BooleanGameRule.of("pvp", true);
    public static final BooleanGameRule SHOW_COORDINATES = BooleanGameRule.of("showcoordinates", true);
    public static final BooleanGameRule NATURAL_REGENERATION = BooleanGameRule.of("naturalregeneration", true);
    public static final BooleanGameRule TNT_EXPLODES = BooleanGameRule.of("tntexplodes", true);
    public static final BooleanGameRule SEND_COMMAND_FEEDBACK = BooleanGameRule.of("sendcommandfeedback", true);
    public static final BooleanGameRule EXPERIMENTAL_GAME_PLAY = BooleanGameRule.of("experimentalgameplay", false);
    public static final IntegerGameRule MAX_COMMAND_CHAIN_LENGTH = IntegerGameRule.of("", 131070);
    public static final BooleanGameRule DO_INSOMNIA = BooleanGameRule.of("doinsomnia", true);
    public static final BooleanGameRule COMMAND_BLOCKS_ENABLED = BooleanGameRule.of("commandblocksenabled", true);
    public static final IntegerGameRule RANDOM_TICK_SPEED = IntegerGameRule.of("randomtickspeed", 2);
    public static final BooleanGameRule DO_IMMEDIATE_RESPAWN = BooleanGameRule.of("doimmediaterespawn", false);
    public static final BooleanGameRule SHOW_DEATH_MESSAGES = BooleanGameRule.of("showdeathmessages", true);
    public static final IntegerGameRule FUNCTION_COMMAND_LIMIT = IntegerGameRule.of("functioncommandlimit", 20000);
    public static final IntegerGameRule SPAWN_RADIUS = IntegerGameRule.of("spawnradius", 0);

    private GameRules() {
        throw new AssertionError("This class cannot be instantiated");
    }
}
