package cn.nukkit;

import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.CommandPermission;
import com.nukkitx.protocol.bedrock.data.PlayerPermission;
import com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket;

import java.util.EnumMap;
import java.util.Map;

/**
 * Nukkit Project
 * Author: MagicDroidX
 */
public class AdventureSettings implements Cloneable {

    private final Map<Type, Boolean> values = new EnumMap<>(Type.class);

    private Player player;

    public AdventureSettings(Player player) {
        this.player = player;
    }

    public AdventureSettings(Player player, Map<Type, Boolean> values) {
        this.player = player;
        this.values.putAll(values);
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
            if (get(t)) {
                pk.getFlags().add(t.getFlag());
            }
        }

        pk.setCommandPermission((player.isOp() ? CommandPermission.OPERATOR : CommandPermission.NORMAL));
        pk.setPlayerPermission((player.isOp() ? PlayerPermission.OPERATOR : PlayerPermission.MEMBER));
        pk.setUniqueEntityId(player.getUniqueId());

        Server.broadcastPacket(player.getViewers(), pk);
        player.sendPacket(pk);

        player.resetInAirTicks();
    }

    public enum Type {
        WORLD_IMMUTABLE(AdventureSettingsPacket.Flag.IMMUTABLE_WORLD, false),
        AUTO_JUMP(AdventureSettingsPacket.Flag.AUTO_JUMP, true),
        ALLOW_FLIGHT(AdventureSettingsPacket.Flag.MAY_FLY, false),
        NO_CLIP(AdventureSettingsPacket.Flag.NO_CLIP, false),
        WORLD_BUILDER(AdventureSettingsPacket.Flag.WORLD_BUILDER, true),
        FLYING(AdventureSettingsPacket.Flag.FLYING, false),
        MUTED(AdventureSettingsPacket.Flag.MUTE, false),
        BUILD_AND_MINE(AdventureSettingsPacket.Flag.MINE, true),
        DOORS_AND_SWITCHED(AdventureSettingsPacket.Flag.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(AdventureSettingsPacket.Flag.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(AdventureSettingsPacket.Flag.ATTACK_PLAYERS, true),
        ATTACK_MOBS(AdventureSettingsPacket.Flag.ATTACK_MOBS, true),
        OPERATOR(AdventureSettingsPacket.Flag.OP, false),
        TELEPORT(AdventureSettingsPacket.Flag.TELEPORT, false);

        private final AdventureSettingsPacket.Flag flag;
        private final boolean defaultValue;

        Type(AdventureSettingsPacket.Flag flag, boolean defaultValue) {
            this.flag = flag;
            this.defaultValue = defaultValue;
        }

        public AdventureSettingsPacket.Flag getFlag() {
            return flag;
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }
    }
}
