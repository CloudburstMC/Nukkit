package cn.nukkit;

import cn.nukkit.network.protocol.UpdateAbilitiesPacket;
import cn.nukkit.network.protocol.UpdateAdventureSettingsPacket;
import cn.nukkit.network.protocol.types.AbilityLayer;
import cn.nukkit.network.protocol.types.PlayerAbility;

import java.util.EnumMap;
import java.util.Map;

/**
 * Adventure settings
 *
 * @author MagicDroidX
 * Nukkit Project
 */
public class AdventureSettings implements Cloneable {

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;

    private final Map<Type, Boolean> values = new EnumMap<>(Type.class);

    private Player player;

    public AdventureSettings(Player player) {
        this.player = player;
    }

    public AdventureSettings clone(Player newPlayer) {
        try {
            AdventureSettings settings = (AdventureSettings) super.clone();
            settings.player = newPlayer;
            return settings;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Set an adventure setting value
     *
     * @param type adventure setting
     * @param value new value
     * @return AdventureSettings
     */
    public AdventureSettings set(Type type, boolean value) {
        this.values.put(type, value);
        return this;
    }

    /**
     * Get an adventure setting value
     *
     * @param type adventure setting
     * @return value
     */
    public boolean get(Type type) {
        Boolean value = this.values.get(type);
        return value == null ? type.getDefaultValue() : value;
    }

    /**
     * Send adventure settings values to the player
     */
    public void update() {
        this.update(true);
    }

    /**
     * Send adventure settings values to the player
     * @param reset reset in air ticks
     */
    void update(boolean reset) {
        UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        packet.setEntityId(player.getId());
        packet.setCommandPermission(player.isOp() ? UpdateAbilitiesPacket.CommandPermission.OPERATOR : UpdateAbilitiesPacket.CommandPermission.NORMAL);
        packet.setPlayerPermission(player.isOp() && !player.isSpectator() ? UpdateAbilitiesPacket.PlayerPermission.OPERATOR : UpdateAbilitiesPacket.PlayerPermission.MEMBER); // Spectator: fix operators being able to break blocks on spectator mode

        AbilityLayer layer = new AbilityLayer();
        layer.setLayerType(AbilityLayer.Type.BASE);
        layer.getAbilitiesSet().addAll(PlayerAbility.VALUES);

        for (Type type : Type.values()) {
            if (type.isAbility() && this.get(type)) {
                layer.getAbilityValues().add(type.getAbility());
            }
        }

        // Because we send speed
        layer.getAbilityValues().add(PlayerAbility.WALK_SPEED);
        layer.getAbilityValues().add(PlayerAbility.FLY_SPEED);
        layer.getAbilityValues().add(PlayerAbility.VERTICAL_FLY_SPEED);

        if (player.isCreative()) { // Make sure player can interact with creative menu
            layer.getAbilityValues().add(PlayerAbility.INSTABUILD);
        }

        if (player.isOp()) {
            layer.getAbilityValues().add(PlayerAbility.OPERATOR_COMMANDS);
        }

        layer.setWalkSpeed(player.getWalkSpeed());
        layer.setFlySpeed(player.getFlySpeed());
        layer.setVerticalFlySpeed(player.getVerticalFlySpeed());
        packet.getAbilityLayers().add(layer);

        if (player.isSpectator()) {
            AbilityLayer spectator = new AbilityLayer();
            spectator.setLayerType(AbilityLayer.Type.SPECTATOR);

            spectator.getAbilitiesSet().addAll(PlayerAbility.VALUES);
            spectator.getAbilitiesSet().remove(PlayerAbility.FLY_SPEED);
            spectator.getAbilitiesSet().remove(PlayerAbility.WALK_SPEED);
            spectator.getAbilitiesSet().remove(PlayerAbility.VERTICAL_FLY_SPEED);

            for (Type type : Type.values()) {
                if (type.isAbility() && this.get(type)) {
                    spectator.getAbilityValues().add(type.getAbility());
                }
            }

            if (player.isOp()) {
                layer.getAbilityValues().add(PlayerAbility.OPERATOR_COMMANDS);
            }

            packet.getAbilityLayers().add(spectator);
        }

        UpdateAdventureSettingsPacket adventurePacket = new UpdateAdventureSettingsPacket();
        adventurePacket.setAutoJump(get(Type.AUTO_JUMP));
        adventurePacket.setImmutableWorld(get(Type.WORLD_IMMUTABLE));
        adventurePacket.setNoMvP(get(Type.NO_MVP));
        adventurePacket.setNoPvM(get(Type.NO_PVM));
        adventurePacket.setShowNameTags(get(Type.SHOW_NAME_TAGS));

        player.dataPacket(packet);
        player.dataPacket(adventurePacket);

        if (reset) {
            player.resetInAirTicks();
        }
    }

    /**
     * List of adventure settings
     */
    public enum Type {
        WORLD_IMMUTABLE(null, false),
        NO_PVM(null, false),
        NO_MVP(PlayerAbility.INVULNERABLE, false),
        SHOW_NAME_TAGS(null, false),
        AUTO_JUMP(null, true),
        ALLOW_FLIGHT(PlayerAbility.MAY_FLY, false),
        NO_CLIP(PlayerAbility.NO_CLIP, false),
        WORLD_BUILDER(PlayerAbility.WORLD_BUILDER, false),
        FLYING(PlayerAbility.FLYING, false),
        MUTED(PlayerAbility.MUTED, false),
        MINE(PlayerAbility.MINE, true),
        DOORS_AND_SWITCHED(PlayerAbility.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(PlayerAbility.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(PlayerAbility.ATTACK_PLAYERS, true),
        ATTACK_MOBS(PlayerAbility.ATTACK_MOBS, true),
        OPERATOR(PlayerAbility.OPERATOR_COMMANDS, false),
        TELEPORT(PlayerAbility.TELEPORT, false),
        BUILD(PlayerAbility.BUILD, true),
        PRIVILEGED_BUILDER(PlayerAbility.PRIVILEGED_BUILDER, false),

        @Deprecated
        DEFAULT_LEVEL_PERMISSIONS(null, false);

        private final PlayerAbility ability;
        private final boolean defaultValue;

        Type(PlayerAbility ability, boolean defaultValue) {
            this.ability = ability;
            this.defaultValue = defaultValue;
        }

        /**
         * Get default value
         *
         * @return default value
         */
        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        /**
         * Get player ability type
         *
         * @return player ability type
         */
        public PlayerAbility getAbility() {
            return this.ability;
        }

        /**
         * Check whether adventure setting is a valid player ability
         *
         * @return is a valid player ability
         */
        public boolean isAbility() {
            return this.ability != null;
        }
    }
}
