package cn.nukkit;

import cn.nukkit.network.protocol.AdventureSettingsPacket;

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
        AdventureSettingsPacket pk = new AdventureSettingsPacket();
        for (Type t : Type.values()) {
            pk.setFlag(t.getId(), get(t));
        }

        pk.commandPermission = (player.isOp() ? AdventureSettingsPacket.PERMISSION_OPERATOR : AdventureSettingsPacket.PERMISSION_NORMAL);
        pk.playerPermission = (player.isOp() && !player.isSpectator() ? Player.PERMISSION_OPERATOR : Player.PERMISSION_MEMBER);
        pk.entityUniqueId = player.getId();

        Server.broadcastPacket(player.getViewers().values(), pk);
        player.dataPacket(pk);

        player.resetInAirTicks();
    }

    public enum Type {
        WORLD_IMMUTABLE(AdventureSettingsPacket.WORLD_IMMUTABLE, false),
        NO_PVM(AdventureSettingsPacket.NO_PVM, false),
        NO_MVP(AdventureSettingsPacket.NO_MVP, false),
        SHOW_NAME_TAGS(AdventureSettingsPacket.SHOW_NAME_TAGS, false),
        AUTO_JUMP(AdventureSettingsPacket.AUTO_JUMP, true),
        ALLOW_FLIGHT(AdventureSettingsPacket.ALLOW_FLIGHT, false),
        NO_CLIP(AdventureSettingsPacket.NO_CLIP, false),
        WORLD_BUILDER(AdventureSettingsPacket.WORLD_BUILDER, false),
        FLYING(AdventureSettingsPacket.FLYING, false),
        MUTED(AdventureSettingsPacket.MUTED, false),
        MINE(AdventureSettingsPacket.MINE, true),
        DOORS_AND_SWITCHED(AdventureSettingsPacket.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(AdventureSettingsPacket.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(AdventureSettingsPacket.ATTACK_PLAYERS, true),
        ATTACK_MOBS(AdventureSettingsPacket.ATTACK_MOBS, true),
        OPERATOR(AdventureSettingsPacket.OPERATOR, false),
        TELEPORT(AdventureSettingsPacket.TELEPORT, false),
        BUILD(AdventureSettingsPacket.BUILD, true),
        DEFAULT_LEVEL_PERMISSIONS(AdventureSettingsPacket.DEFAULT_LEVEL_PERMISSIONS, false);

        private final int id;
        private final boolean defaultValue;

        Type(int id, boolean defaultValue) {
            this.id = id;
            this.defaultValue = defaultValue;
        }

        public int getId() {
            return id;
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }
    }
}
