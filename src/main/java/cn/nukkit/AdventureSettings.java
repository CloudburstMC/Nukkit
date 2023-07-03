package cn.nukkit;

import cn.nukkit.network.protocol.UpdateAbilitiesPacket;
import cn.nukkit.network.protocol.UpdateAdventureSettingsPacket;
import cn.nukkit.network.protocol.types.AbilityLayer;
import cn.nukkit.network.protocol.types.PlayerAbility;

import java.util.EnumMap;
import java.util.Map;

/**
 * Nukkit Project
 * Author: MagicDroidX
 */
public class AdventureSettings implements Cloneable {

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;

    private Map<Type, Boolean> values = new EnumMap<>(Type.class);

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

    public AdventureSettings set(Type type, boolean value) {
        this.values.put(type, value);
        return this;
    }

    public boolean get(Type type) {
        Boolean value = this.values.get(type);
        return value == null ? type.getDefaultValue() : value;
    }

    public void update() {
        UpdateAbilitiesPacket packet = new UpdateAbilitiesPacket();
        packet.setEntityId(player.getId());
        packet.setCommandPermission(player.isOp() ? UpdateAbilitiesPacket.CommandPermission.OPERATOR : UpdateAbilitiesPacket.CommandPermission.NORMAL);
        packet.setPlayerPermission(player.isOp() && !player.isSpectator() ? UpdateAbilitiesPacket.PlayerPermission.OPERATOR : UpdateAbilitiesPacket.PlayerPermission.MEMBER);

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

        if (player.isCreative()) { // Make sure player can interact with creative menu
            layer.getAbilityValues().add(PlayerAbility.INSTABUILD);
        }

        if (player.isOp()) {
            layer.getAbilityValues().add(PlayerAbility.OPERATOR_COMMANDS);
        }

        layer.setWalkSpeed(Player.DEFAULT_SPEED);
        layer.setFlySpeed(Player.DEFAULT_FLY_SPEED);
        packet.getAbilityLayers().add(layer);

        if (this.get(Type.NO_CLIP)) {
            AbilityLayer layer2 = new AbilityLayer();
            layer2.setLayerType(AbilityLayer.Type.SPECTATOR);

            layer2.getAbilitiesSet().addAll(PlayerAbility.VALUES);
            layer2.getAbilitiesSet().remove(PlayerAbility.FLY_SPEED);
            layer2.getAbilitiesSet().remove(PlayerAbility.WALK_SPEED);

            layer2.getAbilityValues().add(PlayerAbility.FLYING);
            layer2.getAbilityValues().add(PlayerAbility.NO_CLIP);
            packet.getAbilityLayers().add(layer2);
        }

        UpdateAdventureSettingsPacket adventurePacket = new UpdateAdventureSettingsPacket();
        adventurePacket.setAutoJump(get(Type.AUTO_JUMP));
        adventurePacket.setImmutableWorld(get(Type.WORLD_IMMUTABLE));
        adventurePacket.setNoMvP(get(Type.NO_MVP));
        adventurePacket.setNoPvM(get(Type.NO_PVM));
        adventurePacket.setShowNameTags(get(Type.SHOW_NAME_TAGS));

        player.dataPacket(packet);
        player.dataPacket(adventurePacket);
        player.resetInAirTicks();
    }

    public enum Type {
        WORLD_IMMUTABLE(false),
        NO_PVM(false),
        NO_MVP(PlayerAbility.INVULNERABLE, false),
        SHOW_NAME_TAGS(false),
        AUTO_JUMP(true),
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

        Type(boolean defaultValue) {
            this.defaultValue = defaultValue;
            this.ability = null;
        }

        Type(PlayerAbility ability, boolean defaultValue) {
            this.ability = ability;
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        public PlayerAbility getAbility() {
            return this.ability;
        }

        public boolean isAbility() {
            return this.ability != null;
        }
    }
}
