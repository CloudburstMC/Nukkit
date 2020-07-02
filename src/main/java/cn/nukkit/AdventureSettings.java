package cn.nukkit;

import cn.nukkit.player.Player;
import com.nukkitx.protocol.bedrock.data.AdventureSetting;
import com.nukkitx.protocol.bedrock.data.PlayerPermission;
import com.nukkitx.protocol.bedrock.data.command.CommandPermission;
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
                pk.getSettings().add(t.getSetting());
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
        WORLD_IMMUTABLE(AdventureSetting.WORLD_IMMUTABLE, false),
        NO_PVM(AdventureSetting.NO_PVM, false),
        NO_MVP(AdventureSetting.NO_MVP, false),
        SHOW_NAME_TAGS(AdventureSetting.SHOW_NAME_TAGS, true),
        AUTO_JUMP(AdventureSetting.AUTO_JUMP, true),
        ALLOW_FLIGHT(AdventureSetting.MAY_FLY, false),
        NO_CLIP(AdventureSetting.NO_CLIP, false),
        WORLD_BUILDER(AdventureSetting.WORLD_BUILDER, true),
        FLYING(AdventureSetting.FLYING, false),
        MUTED(AdventureSetting.MUTED, false),
        MINE(AdventureSetting.MINE, true),
        DOORS_AND_SWITCHED(AdventureSetting.DOORS_AND_SWITCHES, true),
        OPEN_CONTAINERS(AdventureSetting.OPEN_CONTAINERS, true),
        ATTACK_PLAYERS(AdventureSetting.ATTACK_PLAYERS, true),
        ATTACK_MOBS(AdventureSetting.ATTACK_MOBS, true),
        OPERATOR(AdventureSetting.OPERATOR, false),
        TELEPORT(AdventureSetting.TELEPORT, false),
        BUILD(AdventureSetting.BUILD, true);

        private final AdventureSetting flag;
        private final boolean defaultValue;

        Type(AdventureSetting flag, boolean defaultValue) {
            this.flag = flag;
            this.defaultValue = defaultValue;
        }

        public AdventureSetting getSetting() {
            return flag;
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }
    }
}
